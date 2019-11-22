package com.skichrome.realestatemanager.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.skichrome.realestatemanager.model.OnlineSyncRepository
import com.skichrome.realestatemanager.utils.backgroundTask
import com.skichrome.realestatemanager.utils.ioTask
import com.skichrome.realestatemanager.utils.uiJob
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job

class OnlineSyncViewModel(private val repository: OnlineSyncRepository) : ViewModel()
{
    // =======================================
    //                  Fields
    // =======================================

    private val viewModelJob = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    private val _isAgentSyncEnded = MutableLiveData<Boolean>(false)
    val isAgentSyncEnded: LiveData<Boolean>
        get() = _isAgentSyncEnded

    private val _isGlobalSyncEnded = MutableLiveData<Boolean>(false)
    val isGlobalSyncEnded: LiveData<Boolean>
        get() = _isGlobalSyncEnded

    init
    {
        synchronizeAgents()
    }

    // =======================================
    //                 Methods
    // =======================================

    private fun synchronizeAgents()
    {
        _isAgentSyncEnded.value = false

        uiScope.uiJob {
            try
            {
                backgroundTask {
                    repository.synchronizeAgents()
                }
            } catch (e: Exception)
            {
                Log.e("Server Synchronization", "Synchronization error with agents", e)
            }
            _isAgentSyncEnded.value = true
        }
    }

    fun synchroniseDatabase(currentAgentId: Long)
    {
        _isGlobalSyncEnded.value = false

        uiScope.uiJob {
            try
            {
                ioTask {
                    repository.synchronizePoi()
                    repository.synchronizeRealtyTypes()
                    repository.synchronizeRealty(currentAgentId)
                    repository.synchronizePoiRealty(currentAgentId)
                }
                ioTask {
                    repository.synchronizeMediaReferences(currentAgentId)
                }
            } catch (e: Exception)
            {
                Log.e("Server Synchronization", "Synchronization error", e)
            }
            _isGlobalSyncEnded.value = true
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