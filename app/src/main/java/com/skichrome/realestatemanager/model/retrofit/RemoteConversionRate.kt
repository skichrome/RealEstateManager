package com.skichrome.realestatemanager.model.retrofit

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ConversionRate(
    @Json(name = "status") val status: String,
    @Json(name = "system_date") val systemDate: String,
    @Json(name = "lang") val lang: String,
    @Json(name = "result") val results: ConversionRateResults
)

@JsonClass(generateAdapter = true)
data class ConversionRateResults(
    @Json(name = "USD") val usdValue: Float,
    @Json(name = "EUR") val eurValue: Float
)