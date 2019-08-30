package com.skichrome.realestatemanager.model.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Observable

@Dao
interface RealtyDao
{
    @Query("SELECT * FROM Realty")
    fun getAllRealty(): Flowable<List<Realty>>

    @Query("SELECT * FROM Realty WHERE id = :realtyId")
    fun getRealtyById(realtyId: Long): Flowable<Realty>

    @Insert
    fun insertRealty(vararg realty: Realty): Completable

    @Update
    fun updateRealty(realty: Realty): Completable

    @Query("DELETE FROM Realty WHERE id = :realtyId")
    fun deleteRealtyById(realtyId: Long): Completable
}

@Dao
interface PoiDao
{
    @Insert
    fun insertPoi(poi: Poi): Completable

    @Query("SELECT * FROM Poi")
    fun getAllPoi(): Flowable<List<Poi>>

    @Query("SELECT * FROM Poi WHERE poiId = :realtyId")
    fun getPoiOfRealtyById(realtyId: Long): Observable<Poi>

    @Update
    fun updatePoiOfRealty(poi: Poi): Completable

    @Query("DELETE FROM Poi WHERE poiId = :poiId")
    fun deletePoiOfRealtyById(poiId: Long): Completable
}

@Dao
interface RealtyTypeDao
{
    @Insert
    fun insertRealtyType(realtyType: RealtyType): Completable

    @Query("SELECT * FROM RealtyType")
    fun getAllRealtyType(): Flowable<List<RealtyType>>

    @Query("SELECT * FROM RealtyType WHERE realtyTypeId = :realtyId")
    fun getTypeOfRealtyById(realtyId: Long): Observable<RealtyType>

    @Update
    fun updateTypeOfRealty(realtyType: RealtyType): Completable

    @Query("DELETE FROM RealtyType WHERE realtyTypeId = :realtyId")
    fun deleteTypeOfRealtyById(realtyId: Long): Completable
}

@Dao
interface MediaReferenceDao
{
    @Insert
    fun insertMediaReference(mediaRef: MediaReference): Completable

    @Query("SELECT * FROM MediaReference WHERE mediaReferenceId = :realtyId")
    fun getMediaOfRealtyById(realtyId: Long): Observable<MediaReference>

    @Update
    fun updateMediaOfRealty(mediaReference: MediaReference): Completable

    @Query("DELETE FROM MediaReference WHERE mediaReferenceId = :mediaRefId")
    fun deleteMediaOfRealtyById(mediaRefId: Long): Completable
}