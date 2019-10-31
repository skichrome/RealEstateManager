package com.skichrome.realestatemanager.model

import com.skichrome.realestatemanager.model.database.*

class RealtyLocalRepository(private val db: RealEstateDatabase)
{
    // ---------- Realty ---------- //

    suspend fun insertRealty(realty: Realty) = db.realtyDao().insertIgnore(realty)

    suspend fun updateRealty(realty: Realty) = db.realtyDao().update(realty)

    suspend fun getRealtyById(id: Long) = db.realtyDao().getRealtyById(realtyId = id)

    suspend fun getAllRealty() = db.realtyDao().getAllRealty()

    suspend fun getRealtyWithoutLatLngDefined() = db.realtyDao().getRealtyListLatLngNull()

    // ---------- MediaReference ---------- //

    suspend fun insertMediaReference(medias: MediaReference) = db.mediaReferenceDao().insertIgnore(medias)

    suspend fun getMediaReferencesFromRealtyId(id: Long): List<MediaReference> = db.mediaReferenceDao().getMediaOfRealtyById(realtyId = id)

    suspend fun deleteMediaReference(mediaId: Long) = db.mediaReferenceDao().deleteMediaOfRealtyById(mediaRefId = mediaId)

    // ---------- Agent ---------- //

    suspend fun insertAgent(agent: Agent) = db.agentDao().insertIgnore(agent)

    suspend fun updateAgent(agent: Agent) = db.agentDao().update(agent)

    suspend fun getAgentName() = db.agentDao().getAgentName()

    // ---------- RealtyType ---------- //

    suspend fun insertRealtyType(realtyType: RealtyType) = db.realtyTypeDao().insertReplace(realtyType)

    suspend fun getAllRealtyTypes() = db.realtyTypeDao().getAllRealtyType()

    // ---------- Poi ---------- //

    suspend fun insertPoi(poi: Poi) = db.poiDao().insertReplace(poi)

    suspend fun getAllPoi() = db.poiDao().getAllPoi()
}