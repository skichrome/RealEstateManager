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

    private val _synchronisationProgress = MutableLiveData(0)
    val synchronisationProgress: LiveData<Int>
        get() = _synchronisationProgress

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
        _synchronisationProgress.value = 0

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
        _synchronisationProgress.value = 10

        uiScope.uiJob {
            try
            {
                ioTask {
                    repository.synchronizePoi()
                }
                _synchronisationProgress.value = 20
                ioTask {
                    repository.synchronizeRealtyTypes()
                }
                _synchronisationProgress.value = 30
                ioTask {
                    repository.synchronizeRealty(currentAgentId)
                }
                _synchronisationProgress.value = 50
                ioTask {
                    repository.synchronizePoiRealty(currentAgentId)
                }
                _synchronisationProgress.value = 70
                ioTask {
                    repository.synchronizeMediaReferences(currentAgentId)
                }
                ioTask {
                    repository.getRemoteMediaReferences(currentAgentId)
                }
            } catch (e: Exception)
            {
                Log.e("Server Synchronization", "Synchronization error", e)
            }
            _synchronisationProgress.value = 100
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