package com.skichrome.realestatemanager.model.retrofit

import com.skichrome.realestatemanager.model.database.Poi
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class RemotePoi(
    @Json(name = "status") val status: String,
    @Json(name = "system_date") val lastUpdated: String,
    @Json(name = "lang") val lang: String,
    @Json(name = "num_results") val numResults: Int,
    @Json(name = "result") val results: List<PoiResults> = emptyList()
)

@JsonClass(generateAdapter = true)
data class PoiResults(
    @Json(name = "id") val id: Int,
    @Json(name = "name") val name: String
)
{
    companion object
    {
        fun fromRemoteToLocal(poiRealtyResults: List<PoiResults>): List<Poi>
        {
            val localPoiList: MutableList<Poi> = mutableListOf()
            poiRealtyResults.forEach {
                localPoiList.add(Poi(poiId = it.id, name = it.name))
            }
            return localPoiList
        }

        fun fromLocalToRemote(poiRealty: List<Poi>): List<PoiResults>
        {
            val remotePoiList: MutableList<PoiResults> = mutableListOf()
            poiRealty.forEach {
                remotePoiList.add(PoiResults(id = it.poiId, name = it.name))
            }
            return remotePoiList
        }
    }
}