package com.skichrome.realestatemanager.model.retrofit

import com.skichrome.realestatemanager.model.database.RealtyType
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class RemoteRealtyType(
    @Json(name = "status") val status: String,
    @Json(name = "system_date") val lastUpdated: String,
    @Json(name = "lang") val lang: String,
    @Json(name = "num_results") val numResults: Int,
    @Json(name = "result") val results: List<RealtyTypeResults> = emptyList()
)

@JsonClass(generateAdapter = true)
data class RealtyTypeResults(
    @Json(name = "id") val id: Int,
    @Json(name = "name") val name: String
)
{
    companion object
    {
        fun fromRemoteToLocal(poiRealtyResults: List<RealtyTypeResults>): List<RealtyType>
        {
            val localRealtyTypeList: MutableList<RealtyType> = mutableListOf()
            poiRealtyResults.forEach {
                localRealtyTypeList.add(RealtyType(realtyTypeId = it.id, name = it.name))
            }
            return localRealtyTypeList
        }

        fun fromLocalToRemote(poiRealty: List<RealtyType>): List<RealtyTypeResults>
        {
            val remoteRealtyTypeList: MutableList<RealtyTypeResults> = mutableListOf()
            poiRealty.forEach {
                remoteRealtyTypeList.add(RealtyTypeResults(id = it.realtyTypeId, name = it.name))
            }
            return remoteRealtyTypeList
        }
    }
}