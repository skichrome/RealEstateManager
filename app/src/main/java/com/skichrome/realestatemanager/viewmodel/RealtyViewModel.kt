package com.skichrome.realestatemanager.viewmodel

import androidx.databinding.ObservableField
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.skichrome.realestatemanager.model.RealEstateDataRepository
import com.skichrome.realestatemanager.model.database.Realty
import com.skichrome.realestatemanager.utils.backgroundTask
import com.skichrome.realestatemanager.utils.ioTask
import com.skichrome.realestatemanager.utils.uiJob
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job

class RealtyViewModel(private val realtyDataSource: RealEstateDataRepository) : ViewModel()
{
    // =================================
    //              Fields
    // =================================

    private val viewModelJob = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    val realEstates = MutableLiveData<List<Realty>>() // can be observed from fragment
    val isLoading = ObservableField<Boolean>(false) // can be observed from data binding
    val insertLoading = MutableLiveData<Boolean>()

    // =================================
    //              Methods
    // =================================

    fun getAllRealty()
    {
        uiScope.uiJob {
            realEstates.value = ioTask {
                realtyDataSource.getAllRealty()
            }
        }
    }

    fun insertRealty(realty: Realty)
    {
        viewModelScope.uiJob {
            insertLoading.value = true
            /*val insertionResult =*/ backgroundTask { realtyDataSource.insertRealty(realty) }
            insertLoading.value = false
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