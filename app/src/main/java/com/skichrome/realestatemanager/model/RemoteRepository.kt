package com.skichrome.realestatemanager.model

import com.skichrome.realestatemanager.model.retrofit.AgentResults
import com.skichrome.realestatemanager.model.retrofit.MediaReferenceResults
import com.skichrome.realestatemanager.model.retrofit.PoiRealtyResults
import com.skichrome.realestatemanager.model.retrofit.RealtyResults
import com.skichrome.realestatemanager.utils.GoogleRetrofitService
import com.skichrome.realestatemanager.utils.KtorRetrofitService
import okhttp3.MultipartBody
import okhttp3.RequestBody

class RemoteRepository
{
    // ---------- GET ---------- //

    suspend fun getLatLngFromPhysicalAddress(address: String, postCode: Int, city: String) =
        GoogleRetrofitService.service.getLatLngFromPhysicalAddress(address = "$address+$postCode+$city")

    suspend fun getAllRealty(agentId: Long) = KtorRetrofitService.service.getAllRealty(agentId)

    suspend fun getAllRealtyTypes() = KtorRetrofitService.service.getAllRealtyTypes()

    suspend fun getAllPoi() = KtorRetrofitService.service.getAllPoi()

    suspend fun getAllAgents() = KtorRetrofitService.service.getAllAgents()

    suspend fun getAllPoiRealty(agentId: Long) = KtorRetrofitService.service.getAllPoiRealty(agentId)

    suspend fun getAllMediaReferences(agentId: Long) = KtorRetrofitService.service.getAllMediaReferences(agent = agentId)

    // ---------- POST ---------- //

    suspend fun uploadAgents(agents: List<AgentResults>) = KtorRetrofitService.service.uploadAllAgents(agents)

    suspend fun uploadPoiRealty(poiRealtyList: List<PoiRealtyResults>) = KtorRetrofitService.service.uploadAllPoiRealty(poiRealtyList)

    suspend fun uploadRealty(realtyList: List<RealtyResults>) = KtorRetrofitService.service.uploadAllRealty(realtyList)

    suspend fun uploadMediaRefList(mrfList: List<MediaReferenceResults>) = KtorRetrofitService.service.uploadMediaReferences(mediaRefList = mrfList)

    suspend fun uploadMediaRef(id: RequestBody, title: RequestBody, agentId: RequestBody, realtyId: RequestBody, imgFile: MultipartBody.Part) =
        KtorRetrofitService.service.uploadMediaReference(id = id, title = title, agentId = agentId, realtyId = realtyId, img = imgFile)
}