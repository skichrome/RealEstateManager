package com.skichrome.realestatemanager.viewmodel

import android.util.Log
import androidx.databinding.ObservableField
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.skichrome.realestatemanager.model.RealtyRepository
import com.skichrome.realestatemanager.model.database.*
import com.skichrome.realestatemanager.model.database.minimalobj.RealtyMinimalForMap
import com.skichrome.realestatemanager.model.database.minimalobj.RealtyPreviewExtras
import com.skichrome.realestatemanager.utils.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

class RealtyViewModel(private val repository: RealtyRepository) : ViewModel()
{
    // =================================
    //              Fields
    // =================================

    private val viewModelJob = SupervisorJob()
    private val uiScope = CoroutineScope(Dispatchers.Default + viewModelJob)

    private val _realEstates = MutableLiveData<List<Realty>>() // can be observed from fragment
    val realEstates: LiveData<List<Realty>>
        get() = _realEstates

    private val _isLoading = ObservableField<Boolean>(false) // can be observed from data binding
    val isLoading: ObservableField<Boolean>
        get() = _isLoading

    private val _insertLoading = MutableLiveData<Boolean?>()
    val insertLoading: LiveData<Boolean?>
        get() = _insertLoading

    private val _realtyDetailedLoading = MutableLiveData<Boolean?>()
    val realtyDetailedLoading: LiveData<Boolean?>
        get() = _realtyDetailedLoading

    private val _poi = MutableLiveData<List<Poi>>()
    val poi: LiveData<List<Poi>>
        get() = _poi

    private val _realtyTypes = MutableLiveData<List<RealtyType>>()
    val realtyTypes: LiveData<List<RealtyType>>
        get() = _realtyTypes

    private val _agent = MutableLiveData<List<Agent>>()
    val agent: LiveData<List<Agent>>
        get() = _agent

    private val _realtyDetailed = ObservableField<Realty?>()
    val realtyDetailed: ObservableField<Realty?>
        get() = _realtyDetailed

    private val _realtyDetailedLatLng = MutableLiveData<RealtyMinimalForMap>()
    val realtyDetailedLatLng: LiveData<RealtyMinimalForMap>
        get() = _realtyDetailedLatLng

    private val _realtyDetailedPhotos = MutableLiveData<List<MediaReference>>()
    val realtyDetailedPhotos: LiveData<List<MediaReference>>
        get() = _realtyDetailedPhotos

    private val _poiRealty = MutableLiveData<List<Int>>()
    val poiRealty: LiveData<List<Int>>
        get() = _poiRealty

    private val _realtyPreviewExtras = MutableLiveData<List<RealtyPreviewExtras?>>()
    val realtyPreviewExtras: LiveData<List<RealtyPreviewExtras?>>
        get() = _realtyPreviewExtras

    init
    {
        getAllAgents()
        getAllPoi()
        getAllRealtyTypes()
        getRealtyWithoutLatLngAndUpdate()
    }

    // =================================
    //              Methods
    // =================================

    fun resetLoading()
    {
        _insertLoading.value = null
    }

    // ---------- Poi & RealtyTypes ---------- //

    private fun getAllPoi()
    {
        uiScope.uiJob {
            _poi.value = ioTask {
                repository.getAllPoi()
            }
        }
    }

    private fun getAllRealtyTypes()
    {
        uiScope.uiJob {
            _realtyTypes.value = ioTask {
                repository.getAllRealtyTypes()
            }
        }
    }

    // ---------- Realty / MediaReference ---------- //

    fun getAllRealty()
    {
        uiScope.uiJob {
            _isLoading.set(true)
            _realEstates.value = emptyList()
            _realEstates.value = ioTask {
                repository.getAllRealty()
            }
            getRealtyExtras()
            _isLoading.set(false)
        }
    }

    private fun getRealtyExtras()
    {
        uiScope.uiJob {
            val minimalMediaRefList = mutableListOf<RealtyPreviewExtras?>()
            _realtyPreviewExtras.value = minimalMediaRefList

            _realEstates.value?.forEachIndexed { index, realty ->
                val minMediaRef: String? = uiTask {
                    repository.getFirstMediaReferenceFromRealty(realty.id)
                }

                val realtyType = realtyTypes.value?.get(realty.realtyTypeId - 1)?.name
                val extras = RealtyPreviewExtras(realty.id, minMediaRef, realtyType)
                minimalMediaRefList.add(index, extras)
            }
            _realtyPreviewExtras.value = minimalMediaRefList
        }
    }

