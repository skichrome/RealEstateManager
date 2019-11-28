package com.skichrome.realestatemanager.model

import android.util.Log
import androidx.sqlite.db.SimpleSQLiteQuery
import com.skichrome.realestatemanager.androidmanagers.NetManager
import com.skichrome.realestatemanager.model.database.*
import com.skichrome.realestatemanager.model.retrofit.Results

class RealtyRepository(
    private val netManager: NetManager,
    private val localDataSource: LocalRepository,
    private val remoteDataSource: RemoteRepository
)
{
    private fun isConnected(): Boolean = netManager.isConnectedToInternet ?: false

    // ---------- Realty ---------- //

    suspend fun getAllRealty(): List<Realty> = localDataSource.getAllRealty()

    suspend fun getRealty(id: Long): Realty = localDataSource.getRealtyById(id)

    suspend fun updateRealty(realty: Realty): Int = localDataSource.updateRealty(realty)

    suspend fun insertRealty(realty: Realty): Long = localDataSource.insertRealty(realty)

    suspend fun getRealtyLatitudeNotDefined() = localDataSource.getRealtyWithoutLatLngDefined()

    // ---------- MediaReference ---------- //

    suspend fun insertMediaReferences(medias: List<MediaReference?>, realtyId: Long) = medias.forEach {
        it?.let {
            it.realtyId = realtyId
            localDataSource.insertMediaReference(it)
        }
    }

    suspend fun updateMediaReferences(medias: List<MediaReference?>, realtyId: Long) = medias.forEach {
        it?.let {
            it.realtyId = realtyId
            localDataSource.insertMediaReference(it)
        }
    }

    suspend fun getMediaReferencesFromRealty(id: Long): List<MediaReference> = localDataSource.getMediaReferencesFromRealtyId(id)

    suspend fun deleteMediaReference(mediaId: Long) = localDataSource.deleteMediaReference(mediaId)

    // ---------- Agent ---------- //

    suspend fun getAllAgents(): List<Agent> = localDataSource.getAllAgents()

    // ---------- RealtyType ---------- //

    suspend fun getAllRealtyTypes(): List<RealtyType>
    {
        if (isConnected())
        {
            val remoteResult = remoteDataSource.getAllRealtyTypes()
            if (remoteResult.isSuccessful)
            {
                val resultList: MutableList<RealtyType> = mutableListOf()
                remoteResult.body()?.results?.forEach {
                    val realtyType = RealtyType(realtyTypeId = it.id, name = it.name)
                    localDataSource.insertRealtyType(realtyType)
                    resultList.add(realtyType)
                }
                return resultList
            }
        }
        return localDataSource.getAllRealtyTypes()
    }

    // ---------- Poi ---------- //

    suspend fun getAllPoi(): List<Poi>
    {
        if (isConnected())
        {
            val remoteResult = remoteDataSource.getAllPoi()
            if (remoteResult.isSuccessful)
            {
                val resultList: MutableList<Poi> = mutableListOf()
                remoteResult.body()?.results?.forEach {
                    val poi = Poi(poiId = it.id, name = it.name)
                    localDataSource.insertPoi(poi)
                    resultList.add(poi)
                }
                return resultList
            }
        }
        return localDataSource.getAllPoi()
    }

    // ---------- PoiRealty ---------- //

    suspend fun getAllPoiRealtyFromRealtyId(realtyId: Long): List<PoiRealty> = localDataSource.getPoiRealtyFromRealtyId(realtyId)

    suspend fun insertPoiRealty(poiRealty: Array<PoiRealty>) = localDataSource.insertPoiRealty(poiRealty)

    suspend fun deletePoiRealtyFromRealtyId(realtyId: Long) = localDataSource.deletePoiRealtyByRealtyId(realtyId)

    // ---------- LatLng ---------- //

    suspend fun getLatLngFromAddress(address: String, postCode: Int, city: String): List<Results>?
    {
        if (isConnected())
        {
            val result = remoteDataSource.getLatLngFromPhysicalAddress(address, postCode, city)
            if (result.isSuccessful)
                return result.body()?.results
        }
        return null
    }

    // ---------- Search ---------- //

    suspend fun searchFromParameters(
        minPrice: Int?,
        maxPrice: Int?,
        poiList: List<Int>?,
        minSurface: Int?,
        maxSurface: Int?
    ): List<Realty>
    {
        val baseQueryParam = "SELECT * FROM Realty"
        val queryStrBuilder = StringBuilder(baseQueryParam)

        minPrice?.let { queryStrBuilder.append(" AND Realty.price >= $it") }
        maxPrice?.let { queryStrBuilder.append(" AND Realty.price <= $it") }
        minSurface?.let { queryStrBuilder.append(" AND Realty.surface >= $it") }
        maxSurface?.let { queryStrBuilder.append(" AND Realty.surface <= $it") }

        Log.e("RealtyRepository", "List of poi (TODO) : $poiList")

//        poiList?.let { poiListNotNull ->
//            poiListNotNull.forEach {
        //queryStrBuilder.append(" AND PoiRealty.poiId = $it")
//            }
//        }

        if (queryStrBuilder.toString() == baseQueryParam)
            return localDataSource.fetchRealtyFromQueryParam(SimpleSQLiteQuery(baseQueryParam))

        val queryStr = queryStrBuilder.toString().replaceFirst("AND", "WHERE")

        Log.e("RealtyRepo", "Query Value : $queryStr")

        val query = SimpleSQLiteQuery(queryStr)
        val queryResult = localDataSource.fetchRealtyFromQueryParam(query)
        queryResult.forEach {
            Log.e("RealtyRepo", it.toString())
        }

        return queryResult
    }
}