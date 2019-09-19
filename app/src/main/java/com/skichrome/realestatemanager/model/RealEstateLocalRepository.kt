package com.skichrome.realestatemanager.model

import com.skichrome.realestatemanager.model.database.MediaReference
import com.skichrome.realestatemanager.model.database.RealEstateDatabase
import com.skichrome.realestatemanager.model.database.Realty

class RealEstateLocalRepository(private val db: RealEstateDatabase)
{
    suspend fun getAllRealtyTypes() = db.realtyTypeDao().getAllRealtyType()

    suspend fun createRealty(realty: Realty) = db.realtyDao().insertRealty(realty = realty)

    suspend fun getAllRealty() = db.realtyDao().getAllRealty()

    suspend fun insertMediaReferences(medias: MediaReference) = db.mediaReferenceDao().insertMediaReference(mediaRef = medias)
}