    fun getRealty(id: Long)
    {
        _realtyDetailedLoading.value = true
        _realtyDetailed.set(null)

        uiScope.uiJob {
            val realty = backgroundTask {
                repository.getRealty(id)
            }
            _realtyDetailed.set(realty)

            val realtyLat = realty.latitude
            val realtyLng = realty.longitude

            if (realtyLat == null || realtyLng == null)
            {
                backgroundTask {
                    //updateLatLngOfRealty(listOf(realty))
                }
            } else
                _realtyDetailedLatLng.value = RealtyMinimalForMap(realty.id, realtyLat, realtyLng)

            val mediaRefTask = ioTaskAsync {
                repository.getMediaReferencesFromRealty(id)
            }

            _poiRealty.value = ioTask {
                repository.getAllPoiRealtyFromRealtyId(id)
            }

            _realtyDetailedPhotos.value = mediaRefTask.await()
            _realtyDetailedLoading.value = false
        }
    }

    fun insertRealtyAndDependencies(realty: Realty, images: List<MediaReference?>, poiList: List<Int>)
    {
        uiScope.uiJob {
            _insertLoading.value = true

            val insertedId = backgroundTask {
                repository.insertRealty(realty)
            }
            val mediaRefTask = backgroundTaskAsync {
                repository.insertMediaReferences(images, insertedId)
            }
            backgroundTask {
                insertPoiRealty(poiList, insertedId)
            }
            mediaRefTask.await()
            _insertLoading.value = false
        }
    }

    fun updateRealty(realty: Realty, images: List<MediaReference?>, updatedPoiList: List<Int>)
    {
        uiScope.uiJob {
            backgroundTask {
                realty.longitude = realtyDetailed.get()?.longitude
                realty.latitude = realtyDetailed.get()?.latitude

                repository.updateRealty(realty)
            }

            val mediaRefTask = backgroundTaskAsync {
                repository.updateMediaReferences(images, realty.id)
                realtyDetailedPhotos.value?.forEach {
                    if (!images.contains(it))
                        repository.deleteMediaReference(it.mediaReferenceId)
                }
            }

            backgroundTask {
                updatePoiRealty(updatedPoiList, realty.id)
            }

            mediaRefTask.await()
            _insertLoading.value = false
        }
    }

    // ---------- PoiRealty ---------- //

    private suspend fun insertPoiRealty(poi: List<Int>, realtyId: Long)
    {
        val poiRealtyList = mutableListOf<PoiRealty>()
        poi.forEach {
            poiRealtyList.add(PoiRealty(realtyId = realtyId, poiId = it))
        }
        repository.insertPoiRealty(poiRealtyList.toTypedArray())
    }

    private suspend fun updatePoiRealty(poi: List<Int>, realtyId: Long)
    {
        repository.deletePoiRealtyFromRealtyId(realtyId)
        val poiRealtyList = mutableListOf<PoiRealty>().apply {
            poi.forEach { poiInt -> add(PoiRealty(realtyId = realtyId, poiId = poiInt)) }
        }
        repository.insertPoiRealty(poiRealtyList.toTypedArray())
    }

    // ---------- Agent ---------- //

    private fun getAllAgents()
    {
        uiScope.uiJob {
            val agentDb = ioTask {
                repository.getAllAgents()
            }
            _agent.value = agentDb
        }
    }

    // ---------- LatLng ---------- //

    private fun getRealtyWithoutLatLngAndUpdate()
    {
        uiScope.ioJob {
            val realtyNotUpdated = ioTask {
                repository.getRealtyLatitudeNotDefined()
            }
            Log.d("RealtyViewModel", "Realty that need to update their location : \n$realtyNotUpdated")
            ioTask {
                //updateLatLngOfRealty(realtyNotUpdated)
            }
        }
    }

    private suspend fun updateLatLngOfRealty(realtyNotUpdated: List<Realty>)
    {
        realtyNotUpdated.forEach { realty ->
            val latLng = ioTask {
                val addr = realty.address
                val pc = realty.postCode
                val city = realty.city
                repository.getLatLngFromAddress(addr, pc, city)
            }
            latLng?.let {
                if (it.isEmpty())
                    return@let
                realty.latitude = it.first().geometry.location.lat
                realty.longitude = it.first().geometry.location.lng
                repository.updateRealty(realty)
            }
        }
    }

    // ---------- Search Fragment ---------- //

    fun getRealtyMatchingParams(
        minPrice: Int?,
        maxPrice: Int?,
        poiList: List<Int>?,
        minSurface: Int?,
        maxSurface: Int?,
        isSold: Int?,
        creationDate: Long?,
        soldDate: Long?,
        mediaRefMinNumber: Int?,
        postCode: Int?
    )
    {
        uiScope.uiJob {
            _realEstates.value = emptyList()
            _realEstates.value = ioTask {
                repository.searchFromParameters(
                    minPrice,
                    maxPrice,
                    poiList,
                    minSurface,
                    maxSurface,
                    isSold,
                    creationDate,
                    soldDate,
                    mediaRefMinNumber,
                    postCode
                )
            }
            getRealtyExtras()
        }
    }

    // =================================
    //        Superclass Methods
    // =================================

    override fun onCleared()
    {
        super.onCleared()
        viewModelJob.cancel()
    }
}