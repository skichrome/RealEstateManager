package com.skichrome.realestatemanager.model

import com.skichrome.realestatemanager.androidmanagers.NetManager
import com.skichrome.realestatemanager.model.database.Realty

class RealEstateDataRepository(private val netManager: NetManager, private val localDataSource: RealEstateLocalRepository)
{
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

    suspend fun insertRealty(realty: Realty): List<Long> = localDataSource.createRealty(realty)
}