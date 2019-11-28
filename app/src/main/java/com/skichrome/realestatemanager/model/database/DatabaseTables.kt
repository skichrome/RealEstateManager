package com.skichrome.realestatemanager.model.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    foreignKeys = [ForeignKey(
        entity = RealtyType::class,
        parentColumns = ["realtyTypeId"],
        childColumns = ["realty_type_id"]
    ),
        ForeignKey(
            entity = Agent::class,
            parentColumns = ["agentId"],
            childColumns = ["agent_id"]
        )]
)
data class Realty(
    @PrimaryKey(autoGenerate = true) var id: Long = 0L,
    val price: Float,
    val surface: Float,
    val roomNumber: Int,
    val fullDescription: String,
    val address: String,
    val postCode: Int,
    val city: String,
    var latitude: Double? = null,
    var longitude: Double? = null,
    var status: Boolean = false,
    val dateAdded: Long,
    var dateSell: Long? = null,
    @ColumnInfo(name = "agent_id", index = true) val agentId: Long,
    @ColumnInfo(name = "realty_type_id", index = true) var realtyTypeId: Int
)

@Entity
data class Poi(
    @PrimaryKey val poiId: Int,
    var name: String
)

@Entity
data class RealtyType(
    @PrimaryKey var realtyTypeId: Int,
    var name: String
)

@Entity(
    foreignKeys = [ForeignKey(
        entity = Realty::class,
        parentColumns = ["id"],
        childColumns = ["realtyId"],
        onUpdate = ForeignKey.CASCADE,
        onDelete = ForeignKey.CASCADE
    )]
)
data class MediaReference(
    @PrimaryKey(autoGenerate = true) val mediaReferenceId: Long = 0L,
    var reference: String,
    var shortDesc: String,
    @ColumnInfo(name = "realtyId", index = true)
    var realtyId: Long = -1L
)

@Entity(
    primaryKeys = ["realtyId", "poiId"],
    foreignKeys = [ForeignKey(
        entity = Realty::class,
        parentColumns = ["id"],
        childColumns = ["realtyId"]
    ),
        ForeignKey(
            entity = Poi::class,
            parentColumns = ["poiId"],
            childColumns = ["poiId"]
        )]
)
data class PoiRealty(
    val realtyId: Long,
    @ColumnInfo(index = true) val poiId: Int
)

@Entity
data class Agent(
    @PrimaryKey val agentId: Long,
    val name: String,
    val lastUpdate: Long? = null
)