package com.skichrome.realestatemanager.model.retrofit

import com.skichrome.realestatemanager.model.database.MediaReference
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class RemoteMediaReference(
    @Json(name = "status") val status: String,
    @Json(name = "system_date") val lastUpdated: String,
    @Json(name = "lang") val lang: String,
    @Json(name = "result") val results: List<MediaReferenceResults> = emptyList()
)

@JsonClass(generateAdapter = true)
data class MediaReferenceResults(@Json(name = "id") val id: Long,
                                 @Json(name = "short_desc") val shortDesc: String,
                                 @Json(name = "agent_id") val agentId: Long,
                                 @Json(name = "reference") val reference: String,
                                 @Json(name = "realty_id") val realtyId: Long

)
{
    companion object
    {
        fun fromRemoteToLocal(remoteMediaRef: List<MediaReferenceResults>): List<MediaReference>
        {
            val localList: MutableList<MediaReference> = mutableListOf()
            remoteMediaRef.forEach {
                localList.add(
                    MediaReference(
                        mediaReferenceId = it.id,
                        shortDesc = it.shortDesc,
                        realtyId = it.realtyId,
                        reference = it.reference
                    )
                )
            }
            return localList
        }

        fun fromLocalToRemote(localMediaRef: List<MediaReference>, agentId: Long): List<MediaReferenceResults>
        {
            val remoteList: MutableList<MediaReferenceResults> = mutableListOf()
            localMediaRef.forEach {
                remoteList.add(
                    MediaReferenceResults(
                        id = it.mediaReferenceId,
                        reference = it.reference,
                        realtyId = it.realtyId,
                        shortDesc = it.shortDesc,
                        agentId = agentId
                    )
                )
            }
            return remoteList
        }
    }
}