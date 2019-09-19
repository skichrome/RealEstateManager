package com.skichrome.realestatemanager.model.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import java.util.*

@Entity(
    foreignKeys = [
        ForeignKey(
            entity = RealtyType::class,
            parentColumns = ["realtyTypeId"],
            childColumns = ["realty_type_id"]
        )
    ]
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
    var status: Boolean = false,
    val dateAdded: Date,
    var dateSell: Date? = null,
    val agent: String,
    @ColumnInfo(name = "realty_type_id", index = true) val realtyTypeId: Int
)

@Entity
data class Poi(
    @PrimaryKey(autoGenerate = true) val poiId: Int = 0,
    var name: String
)

@Entity
data class RealtyType(
    @PrimaryKey(autoGenerate = true) val realtyTypeId: Int = 0,
    var name: String
)

@Entity(
    foreignKeys = [
        ForeignKey(
            entity = Realty::class,
            parentColumns = ["id"],
            childColumns = ["realtyId"],
            onUpdate = ForeignKey.CASCADE,
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class MediaReference(
    @PrimaryKey(autoGenerate = true) val mediaReferenceId: Long = 0L,
    var reference: String,
    var shortDesc: String,
    @ColumnInfo(name = "realtyId", index = true)
    val realtyId: Long
)

@Entity(
    primaryKeys = ["realtyId", "poiId"],
    foreignKeys = [ForeignKey(
        entity = Realty::class,
        parentColumns = ["id"],
        childColumns = ["poiId"]
    )]
)
data class PoiRealty(
    val realtyId: Long,
    @ColumnInfo(index = true) val poiId: Int
)