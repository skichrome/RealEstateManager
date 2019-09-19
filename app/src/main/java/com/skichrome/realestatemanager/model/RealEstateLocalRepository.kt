package com.skichrome.realestatemanager.model

import com.skichrome.realestatemanager.model.database.RealEstateDatabase
import com.skichrome.realestatemanager.model.database.Realty

class RealEstateLocalRepository(private val db: RealEstateDatabase)
{
    suspend fun getAllRealtyTypes() = db.realtyTypeDao().getAllRealtyType()

    suspend fun createRealty(vararg realty: Realty) = db.realtyDao().insertRealty(realty = *realty)

    suspend fun getAllRealty() = db.realtyDao().getAllRealty()
}