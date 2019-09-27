package com.skichrome.realestatemanager.model

import com.skichrome.realestatemanager.model.database.Agent
import com.skichrome.realestatemanager.model.database.MediaReference
import com.skichrome.realestatemanager.model.database.RealEstateDatabase
import com.skichrome.realestatemanager.model.database.Realty

class RealEstateLocalRepository(private val db: RealEstateDatabase)
{
    // ---------- Realty ---------- //

    suspend fun insertRealty(realty: Realty) = db.realtyDao().insert(realty)

    suspend fun updateRealty(realty: Realty) = db.realtyDao().update(realty)

    suspend fun getRealtyById(id: Long) = db.realtyDao().getRealtyById(realtyId = id)

    suspend fun getAllRealty() = db.realtyDao().getAllRealty()

    suspend fun getAllRealtyTypes() = db.realtyTypeDao().getAllRealtyType()

    // ---------- MediaReference ---------- //

    suspend fun insertMediaReference(medias: MediaReference) = db.mediaReferenceDao().insert(medias)

    suspend fun getMediaReferencesFromRealtyId(id: Long): List<MediaReference> = db.mediaReferenceDao().getMediaOfRealtyById(realtyId = id)

    suspend fun deleteMediaReference(mediaId: Long) = db.mediaReferenceDao().deleteMediaOfRealtyById(mediaRefId = mediaId)

    // ---------- Agent ---------- //

    suspend fun insertAgent(agent: Agent) = db.agentDao().insert(agent)

    suspend fun updateAgent(agent: Agent) = db.agentDao().update(agent)

    suspend fun getAgentName() = db.agentDao().getAgentName()
}