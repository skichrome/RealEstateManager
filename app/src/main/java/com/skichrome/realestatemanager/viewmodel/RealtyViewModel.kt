package com.skichrome.realestatemanager.viewmodel

import android.util.Log
import androidx.databinding.ObservableField
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.skichrome.realestatemanager.model.RealtyRepository
import com.skichrome.realestatemanager.model.database.*
import com.skichrome.realestatemanager.model.database.minimalobj.RealtyMinimalForMap
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

    private val _poi = MutableLiveData<List<Poi>>()
    val poi: LiveData<List<Poi>>
        get() = _poi

    private val _agent = MutableLiveData<List<Agent>>()
    val agent: LiveData<List<Agent>>
        get() = _agent

    private val _realtyDetailed = ObservableField<Realty>()
    val realtyDetailed: ObservableField<Realty>
        get() = _realtyDetailed

    private val _realtyDetailedLatLng = MutableLiveData<RealtyMinimalForMap>()
    val realtyDetailedLatLng: LiveData<RealtyMinimalForMap>
        get() = _realtyDetailedLatLng

    private val _realtyDetailedPhotos = MutableLiveData<List<MediaReference>>()
    val realtyDetailedPhotos: LiveData<List<MediaReference>>
        get() = _realtyDetailedPhotos

    private val _poiRealty = MutableLiveData<List<PoiRealty>>()
    val poiRealty: LiveData<List<PoiRealty>>
        get() = _poiRealty

    init
    {
        getAllAgents()
        getAllPoi()
        getRealtyWithoutLatLngAndUpdate()
    }

    // =================================
    //              Methods
    // =================================

    fun resetLoading()
    {
        _insertLoading.value = null
    }

    // ---------- Realty / MediaReference ---------- //

    private fun getAllPoi()
    {
        uiScope.uiJob {
            _poi.value = ioTask {
                repository.getAllPoi()
            }
        }
    }

    fun getAllRealty()
    {
        uiScope.uiJob {
            _realEstates.value = ioTask {
                repository.getAllRealty()
            }
        }
    }

    fun getRealty(id: Long)
    {
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

    // =================================
    //        Superclass Methods
    // =================================

    override fun onCleared()
    {
        super.onCleared()
        viewModelJob.cancel()
    }
}