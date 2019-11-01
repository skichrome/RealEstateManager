package com.skichrome.realestatemanager.model

import android.util.Log
import com.skichrome.realestatemanager.androidmanagers.NetManager
import com.skichrome.realestatemanager.model.database.*
import com.skichrome.realestatemanager.model.retrofit.AgentResults
import com.skichrome.realestatemanager.model.retrofit.PoiRealtyResults
import com.skichrome.realestatemanager.model.retrofit.RealtyResults
import com.skichrome.realestatemanager.model.retrofit.Results

class RealtyRepository(
    private val netManager: NetManager,
    private val localDataSource: RealtyLocalRepository,
    private val remoteDataSource: RealtyRemoteRepository
)
{
    // ---------- Remote synchronisation ---------- //

    suspend fun synchronizeWithRemote(): Boolean
    {
        netManager.isConnectedToInternet?.let { isConnected ->
            if (isConnected)
            {
                try
                {
                    synchronizeAgents()
                    synchronizePoiRealty()
                    synchronizeRealty()
                } catch (e: Exception)
                {
                    Log.e("Server Synchronization", "Synchronization error", e)
                    return false
                }
            }
            return isConnected
        }
        return false
    }

    private suspend fun synchronizeAgents()
    {
        val agentRemote = AgentResults.fromLocalToRemote(getAllAgents())
        if (agentRemote.isEmpty())
            return
        val results = remoteDataSource.uploadAgents(agentRemote)

        val status = results.body()?.status ?: false
        if (!status)
            throw Exception("An error occurred when trying to synchronize Agents")
    }

    private suspend fun synchronizePoiRealty()
    {
        val poiRealtyRemote = PoiRealtyResults.fromLocalToRemote(getAllPoiRealty())
        if (poiRealtyRemote.isEmpty())
            return
        val results = remoteDataSource.uploadPoiRealty(poiRealtyRemote)

        val status = results.body()?.status ?: false
        if (!status)
            throw Exception("An error occurred when trying to synchronize PoiRealty")
    }

    private suspend fun synchronizeRealty()
    {
        val poiRealtyRemote = RealtyResults.fromLocalToRemote(getAllRealty())
        if (poiRealtyRemote.isEmpty())
            return
        val results = remoteDataSource.uploadRealty(poiRealtyRemote)

        val status = results.body()?.status ?: false
        if (!status)
            throw Exception("An error occurred when trying to synchronize PoiRealty")
    }

    // ---------- Realty ---------- //

    suspend fun getAllRealty(): List<Realty> = localDataSource.getAllRealty()

    suspend fun getRealty(id: Long): Realty = localDataSource.getRealtyById(id)

    suspend fun updateRealty(realty: Realty): Int = localDataSource.updateRealty(realty)

    suspend fun insertRealty(realty: Realty): Long = localDataSource.insertRealty(realty)

    suspend fun getRealtyLatitudeNotDefined() = localDataSource.getRealtyWithoutLatLngDefined()

    // ---------- MediaReference ---------- //

    suspend fun insertMediaReferences(medias: List<MediaReference?>, realtyId: Long) = medias.forEach {
        it?.let {
            it.realtyId = realtyId
            localDataSource.insertMediaReference(it)
        }
    }

    suspend fun updateMediaReferences(medias: List<MediaReference?>, realtyId: Long) = medias.forEach {
        it?.let {
            it.realtyId = realtyId
            localDataSource.insertMediaReference(it)
        }
    }

    suspend fun getMediaReferencesFromRealty(id: Long): List<MediaReference> = localDataSource.getMediaReferencesFromRealtyId(id)

    suspend fun deleteMediaReference(mediaId: Long) = localDataSource.deleteMediaReference(mediaId)

    // ---------- Agent ---------- //

    suspend fun insertAgent(agent: Agent): Long = localDataSource.insertAgent(agent)

    suspend fun getAllAgents(): List<Agent> = localDataSource.getAllAgents()

    // ---------- RealtyType ---------- //

    suspend fun getAllRealtyTypes(): List<RealtyType>
    {
        netManager.isConnectedToInternet?.let { isConnected ->
            if (isConnected)
            {
                val remoteResult = remoteDataSource.getAllRealtyTypes()
                if (remoteResult.isSuccessful)
                {
                    val resultList: MutableList<RealtyType> = mutableListOf()
                    remoteResult.body()?.results?.forEach {
                        val realtyType = RealtyType(realtyTypeId = it.id, name = it.name)
                        localDataSource.insertRealtyType(realtyType)
                        resultList.add(realtyType)
                    }
                    return resultList
                }
            }
        }
        return localDataSource.getAllRealtyTypes()
    }

    // ---------- Poi ---------- //

    suspend fun getAllPoi(): List<Poi>
    {
        netManager.isConnectedToInternet?.let { isConnected ->
            if (isConnected)
            {
                val remoteResult = remoteDataSource.getAllPoi()
                if (remoteResult.isSuccessful)
                {
                    val resultList: MutableList<Poi> = mutableListOf()
                    remoteResult.body()?.results?.forEach {
                        val poi = Poi(poiId = it.id, name = it.name)
                        localDataSource.insertPoi(poi)
                        resultList.add(poi)
                    }
                    return resultList
                }
            }
        }
        return localDataSource.getAllPoi()
    }

    // ---------- PoiRealty ---------- //

    suspend fun getAllPoiRealty(): List<PoiRealty>
    {
        return localDataSource.getAllPoiRealty()
    }

    // ---------- LatLng ---------- //

    suspend fun getLatLngFromAddress(address: String, postCode: Int, city: String): List<Results>?
    {
        netManager.isConnectedToInternet?.let {
            if (it)
            {
                val result = remoteDataSource.getLatLngFromPhysicalAddress(address, postCode, city)
                if (result.isSuccessful)
                    return result.body()?.results
            }
        }
        return null
    }
}