package com.skichrome.realestatemanager.viewmodel

import androidx.databinding.ObservableField
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.skichrome.realestatemanager.model.RealtyRepository
import com.skichrome.realestatemanager.model.database.*
import com.skichrome.realestatemanager.model.database.minimalobj.RealtyMinimalForMap
import com.skichrome.realestatemanager.utils.backgroundTask
import com.skichrome.realestatemanager.utils.ioJob
import com.skichrome.realestatemanager.utils.ioTask
import com.skichrome.realestatemanager.utils.uiJob
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job

class RealtyViewModel(private val repository: RealtyRepository) : ViewModel()
{
    // =================================
    //              Fields
    // =================================

    private val viewModelJob = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    private val _realEstates = MutableLiveData<List<Realty>>() // can be observed from fragment
    val realEstates: LiveData<List<Realty>>
        get() = _realEstates

    private val _isLoading = ObservableField<Boolean>(false) // can be observed from data binding
    val isLoading: ObservableField<Boolean>
        get() = _isLoading

    private val _insertLoading = MutableLiveData<Boolean?>()
    val insertLoading: LiveData<Boolean?>
        get() = _insertLoading

    private val _realtyTypes = MutableLiveData<List<RealtyType>>()
    val realtyTypes: LiveData<List<RealtyType>>
        get() = _realtyTypes

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
    val realtyDetailedPhotos: MutableLiveData<List<MediaReference>>
        get() = _realtyDetailedPhotos

    init
    {
        getAllAgents()
        getRealtyTypes()
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

    private fun getRealtyTypes()
    {
        uiScope.uiJob {
            _realtyTypes.value = ioTask {
                repository.getAllRealtyTypes()
            }
        }
    }

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
            val realty = ioTask {
                repository.getRealty(id)
            }
            _realtyDetailed.set(realty)

            val realtyLat = realty.latitude
            val realtyLng = realty.longitude

            if (realtyLat == null || realtyLng == null)
            {
                //updateLatLngOfRealty(listOf(realty))
            }
            else
                _realtyDetailedLatLng.value = RealtyMinimalForMap(realty.id, realtyLat, realtyLng)

            _realtyDetailedPhotos.value = ioTask {
                repository.getMediaReferencesFromRealty(id)
            }
        }
    }

    fun insertRealty(realty: Realty, images: List<MediaReference?>): Long
    {
        var insertedId = -1L
        uiScope.uiJob {
            _insertLoading.value = true

            insertedId = backgroundTask {
                val realtyInsertedId = repository.insertRealty(realty)
                repository.insertMediaReferences(images, realtyInsertedId)
                return@backgroundTask realtyInsertedId
            }
            _insertLoading.value = false
        }
        return insertedId
    }

    fun updateRealty(realty: Realty, images: List<MediaReference?>)
    {
        uiScope.uiJob {
            backgroundTask {
                repository.updateRealty(realty)
                repository.updateMediaReferences(images, realty.id)

                realtyDetailedPhotos.value?.forEach {
                    if (!images.contains(it))
                        repository.deleteMediaReference(it.mediaReferenceId)
                }
            }
            _insertLoading.value = false
        }
    }

    // ---------- PoiRealty ---------- //

    fun insertPoiRealty(poi: List<Int>, realtyId: Long)
    {
        uiScope.ioJob {
            ioTask {
                val poiRealtyList = mutableListOf<PoiRealty>()
                poi.forEach {
                    poiRealtyList.add(PoiRealty(realtyId = realtyId, poiId = it))
                }
                repository.insertPoiRealty(poiRealtyList.toTypedArray())
            }
        }
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

    fun createNewAgent(newAgent: Agent)
    {
        uiScope.uiJob {
            ioTask {
                repository.insertAgent(newAgent)
            }
        }
    }

    // ---------- LatLng ---------- //

    private fun getRealtyWithoutLatLngAndUpdate()
    {
        uiScope.ioJob {
            val realtyNotUpdated = ioTask {
                repository.getRealtyLatitudeNotDefined()
            }
            //updateLatLngOfRealty(realtyNotUpdated)
        }
    }

    private fun updateLatLngOfRealty(realtyNotUpdated: List<Realty>)
    {
        uiScope.ioJob {

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