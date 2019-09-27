package com.skichrome.realestatemanager.model

import com.skichrome.realestatemanager.androidmanagers.NetManager
import com.skichrome.realestatemanager.model.database.Agent
import com.skichrome.realestatemanager.model.database.MediaReference
import com.skichrome.realestatemanager.model.database.Realty
import com.skichrome.realestatemanager.model.database.RealtyType

class RealEstateDataRepository(private val netManager: NetManager, private val localDataSource: RealEstateLocalRepository)
{
    // ---------- Realty ---------- //

    suspend fun getAllRealtyTypes(): List<RealtyType>
    {
        netManager.isConnectedToInternet?.let {
            //            if (it)
//            {
//                return emptyList()
//            }
        }
        return localDataSource.getAllRealtyTypes()
    }

    suspend fun getAllRealty(): List<Realty>
    {
        netManager.isConnectedToInternet?.let {
            //            if (it)
//            {
//                // Todo Remote synchronisation
//                return emptyList()
//            }
        }
        return localDataSource.getAllRealty()
    }

    suspend fun getRealty(id: Long): Realty = localDataSource.getRealtyById(id)

    suspend fun updateRealty(realty: Realty): Int = localDataSource.updateRealty(realty)

    suspend fun insertRealty(realty: Realty): Long = localDataSource.insertRealty(realty)

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
}