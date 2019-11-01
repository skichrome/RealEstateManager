package com.skichrome.realestatemanager.model.retrofit

import com.skichrome.realestatemanager.model.database.Realty
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class RemoteRealty(
    @Json(name = "status") val status: String,
    @Json(name = "system_date") val lastUpdated: String,
    @Json(name = "lang") val lang: String,
    @Json(name = "num_results") val numResults: Int,
    @Json(name = "result") val results: List<RealtyResults> = emptyList()
)

@JsonClass(generateAdapter = true)
data class RealtyResults(
    @Json(name = "id") val id: Long,
    @Json(name = "price") val price: Float,
    @Json(name = "surface") val surface: Float,
    @Json(name = "room_number") val roomNumber: Int,
    @Json(name = "full_description") val fullDescription: String,
    @Json(name = "address") val address: String,
    @Json(name = "post_code") val postCode: Int,
    @Json(name = "city") val city: String,
    @Json(name = "latitude") val latitude: Double?,
    @Json(name = "longitude") val longitude: Double?,
    @Json(name = "status") val status: Boolean,
    @Json(name = "date_added") val dateAdded: Long,
    @Json(name = "date_sell") val dateSell: Long?,
    @Json(name = "agent_id") val agentId: Long,
    @Json(name = "realty_type_id") val realtyTypeId: Int
)
{
    companion object
    {
        fun fromRemoteToLocal(realtyResults: List<RealtyResults>): List<Realty>
        {
            val localRealtyList: MutableList<Realty> = mutableListOf()
            realtyResults.forEach {
                localRealtyList.add(
                    Realty(
                        agentId = it.agentId,
                        status = it.status,
                        id = it.id,
                        realtyTypeId = it.realtyTypeId,
                        address = it.address,
                        city = it.city,
                        dateAdded = it.dateAdded,
                        dateSell = it.dateSell,
                        fullDescription = it.fullDescription,
                        latitude = it.latitude,
                        longitude = it.longitude,
                        postCode = it.postCode,
                        price = it.price,
                        roomNumber = it.roomNumber,
                        surface = it.surface
                    )
                )
            }
            return localRealtyList
        }

        fun fromLocalToRemote(realtyList: List<Realty>): List<RealtyResults>
        {
            val remoteRealtyList: MutableList<RealtyResults> = mutableListOf()
            realtyList.forEach {
                remoteRealtyList.add(
                    RealtyResults(
                        agentId = it.agentId,
                        status = it.status,
                        id = it.id,
                        realtyTypeId = it.realtyTypeId,
                        address = it.address,
                        city = it.city,
                        dateAdded = it.dateAdded,
                        dateSell = it.dateSell,
                        fullDescription = it.fullDescription,
                        latitude = it.latitude,
                        longitude = it.longitude,
                        postCode = it.postCode,
                        price = it.price,
                        roomNumber = it.roomNumber,
                        surface = it.surface
                    )
                )
            }
            return remoteRealtyList
        }
    }
}