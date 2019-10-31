package com.skichrome.realestatemanager.model.database

import android.database.Cursor
import androidx.room.*

interface BaseDao<T>
{
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertIgnore(obj: T): Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertIgnore(vararg obj: T): List<Long>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReplace(obj: T): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReplace(vararg obj: T): List<Long>

    @Update
    suspend fun update(obj: T): Int

    @Update
    suspend fun update(vararg obj: T): Int
}

@Dao
interface RealtyDao : BaseDao<Realty>
{
    @Query("SELECT * FROM Realty")
    suspend fun getAllRealty(): List<Realty>

    @Query("SELECT * FROM Realty WHERE id = :realtyId")
    suspend fun getRealtyById(realtyId: Long): Realty

    @Query("DELETE FROM Realty WHERE id = :realtyId")
    suspend fun deleteRealtyById(realtyId: Long)

    @Query("SELECT * FROM Realty WHERE latitude IS NULL OR longitude IS NULL")
    suspend fun getRealtyListLatLngNull(): List<Realty>

    //ContentProvider
    @Query("SELECT * FROM Realty WHERE id = :realtyId")
    fun getRealtyByIdWithCursor(realtyId: Long): Cursor
}

@Dao
interface PoiDao : BaseDao<Poi>
{

    @Query("SELECT * FROM Poi")
    suspend fun getAllPoi(): List<Poi>

    @Query("SELECT * FROM Poi WHERE poiId = :realtyId")
    suspend fun getPoiOfRealtyById(realtyId: Int): Poi

    @Query("DELETE FROM Poi WHERE poiId = :poiId")
    suspend fun deletePoiOfRealtyById(poiId: Int)
}

@Dao
interface RealtyTypeDao : BaseDao<RealtyType>
{
    @Query("SELECT * FROM RealtyType")
    suspend fun getAllRealtyType(): List<RealtyType>

    @Query("SELECT * FROM RealtyType WHERE realtyTypeId = :realtyTypeId")
    suspend fun getTypeOfRealtyById(realtyTypeId: Int): RealtyType

    @Query("DELETE FROM RealtyType WHERE realtyTypeId = :realtyTypeId")
    suspend fun deleteTypeOfRealtyById(realtyTypeId: Int)
}

@Dao
interface MediaReferenceDao : BaseDao<MediaReference>
{
    @Query("SELECT * FROM MediaReference")
    suspend fun getAllMedias(): List<MediaReference>

    @Query("SELECT * FROM MediaReference WHERE realtyId = :realtyId")
    suspend fun getMediaOfRealtyById(realtyId: Long): List<MediaReference>

    @Query("DELETE FROM MediaReference WHERE mediaReferenceId = :mediaRefId")
    suspend fun deleteMediaOfRealtyById(mediaRefId: Long)
}

@Dao
interface AgentDao : BaseDao<Agent>
{
    @Query("SELECT name FROM Agent LIMIT 1")
    suspend fun getAgentName(): String

    @Query("SELECT * FROM Agent")
    suspend fun getAllAgents(): List<Agent>
}