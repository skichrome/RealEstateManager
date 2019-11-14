package com.skichrome.realestatemanager.model

import com.skichrome.realestatemanager.androidmanagers.NetManager
import com.skichrome.realestatemanager.model.retrofit.*

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

    // ---------- Poi & RealtyTypes ---------- //

    suspend fun synchronizePoi()
    {
        if (isConnected())
        {
            val poiRemote = remoteDataSource.getAllPoi()
            throwExceptionIfStatusIsFalse(poiRemote.isSuccessful, "poi (upload)")

            poiRemote.body()?.results?.let {
                PoiResults.fromRemoteToLocal(it).apply {
                    localDataSource.insertPoiList(this.toTypedArray())
                }
            }
        }
    }

    suspend fun synchronizeRealtyTypes()
    {
        if (isConnected())
        {
            val poiRemote = remoteDataSource.getAllRealtyTypes()
            throwExceptionIfStatusIsFalse(poiRemote.isSuccessful, "poi (download)")

            poiRemote.body()?.results?.let {
                RealtyTypeResults.fromRemoteToLocal(it).apply {
                    localDataSource.insertRealtyTypeList(this.toTypedArray())
                }
            }
        }
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
        throwExceptionIfStatusIsFalse(status, "agents (upload)")
    }

    private suspend fun downloadAgents()
    {
        val agentRemote = remoteDataSource.getAllAgents()
        throwExceptionIfStatusIsFalse(agentRemote.isSuccessful, "agents (download)")

        agentRemote.body()?.results?.let {
            AgentResults.fromRemoteToLocal(it).apply {
                localDataSource.insertAgentList(this.toTypedArray())
            }
        }
    }

    // ---------- PoiRealty ---------- //

    suspend fun synchronizePoiRealty(agentId: Long)
    {
        if (isConnected())
        {
            uploadPoiRealty(agentId)
            downloadPoiRealty(agentId)
        }
    }

    private suspend fun uploadPoiRealty(agentId: Long)
    {
        val poiRealtyRemote = PoiRealtyResults.fromLocalToRemote(localDataSource.getAllPoiRealty(), agentId)
        if (poiRealtyRemote.isEmpty())
            return
        val results = remoteDataSource.uploadPoiRealty(poiRealtyRemote)

        val status = results.body()?.status ?: false
        throwExceptionIfStatusIsFalse(status, "PoiRealty (upload)")
    }

    private suspend fun downloadPoiRealty(agentId: Long)
    {
        val poiRealtyRemote = remoteDataSource.getAllPoiRealty(agentId)
        throwExceptionIfStatusIsFalse(poiRealtyRemote.isSuccessful, "PoiRealty (download)")

        poiRealtyRemote.body()?.results?.let {
            PoiRealtyResults.fromRemoteToLocal(it).apply {
                localDataSource.insertPoiRealty(this.toTypedArray())
            }
        }
    }

    // ---------- Realty ---------- //

    suspend fun synchronizeRealty(agentId: Long)
    {
        if (isConnected())
        {
            uploadRealty()
            downloadRealty(agentId)
        }
    }

    private suspend fun uploadRealty()
    {
        val realtyRemote = RealtyResults.fromLocalToRemote(localDataSource.getAllRealty())
        if (realtyRemote.isEmpty())
            return
        val results = remoteDataSource.uploadRealty(realtyRemote)

        val status = results.body()?.status ?: false
        throwExceptionIfStatusIsFalse(status, "realty (upload)")
    }

    private suspend fun downloadRealty(agentId: Long)
    {
        val realtyRemote = remoteDataSource.getAllRealty(agentId)
        throwExceptionIfStatusIsFalse(realtyRemote.isSuccessful, "realty (download)")

        realtyRemote.body()?.results?.let {
            RealtyResults.fromRemoteToLocal(it).apply {
                localDataSource.insertRealtyList(this.toTypedArray())
            }
        }
    }
}