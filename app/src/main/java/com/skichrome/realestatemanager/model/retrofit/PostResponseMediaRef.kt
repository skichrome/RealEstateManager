package com.skichrome.realestatemanager.model.retrofit

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class PostResponseMediaRef(@Json(name = "OK") val status: Boolean?,
                                @Json(name = "id") val id: Long?,
                                @Json(name = "url") val url: String?
)