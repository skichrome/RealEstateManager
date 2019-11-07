package com.skichrome.realestatemanager.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.skichrome.realestatemanager.model.OnlineSyncRepository
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

    private val _isSyncEnded = MutableLiveData<Boolean>(false)
    val isSyncEnded: LiveData<Boolean>
        get() = _isSyncEnded

    init
    {
        synchroniseDatabase()
    }

    // =======================================
    //                 Methods
    // =======================================

    private fun synchroniseDatabase()
    {
        uiScope.uiJob {
            try
            {
                ioTask {
                    repository.synchronizeAgents()
                }
                ioTask {
                    repository.synchronizeRealty()
                }
                ioTask {
                    repository.synchronizePoiRealty()
                }
            } catch (e: Exception)
            {
                Log.e("Server Synchronization", "Synchronization error", e)
            }
            _isSyncEnded.value = true
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