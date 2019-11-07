package com.skichrome.realestatemanager.model

import com.skichrome.realestatemanager.androidmanagers.NetManager
import com.skichrome.realestatemanager.model.retrofit.AgentResults
import com.skichrome.realestatemanager.model.retrofit.PoiRealtyResults
import com.skichrome.realestatemanager.model.retrofit.RealtyResults

class OnlineSyncRepository(
    private val netManager: NetManager,
    private val localDataSource: LocalRepository,
    private val remoteDataSource: RemoteRepository
)
{
    // =======================================
    //                 Methods
    // =======================================

    private fun isConnected(): Boolean = netManager.isConnectedToInternet ?: false

    private fun throwExceptionIfStatusIsFalse(status: Boolean, origin: String)
    {
        if (!status)
            throw Exception("An error occurred when trying to synchronize $origin")
    }

    // ---------- Agents ---------- //

    suspend fun synchronizeAgents()
    {
        if (isConnected())
        {
            uploadAgents()
            downloadAgents()
        }
    }

    private suspend fun uploadAgents()
    {
        val agentLocal = AgentResults.fromLocalToRemote(localDataSource.getAllAgents())
        if (agentLocal.isEmpty())
            return
        val results = remoteDataSource.uploadAgents(agentLocal)

        val status = results.body()?.status ?: false
        throwExceptionIfStatusIsFalse(status, "agents")
    }

    private suspend fun downloadAgents()
    {
        val agentRemote = remoteDataSource.getAllAgents()
        throwExceptionIfStatusIsFalse(agentRemote.isSuccessful, "agents")

        agentRemote.body()?.results?.let {
            AgentResults.fromRemoteToLocal(it).apply {
                localDataSource.insertAgentList(this.toTypedArray())
            }
        }
    }

    // ---------- PoiRealty ---------- //

    suspend fun synchronizePoiRealty()
    {
        if (isConnected())
        {
            uploadPoiRealty()
            downloadPoiRealty()
        }
    }

    private suspend fun uploadPoiRealty()
    {
        val poiRealtyRemote = PoiRealtyResults.fromLocalToRemote(localDataSource.getAllPoiRealty())
        if (poiRealtyRemote.isEmpty())
            return
        val results = remoteDataSource.uploadPoiRealty(poiRealtyRemote)

        val status = results.body()?.status ?: false
        throwExceptionIfStatusIsFalse(status, "PoiRealty")
    }

    private suspend fun downloadPoiRealty()
    {
        val poiRealtyRemote = remoteDataSource.getAllPoiRealty()
        throwExceptionIfStatusIsFalse(poiRealtyRemote.isSuccessful, "PoiRealty")

        poiRealtyRemote.body()?.results?.let {
            PoiRealtyResults.fromRemoteToLocal(it).apply {
                localDataSource.insertPoiRealty(this.toTypedArray())
            }
        }
    }

    // ---------- Realty ---------- //

    suspend fun synchronizeRealty()
    {
        if (isConnected())
        {
            uploadRealty()
            downloadRealty()
        }
    }

    private suspend fun uploadRealty()
    {
        val realtyRemote = RealtyResults.fromLocalToRemote(localDataSource.getAllRealty())
        if (realtyRemote.isEmpty())
            return
        val results = remoteDataSource.uploadRealty(realtyRemote)

        val status = results.body()?.status ?: false
        throwExceptionIfStatusIsFalse(status, "realty")
    }

    private suspend fun downloadRealty()
    {
        val realtyRemote = remoteDataSource.getAllRealty()
        throwExceptionIfStatusIsFalse(realtyRemote.isSuccessful, "realty")

        realtyRemote.body()?.results?.let {
            RealtyResults.fromRemoteToLocal(it).apply {
                localDataSource.insertRealtyList(this.toTypedArray())
            }
        }
    }
}