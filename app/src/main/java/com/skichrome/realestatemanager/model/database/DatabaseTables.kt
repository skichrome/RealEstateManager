package com.skichrome.realestatemanager.model.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import java.util.*

@Entity
data class Realty(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val price: Float,
    val surface: Float,
    val roomNumber: Int,
    val fullDescription: String,
    val address: String,
    val postCode: Int,
    val city: String,
    val status: Boolean,
    val dateAdded: Date,
    val dateSell: Date? = null,
    val agent: String
)

@Entity(
    foreignKeys = [
        ForeignKey(
            entity = Realty::class,
            parentColumns = ["id"],
            childColumns = ["realtyId"]
        )
    ]
)
data class Poi(
    @PrimaryKey(autoGenerate = true) val poiId: Long,
    val type: String,
    @ColumnInfo(name = "realtyId", index = true)
    val realtyId: Long
)

@Entity(
    foreignKeys = [
        ForeignKey(
            entity = Realty::class,
            parentColumns = ["id"],
            childColumns = ["realtyId"]
        )
    ]
)
data class RealtyType(
    @PrimaryKey(autoGenerate = true) val realtyTypeId: Long,
    val name: String,
    @ColumnInfo(name = "realtyId", index = true)
    val realtyId: Long
)

@Entity(
    foreignKeys = [
        ForeignKey(
            entity = Realty::class,
            parentColumns = ["id"],
            childColumns = ["realtyId"]
        )
    ]
)
data class MediaReference(
    @PrimaryKey(autoGenerate = true) val mediaReferenceId: Long,
    val reference: String,
    val shortDesc: String,
    @ColumnInfo(name = "realtyId", index = true)
    val realtyId: Long
)