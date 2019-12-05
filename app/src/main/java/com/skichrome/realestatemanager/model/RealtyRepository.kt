package com.skichrome.realestatemanager.model

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

    suspend fun getFirstMediaReferenceFromRealty(realtyId: Long) = localDataSource.getFirstMediaReferenceFromRealty(realtyId)

    suspend fun getMediaReferencesFromRealty(id: Long): List<MediaReference> = localDataSource.getMediaReferencesFromRealtyId(id)

    suspend fun deleteMediaReference(mediaId: Long) = localDataSource.deleteMediaReference(mediaId)

    // ---------- Agent ---------- //

    suspend fun getAllAgents(): List<Agent> = localDataSource.getAllAgents()

    // ---------- Poi & RealtyTypes ---------- //

    suspend fun getAllPoi(): List<Poi> = localDataSource.getAllPoi()

    suspend fun getAllRealtyTypes(): List<RealtyType> = localDataSource.getAllRealtyTypes()

    // ---------- PoiRealty ---------- //

    suspend fun getAllPoiRealtyFromRealtyId(realtyId: Long): List<Int> = localDataSource.getPoiRealtyFromRealtyId(realtyId)

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
        maxSurface: Int?,
        isSold: Int?,
        creationDate: Long?,
        soldDate: Long?,
        mediaRefMinNumber: Int?,
        postCode: Int?
    ): List<Realty>
    {
        val realtyResults = mutableListOf<Realty>()
        val baseQueryParam = "SELECT * FROM Realty"
        val queryStrBuilder = StringBuilder(baseQueryParam)

        minPrice?.let { queryStrBuilder.append(" AND Realty.price >= $it") }
        maxPrice?.let { queryStrBuilder.append(" AND Realty.price <= $it") }
        minSurface?.let { queryStrBuilder.append(" AND Realty.surface >= $it") }
        maxSurface?.let { queryStrBuilder.append(" AND Realty.surface <= $it") }
        isSold?.let { queryStrBuilder.append(" AND Realty.status == $it") }
        creationDate?.let { queryStrBuilder.append(" AND Realty.dateAdded >= $it") }
        soldDate?.let { queryStrBuilder.append(" AND Realty.dateSell >= $it") }
        postCode?.let { queryStrBuilder.append(" AND Realty.postCode == $it") }

        if (queryStrBuilder.toString() == baseQueryParam && poiList.isNullOrEmpty() && mediaRefMinNumber == null)
            return localDataSource.fetchRealtyFromQueryParam(SimpleSQLiteQuery(baseQueryParam))

        val queryResult = queryStrBuilder.toString()
            .replaceFirst("AND", "WHERE")
            .let {
                localDataSource.fetchRealtyFromQueryParam(SimpleSQLiteQuery(it))
            }

        if (poiList.isNullOrEmpty() && mediaRefMinNumber == null)
            return queryResult

        poiList?.let { poiListNotNull ->
            queryResult.filter {
                val poiFromRealty = localDataSource.getRealtyIdListFromPoiIdList(poiListNotNull)
                poiFromRealty.contains(it.id)
            }.let {
                realtyResults.addAll(it)
            }
        }

        mediaRefMinNumber?.let { mediaRefMinNumberNotNull ->
            if (realtyResults.isEmpty())
            {
                queryResult.forEach {
                    val realtyMediaRefNumber = localDataSource.getMediaReferenceCountFromRealtyId(it.id)
                    if (realtyMediaRefNumber >= mediaRefMinNumberNotNull)
                        realtyResults.add(it)
                }
            } else
            {
                realtyResults.forEach {
                    val realtyMediaRefNumber = localDataSource.getMediaReferenceCountFromRealtyId(it.id)
                    if (realtyMediaRefNumber <= mediaRefMinNumberNotNull)
                        realtyResults.remove(it)
                }
            }
        }
        return realtyResults
    }
}