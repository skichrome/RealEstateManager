package com.skichrome.realestatemanager.model

import android.util.Log
import com.skichrome.realestatemanager.androidmanagers.NetManager
import com.skichrome.realestatemanager.model.database.Agent
import com.skichrome.realestatemanager.model.database.MediaReference
import com.skichrome.realestatemanager.model.database.Realty
import com.skichrome.realestatemanager.model.database.RealtyType
import com.skichrome.realestatemanager.model.retrofit.RealtyRemoteRepository
import com.skichrome.realestatemanager.model.retrofit.Result

class RealtyRepository(
    private val netManager: NetManager,
    private val localDataSource: RealtyLocalRepository,
    private val remoteDataSource: RealtyRemoteRepository
)
{
    // ---------- Realty ---------- //

    suspend fun getAllRealtyTypes(): List<RealtyType> = localDataSource.getAllRealtyTypes()

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

    suspend fun updateAgent(agent: Agent): Int = localDataSource.updateAgent(agent)

    suspend fun getAgentName(): String = localDataSource.getAgentName()

    // ---------- LatLng ---------- //

    suspend fun getLatLngFromAddress(address: String, postCode: Int, city: String): List<Result>?
    {
        netManager.isConnectedToInternet?.let {
            if (it)
            {
                val result = remoteDataSource.getLatLngFromPhysicalAddress(address, postCode, city)
                Log.e("Debug", "Realty not updated : ${result.body()}")
                if (result.isSuccessful)
                    return result.body()?.results
            }
        }
        return null
    }
}