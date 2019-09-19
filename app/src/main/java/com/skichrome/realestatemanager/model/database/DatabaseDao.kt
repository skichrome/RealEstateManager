package com.skichrome.realestatemanager.model.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface RealtyDao
{
    @Query("SELECT * FROM Realty")
    suspend fun getAllRealty(): List<Realty>

    @Query("SELECT * FROM Realty WHERE id = :realtyId")
    suspend fun getRealtyById(realtyId: Long): Realty

    @Insert
    suspend fun insertRealty(realty: Realty): Long

    @Update
    suspend fun updateRealty(realty: Realty): Int

    @Query("DELETE FROM Realty WHERE id = :realtyId")
    suspend fun deleteRealtyById(realtyId: Long)
}

@Dao
interface PoiDao
{
    @Insert
    suspend fun insertPoi(poi: Poi): Long

    @Query("SELECT * FROM Poi")
    suspend fun getAllPoi(): List<Poi>

    @Query("SELECT * FROM Poi WHERE poiId = :realtyId")
    suspend fun getPoiOfRealtyById(realtyId: Int): Poi

    @Update
    suspend fun updatePoiOfRealty(poi: Poi): Int

    @Query("DELETE FROM Poi WHERE poiId = :poiId")
    suspend fun deletePoiOfRealtyById(poiId: Int)
}

@Dao
interface RealtyTypeDao
{
    @Insert
    suspend fun insertRealtyType(realtyType: RealtyType): Long

    @Query("SELECT * FROM RealtyType")
    suspend fun getAllRealtyType(): List<RealtyType>

    @Query("SELECT * FROM RealtyType WHERE realtyTypeId = :realtyId")
    suspend fun getTypeOfRealtyById(realtyId: Int): RealtyType

    @Update
    suspend fun updateTypeOfRealty(realtyType: RealtyType): Int

    @Query("DELETE FROM RealtyType WHERE realtyTypeId = :realtyId")
    suspend fun deleteTypeOfRealtyById(realtyId: Int)
}

@Dao
interface MediaReferenceDao
{
    @Insert
    suspend fun insertMediaReference(mediaRef: MediaReference): Long

    @Query("SELECT * FROM MediaReference")
    suspend fun getAllMedias(): List<MediaReference>

    @Query("SELECT * FROM MediaReference WHERE realtyId = :realtyId")
    suspend fun getMediaOfRealtyById(realtyId: Long): List<MediaReference>

    @Update
    suspend fun updateMediaOfRealty(mediaReference: MediaReference): Int

    @Query("DELETE FROM MediaReference WHERE mediaReferenceId = :mediaRefId")
    suspend fun deleteMediaOfRealtyById(mediaRefId: Long)
}