package com.skichrome.realestatemanager.model.retrofit

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class RemoteAgent(
    @Json(name = "status") val status: String,
    @Json(name = "last_updated") val lastUpdated: String,
    @Json(name = "lang") val lang: String,
    @Json(name = "num_results") val numResults: Int,
    @Json(name = "result") val results: List<AgentResults> = emptyList()
)

@JsonClass(generateAdapter = true)
data class AgentResults(
    @Json(name = "agent_id") val id: Long,
    @Json(name = "name") val name: String,
    @Json(name = "last_database_update") val lastUpdate: String
)