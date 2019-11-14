package com.skichrome.realestatemanager.model.retrofit

import com.skichrome.realestatemanager.model.database.PoiRealty
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class RemotePoiRealty(
    @Json(name = "status") val status: String,
    @Json(name = "system_date") val lastUpdated: String,
    @Json(name = "lang") val lang: String,
    @Json(name = "num_results") val numResults: Int,
    @Json(name = "result") val results: List<PoiRealtyResults> = emptyList()
)

@JsonClass(generateAdapter = true)
data class PoiRealtyResults(
    @Json(name = "realty_id") val realtyId: Long,
    @Json(name = "agent_id") val agentId: Long,
    @Json(name = "poi_id") val poiId: Int
)
{
    companion object
    {
        fun fromRemoteToLocal(poiRealtyResults: List<PoiRealtyResults>): List<PoiRealty>
        {
            val localPoiRealtyList: MutableList<PoiRealty> = mutableListOf()
            poiRealtyResults.forEach {
                localPoiRealtyList.add(PoiRealty(poiId = it.poiId, realtyId = it.realtyId))
            }
            return localPoiRealtyList
        }

        fun fromLocalToRemote(poiRealty: List<PoiRealty>, agentId: Long): List<PoiRealtyResults>
        {
            val remotePoiRealtyList: MutableList<PoiRealtyResults> = mutableListOf()
            poiRealty.forEach {
                remotePoiRealtyList.add(PoiRealtyResults(realtyId = it.realtyId, poiId = it.poiId, agentId = agentId))
            }
            return remotePoiRealtyList
        }
    }
}