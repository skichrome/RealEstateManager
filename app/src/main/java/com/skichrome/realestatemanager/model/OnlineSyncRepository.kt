package com.skichrome.realestatemanager.model

import com.skichrome.realestatemanager.androidmanagers.NetManager
import com.skichrome.realestatemanager.model.database.MediaReference
import com.skichrome.realestatemanager.model.retrofit.*
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File

class OnlineSyncRepository(
    private val netManager: NetManager,
    private val localDataSource: LocalRepository,
    private val remoteDataSource: RemoteRepository
)
{
    // =======================================
    //                 Methods
    // =======================================

    private fun isConnected(): Boolean = netManager.isConnectedToInternet ?: false

    private fun throwExceptionIfStatusIsFalse(status: Boolean, origin: String)
    {
        if (!status)
            throw Exception("An error occurred when trying to synchronize $origin")
    }

    // ---------- Currency Conversion Rate ---------- //

    suspend fun getCurrencyConversionRate(): Pair<Float, Float>?
    {
        if (isConnected())
        {
            val convRateResponse = remoteDataSource.getCurrencyConversionRate()
            throwExceptionIfStatusIsFalse(convRateResponse.isSuccessful, "Currency conversion rate (download)")

            convRateResponse.body()?.results?.let {
                return Pair(it.usdValue, it.eurValue)
            }
        }
        return null
    }

    // ---------- Poi & RealtyTypes ---------- //

    suspend fun synchronizePoi()
    {
        if (isConnected())
        {
            val poiRemote = remoteDataSource.getAllPoi()
            throwExceptionIfStatusIsFalse(poiRemote.isSuccessful, "poi (download)")

            poiRemote.body()?.results?.let {
                PoiResults.fromRemoteToLocal(it).apply {
                    localDataSource.insertPoiList(this.toTypedArray())
                }
            }
        }
    }

    suspend fun synchronizeRealtyTypes()
    {
        if (isConnected())
        {
            val poiRemote = remoteDataSource.getAllRealtyTypes()
            throwExceptionIfStatusIsFalse(poiRemote.isSuccessful, "Realty Types (download)")

            poiRemote.body()?.results?.let {
                RealtyTypeResults.fromRemoteToLocal(it).apply {
                    localDataSource.insertRealtyTypeList(this.toTypedArray())
                }
            }
        }
    }

    // ---------- Agents ---------- //

    suspend fun synchronizeAgents()
    {
        if (isConnected())
        {
            uploadAgents()
            downloadAgents()
        }
    }

    private suspend fun uploadAgents()
    {
        val agentLocal = AgentResults.fromLocalToRemote(localDataSource.getAllAgents())
        if (agentLocal.isEmpty())
            return
        val results = remoteDataSource.uploadAgents(agentLocal)

        val status = results.body()?.status ?: false
        throwExceptionIfStatusIsFalse(status, "agents (upload)")
    }

    private suspend fun downloadAgents()
    {
        val agentRemote = remoteDataSource.getAllAgents()
        throwExceptionIfStatusIsFalse(agentRemote.isSuccessful, "agents (download)")

        agentRemote.body()?.results?.let {
            AgentResults.fromRemoteToLocal(it).apply {
                localDataSource.insertAgentList(this.toTypedArray())
            }
        }
    }

    // ---------- PoiRealty ---------- //

    suspend fun synchronizePoiRealty(agentId: Long)
    {
        if (isConnected())
        {
            uploadPoiRealty(agentId)
            downloadPoiRealty(agentId)
        }
    }

    private suspend fun uploadPoiRealty(agentId: Long)
    {
        val poiRealtyRemote = PoiRealtyResults.fromLocalToRemote(localDataSource.getAllPoiRealty(), agentId)
        if (poiRealtyRemote.isEmpty())
            return
        val results = remoteDataSource.uploadPoiRealty(poiRealtyRemote)

        val status = results.body()?.status ?: false
        throwExceptionIfStatusIsFalse(status, "PoiRealty (upload)")
    }

    private suspend fun downloadPoiRealty(agentId: Long)
    {
        val poiRealtyRemote = remoteDataSource.getAllPoiRealty(agentId)
        throwExceptionIfStatusIsFalse(poiRealtyRemote.isSuccessful, "PoiRealty (download)")

        poiRealtyRemote.body()?.results?.let {
            PoiRealtyResults.fromRemoteToLocal(it).apply {
                localDataSource.insertPoiRealty(this.toTypedArray())
            }
        }
    }

    // ---------- Realty ---------- //

    suspend fun synchronizeRealty(agentId: Long)
    {
        if (isConnected())
        {
            uploadRealty()
            downloadRealty(agentId)
        }
    }

    private suspend fun uploadRealty()
    {
        val realtyRemote = RealtyResults.fromLocalToRemote(localDataSource.getAllRealty())
        if (realtyRemote.isEmpty())
            return
        val results = remoteDataSource.uploadRealty(realtyRemote)

        val status = results.body()?.status ?: false
        throwExceptionIfStatusIsFalse(status, "realty (upload)")
    }

    private suspend fun downloadRealty(agentId: Long)
    {
        val realtyRemote = remoteDataSource.getAllRealty(agentId)
        throwExceptionIfStatusIsFalse(realtyRemote.isSuccessful, "realty (download)")

        realtyRemote.body()?.results?.let {
            RealtyResults.fromRemoteToLocal(it).apply {
                localDataSource.insertRealtyList(this.toTypedArray())
            }
        }
    }

    // ---------- Media References ---------- //

    suspend fun synchronizeMediaReferences(agentId: Long)
    {
        if (isConnected())
        {
            val mediaRefToUpload = getMediaReferenceDeltaFromServer(agentId)
            uploadMediaReferences(mediaRefToUpload, agentId)
        }
    }

    private suspend fun getMediaReferenceDeltaFromServer(agentId: Long): RemoteMediaReference
    {
        val mediaRefList = MediaReferenceResults.fromLocalToRemote(localDataSource.getAllMediaReferences(), agentId)
        val result = remoteDataSource.uploadMediaRefList(mediaRefList)
        throwExceptionIfStatusIsFalse(result.isSuccessful, "MediaRefDelta")

        return result.body() ?: throw Exception("Error with body result of MediaRef delta method.")
    }

    private suspend fun uploadMediaReferences(mediaRefToUpload: RemoteMediaReference, agentId: Long)
    {
        val uploadList = mediaRefToUpload.results

        if (uploadList.isEmpty())
            return

        val mediaRefIdList = mutableListOf<Long>()
        uploadList.forEach {
            mediaRefIdList.add(it.id)
        }

        localDataSource.getMediaReferencesByIdList(mediaRefIdList).forEach {
            uploadMediaRefToServer(it.reference, it.shortDesc, agentId, it.realtyId, it.mediaReferenceId)
        }
    }

    private suspend fun uploadMediaRefToServer(path: String, title: String, agentId: Long, realtyId: Long, imgId: Long)
    {
        val file = File(path)
        val rb = RequestBody.create(MediaType.parse("media/*"), file)
        val mpb = MultipartBody.Part.createFormData("upload", file.name, rb)

        val titleRB = RequestBody.create(MultipartBody.FORM, title)
        val agentIdRB = RequestBody.create(MultipartBody.FORM, agentId.toString())
        val idRB = RequestBody.create(MultipartBody.FORM, imgId.toString())
        val realtyIdRB = RequestBody.create(MultipartBody.FORM, realtyId.toString())
        remoteDataSource.uploadMediaRef(id = idRB, imgFile = mpb, agentId = agentIdRB, title = titleRB, realtyId = realtyIdRB)
    }

    suspend fun getRemoteMediaReferences(currentAgentId: Long)
    {
        if (isConnected())
        {
            val remoteMediaRef = remoteDataSource.getAllMediaReferences(currentAgentId)
            throwExceptionIfStatusIsFalse(remoteMediaRef.isSuccessful, "MediaReferences (download)")

            remoteMediaRef.body()?.results?.let { MediaReferenceResults.fromRemoteToLocal(it) }
                ?.forEach { updateLocalMediaRefDatabaseAndFiles(it) }
        }
    }

    private suspend fun updateLocalMediaRefDatabaseAndFiles(mediaReference: MediaReference)
    {
        val file = File(mediaReference.reference)
        if (file.exists())
            file.delete()
        localDataSource.insertMediaReference(mediaReference)
        localDataSource.updateMediaReference(mediaReference)
    }
}