package com.skichrome.realestatemanager.model

import com.skichrome.realestatemanager.androidmanagers.NetManager
import com.skichrome.realestatemanager.model.database.MediaReference
import com.skichrome.realestatemanager.model.database.Realty
import com.skichrome.realestatemanager.model.database.RealtyType

class RealEstateDataRepository(private val netManager: NetManager, private val localDataSource: RealEstateLocalRepository)
{
    suspend fun getAllRealtyTypes(): List<RealtyType>
    {
        netManager.isConnectedToInternet?.let {
            if (it)
            {
                return emptyList()
            }
        }
        return localDataSource.getAllRealtyTypes()
    }

    suspend fun getAllRealty(): List<Realty>
    {
        netManager.isConnectedToInternet?.let {
            if (it)
            {
                // Todo Save to remote api
                return listOf()
            }
        }
        return localDataSource.getAllRealty()
    }

    suspend fun getRealty(id: Long): Realty = localDataSource.getRealtyById(id)

    suspend fun insertRealty(realty: Realty): Long = localDataSource.createRealty(realty)

    suspend fun insertMediaReferences(medias: List<MediaReference?>, realtyId: Long) = medias.forEach {
        if (it != null)
        {
            it.realtyId = realtyId
            localDataSource.insertMediaReferences(it)
        }
    }

    suspend fun getMediaReferencesFromRealty(id: Long): List<MediaReference> = localDataSource.getMediaReferencesFromRealtyId(id)
}