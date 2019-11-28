package com.skichrome.realestatemanager.model

import androidx.sqlite.db.SupportSQLiteQuery
import com.skichrome.realestatemanager.model.database.*

class LocalRepository(private val db: RealEstateDatabase)
{
    // ---------- Realty ---------- //

    suspend fun insertRealty(realty: Realty) = db.realtyDao().insertIgnore(realty)

    suspend fun insertRealtyList(realty: Array<Realty>) = db.realtyDao().insertIgnore(*realty)

    suspend fun updateRealty(realty: Realty) = db.realtyDao().update(realty)

    suspend fun getRealtyById(id: Long) = db.realtyDao().getRealtyById(realtyId = id)

    suspend fun getAllRealty() = db.realtyDao().getAllRealty()

    suspend fun getRealtyWithoutLatLngDefined() = db.realtyDao().getRealtyListLatLngNull()

    // ---------- MediaReference ---------- //

    suspend fun insertMediaReference(medias: MediaReference) = db.mediaReferenceDao().insertIgnore(medias)

    suspend fun getMediaReferencesFromRealtyId(id: Long): List<MediaReference> = db.mediaReferenceDao().getMediaOfRealtyById(realtyId = id)

    suspend fun getMediaReferencesByIdList(ids: List<Long>) = db.mediaReferenceDao().getMediaByIdList(ids)

    suspend fun getMediaReferenceCountFromRealtyId(realtyId: Long) = db.mediaReferenceDao().getMediaReferenceNumberFromRealtyId(realtyId = realtyId)

    suspend fun getAllMediaReferences(): List<MediaReference> = db.mediaReferenceDao().getAllMedias()

    suspend fun deleteMediaReference(mediaId: Long) = db.mediaReferenceDao().deleteMediaOfRealtyById(mediaRefId = mediaId)

    // ---------- Agent ---------- //

    suspend fun insertAgent(agent: Agent) = db.agentDao().insertIgnore(agent)

    suspend fun insertAgentList(agent: Array<Agent>) = db.agentDao().insertIgnore(*agent)

    suspend fun updateAgent(agent: Agent) = db.agentDao().update(agent)

    suspend fun getAllAgents() = db.agentDao().getAllAgents()

    // ---------- RealtyType ---------- //

    suspend fun insertRealtyType(realtyType: RealtyType) = db.realtyTypeDao().insertReplace(realtyType)

    suspend fun insertRealtyTypeList(realtyType: Array<RealtyType>) = db.realtyTypeDao().insertReplace(*realtyType)

    suspend fun getAllRealtyTypes() = db.realtyTypeDao().getAllRealtyType()

    // ---------- Poi ---------- //

    suspend fun insertPoi(poi: Poi) = db.poiDao().insertReplace(poi)

    suspend fun insertPoiList(poi: Array<Poi>) = db.poiDao().insertReplace(*poi)

    suspend fun getAllPoi() = db.poiDao().getAllPoi()

    // ---------- PoiRealty ---------- //

    suspend fun getAllPoiRealty() = db.poiRealtyDao().getAllPoiRealty()

    suspend fun getPoiRealtyFromRealtyId(realtyId: Long) = db.poiRealtyDao().getPoIRealtyFromRealtyId(realtyId = realtyId)

    suspend fun getRealtyIdListFromPoiIdList(poiIds: List<Int>) = db.poiRealtyDao().getRealtyIdListFromPoiIdList(poiList = poiIds)

    suspend fun deletePoiRealtyByRealtyId(realtyId: Long) = db.poiRealtyDao().deletePoiRealtyFromRealtyId(realtyId = realtyId)

    suspend fun insertPoiRealty(poiRealty: Array<PoiRealty>) = db.poiRealtyDao().insertReplace(*poiRealty)

    // ---------- Raw Query ---------- //

    suspend fun fetchRealtyFromQueryParam(sqlQuery: SupportSQLiteQuery) = db.rawQueryDao().getRealtyFromRaw(sqlQuery = sqlQuery)
}