package com.skichrome.realestatemanager.model

import com.skichrome.realestatemanager.androidmanagers.NetManager
import com.skichrome.realestatemanager.model.database.*
import com.skichrome.realestatemanager.model.retrofit.Results

class RealtyRepository(
    private val netManager: NetManager,
    private val localDataSource: RealtyLocalRepository,
    private val remoteDataSource: RealtyRemoteRepository
)
{
    private fun isConnected(): Boolean = netManager.isConnectedToInternet?.let { isConnected ->
        return isConnected
    } ?: false

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
        if (isConnected())
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
        return localDataSource.getAllRealtyTypes()
    }

    // ---------- Poi ---------- //

    suspend fun getAllPoi(): List<Poi>
    {
        if (isConnected())
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
        return localDataSource.getAllPoi()
    }

    // ---------- PoiRealty ---------- //

    suspend fun getAllPoiRealty(): List<PoiRealty>
    {
        return localDataSource.getAllPoiRealty()
    }

    suspend fun insertPoiRealty(poiRealty: Array<PoiRealty>)
    {
        localDataSource.insertPoiRealty(poiRealty)
    }

    // ---------- LatLng ---------- //

    suspend fun getLatLngFromAddress(address: String, postCode: Int, city: String): List<Results>?
    {
        if (isConnected())
        {
            val result = remoteDataSource.getLatLngFromPhysicalAddress(address, postCode, city)
            if (result.isSuccessful)
                return result.body()?.results
        }
        return null
    }
}