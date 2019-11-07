package com.skichrome.realestatemanager.viewmodel

import android.util.Log
import androidx.databinding.ObservableBoolean
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.skichrome.realestatemanager.model.SignInRepository
import com.skichrome.realestatemanager.model.database.Agent
import com.skichrome.realestatemanager.utils.ioTask
import com.skichrome.realestatemanager.utils.uiJob
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job

class SignInViewModel(private val repository: SignInRepository) : ViewModel()
{
    // =======================================
    //                  Fields
    // =======================================

    private val viewModelJob = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    private val _isConnected = ObservableBoolean()
    val isConnected: ObservableBoolean
        get() = _isConnected

    private val _newAgentInserted = MutableLiveData<Agent?>()
    val newAgentInserted: LiveData<Agent?>
        get() = _newAgentInserted

    private val _availableUsers = MutableLiveData<List<Agent>>()
    val availableUsers: LiveData<List<Agent>>
        get() = _availableUsers

    init
    {
        isConnectedToInternet()
        getAvailableAgents()
    }

    // =======================================
    //                 Methods
    // =======================================

    private fun isConnectedToInternet()
    {
        val connStatus = repository.getInternetConnStatus()
        Log.e("SignInVM", "Network val : $connStatus")
        _isConnected.set(connStatus)
    }

    private fun getAvailableAgents()
    {
        uiScope.uiJob {
            val agents = ioTask {
                repository.getAllAgents()
            }
            _availableUsers.value = agents
        }
    }

    fun createAgent(newName: String)
    {
        uiScope.uiJob {
            val insertedAgent = ioTask {
                val id = _availableUsers.value!!.size + 1L
                val agent = Agent(agentId = id, name = newName)
                repository.insertNewAgent(agent)
                return@ioTask agent
            }
            _newAgentInserted.value = insertedAgent
        }
    }
}