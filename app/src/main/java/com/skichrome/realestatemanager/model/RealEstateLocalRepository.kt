package com.skichrome.realestatemanager.model

import com.skichrome.realestatemanager.model.database.MediaReference
import com.skichrome.realestatemanager.model.database.RealEstateDatabase
import com.skichrome.realestatemanager.model.database.Realty

class RealEstateLocalRepository(private val db: RealEstateDatabase)
{
    suspend fun getAllRealtyTypes() = db.realtyTypeDao().getAllRealtyType()

    suspend fun createRealty(realty: Realty) = db.realtyDao().insertRealty(realty = realty)

    suspend fun updateRealty(realty: Realty) = db.realtyDao().updateRealty(realty = realty)

    suspend fun getAllRealty() = db.realtyDao().getAllRealty()

    suspend fun getRealtyById(id: Long) = db.realtyDao().getRealtyById(realtyId = id)

    suspend fun insertMediaReference(medias: MediaReference) = db.mediaReferenceDao().insertMediaReference(mediaRef = medias)

    suspend fun getMediaReferencesFromRealtyId(id: Long): List<MediaReference> = db.mediaReferenceDao().getMediaOfRealtyById(realtyId = id)

    suspend fun deleteMediaReference(mediaId: Long) = db.mediaReferenceDao().deleteMediaOfRealtyById(mediaRefId = mediaId)
}