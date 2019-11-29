package com.skichrome.realestatemanager.model.database

import android.database.Cursor
import androidx.room.*
import androidx.sqlite.db.SupportSQLiteQuery

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
}

@Dao
interface RealtyDao : BaseDao<Realty>
{
    @Query("SELECT * FROM Realty")
    suspend fun getAllRealty(): List<Realty>

    @Query("SELECT * FROM Realty WHERE id = :realtyId")
    suspend fun getRealtyById(realtyId: Long): Realty

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
}

@Dao
interface RealtyTypeDao : BaseDao<RealtyType>
{
    @Query("SELECT * FROM RealtyType")
    suspend fun getAllRealtyTypes(): List<RealtyType>
}

@Dao
interface MediaReferenceDao : BaseDao<MediaReference>
{
    @Query("SELECT * FROM MediaReference")
    suspend fun getAllMedias(): List<MediaReference>

    @Query("SELECT * FROM MediaReference WHERE realtyId = :realtyId")
    suspend fun getMediaOfRealtyById(realtyId: Long): List<MediaReference>

    @Query("SELECT reference FROM MediaReference WHERE realtyId = :realtyId LIMIT 1")
    suspend fun getFirstMediaOfRealtyById(realtyId: Long): String?

    @Query("SELECT * FROM MediaReference WHERE mediaReferenceId IN (:mediaRefIdList)")
    suspend fun getMediaByIdList(mediaRefIdList: List<Long>): List<MediaReference>

    @Query("SELECT COUNT(mediaReferenceId) FROM MediaReference WHERE realtyId = :realtyId")
    suspend fun getMediaReferenceNumberFromRealtyId(realtyId: Long): Long

    @Query("DELETE FROM MediaReference WHERE mediaReferenceId = :mediaRefId")
    suspend fun deleteMediaOfRealtyById(mediaRefId: Long)
}

@Dao
interface PoiRealtyDao : BaseDao<PoiRealty>
{
    @Query("SELECT * FROM PoiRealty")
    suspend fun getAllPoiRealty(): List<PoiRealty>

    @Query("SELECT poiId FROM PoiRealty WHERE realtyId = :realtyId")
    suspend fun getPoIRealtyFromRealtyId(realtyId: Long): List<Int>

    @Query("SELECT realtyId FROM PoiRealty WHERE poiId IN (:poiList)")
    suspend fun getRealtyIdListFromPoiIdList(poiList: List<Int>): List<Long>

    @Query("DELETE FROM PoiRealty WHERE realtyId = :realtyId")
    suspend fun deletePoiRealtyFromRealtyId(realtyId: Long): Int
}

@Dao
interface AgentDao : BaseDao<Agent>
{
    @Query("SELECT * FROM Agent")
    suspend fun getAllAgents(): List<Agent>
}

@Dao
interface RawQueryDao
{
    @RawQuery(observedEntities = [Realty::class])
    suspend fun getRealtyFromRaw(sqlQuery: SupportSQLiteQuery): List<Realty>
}