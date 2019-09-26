package com.skichrome.realestatemanager.viewmodel

import androidx.databinding.ObservableField
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.skichrome.realestatemanager.model.RealEstateDataRepository
import com.skichrome.realestatemanager.model.database.MediaReference
import com.skichrome.realestatemanager.model.database.Realty
import com.skichrome.realestatemanager.model.database.RealtyType
import com.skichrome.realestatemanager.utils.backgroundTask
import com.skichrome.realestatemanager.utils.ioTask
import com.skichrome.realestatemanager.utils.uiJob
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job

class RealtyViewModel(private val repository: RealEstateDataRepository) : ViewModel()
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

    private val _realtyDetailed = ObservableField<Realty>()
    val realtyDetailed: ObservableField<Realty>
        get() = _realtyDetailed

    private val _realtyDetailedPhotos = MutableLiveData<List<MediaReference>>()
    val realtyDetailedPhotos: MutableLiveData<List<MediaReference>>
        get() = _realtyDetailedPhotos

    init
    {
        getRealtyTypes()
    }

    // =================================
    //              Methods
    // =================================

    fun resetLoading()
    {
        _insertLoading.value = null
    }

    private fun getRealtyTypes()
    {
        uiScope.uiJob {
            _realtyTypes.value = ioTask {
                repository.getAllRealtyTypes()
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
            _realtyDetailed.set(
                ioTask {
                    repository.getRealty(id)
                })
            _realtyDetailedPhotos.value = ioTask {
                repository.getMediaReferencesFromRealty(id)
            }
        }
    }

    fun insertRealty(realty: Realty, images: List<MediaReference?>)
    {
        viewModelScope.uiJob {
            _insertLoading.value = true

            backgroundTask {
                val realtyInsertedId = repository.insertRealty(realty)
                repository.insertMediaReferences(images, realtyInsertedId)
            }
            _insertLoading.value = false
        }
    }

    fun updateRealty(realty: Realty, images: List<MediaReference?>)
    {
        viewModelScope.uiJob {
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

    // =================================
    //        Superclass Methods
    // =================================

    override fun onCleared()
    {
        super.onCleared()
        viewModelJob.cancel()
    }
}