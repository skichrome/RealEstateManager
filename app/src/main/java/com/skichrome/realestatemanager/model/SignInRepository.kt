package com.skichrome.realestatemanager.model

import com.skichrome.realestatemanager.androidmanagers.NetManager
import com.skichrome.realestatemanager.model.database.Agent
import com.skichrome.realestatemanager.model.retrofit.AgentResults

class SignInRepository(
    private val netManager: NetManager,
    private val localDataSource: LocalRepository,
    private val remoteDataSource: RemoteRepository
)
{
    fun getInternetConnStatus(): Boolean = netManager.isConnectedToInternet ?: false

    suspend fun getAllAgents(): List<Agent> = localDataSource.getAllAgents()

    suspend fun insertNewAgent(agent: Agent)
    {
        if (getInternetConnStatus())
        {
            val agentRemote = AgentResults.fromLocalToRemote(listOf(agent))
            remoteDataSource.uploadAgents(agentRemote)
        }
        localDataSource.insertAgent(agent)
    }
}