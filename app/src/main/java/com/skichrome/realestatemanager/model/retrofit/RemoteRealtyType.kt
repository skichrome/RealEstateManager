package com.skichrome.realestatemanager.model.retrofit

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class RemoteRealtyType(
    @Json(name = "status") val status: String,
    @Json(name = "last_updated") val lastUpdated: String,
    @Json(name = "lang") val lang: String,
    @Json(name = "num_results") val numResults: Int,
    @Json(name = "result") val results: List<RealtyTypeResults> = emptyList()
)

@JsonClass(generateAdapter = true)
data class RealtyTypeResults(
    @Json(name = "id") val id: Int,
    @Json(name = "name") val name: String
)