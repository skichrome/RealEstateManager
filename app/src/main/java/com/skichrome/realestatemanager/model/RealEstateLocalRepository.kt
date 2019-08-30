package com.skichrome.realestatemanager.model

import com.skichrome.realestatemanager.model.database.RealEstateDatabase
import com.skichrome.realestatemanager.model.database.Realty

class RealEstateLocalRepository(private val db: RealEstateDatabase)
{
    fun createRealty(vararg realty: Realty) = db.realtyDao().insertRealty(realty = *realty)

    fun getAllRealty() = db.realtyDao().getAllRealty()
}