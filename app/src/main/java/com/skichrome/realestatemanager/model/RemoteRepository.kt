package com.skichrome.realestatemanager.model

import com.skichrome.realestatemanager.model.retrofit.AgentResults
import com.skichrome.realestatemanager.model.retrofit.PoiRealtyResults
import com.skichrome.realestatemanager.model.retrofit.RealtyResults
import com.skichrome.realestatemanager.utils.GoogleRetrofitService
import com.skichrome.realestatemanager.utils.KtorRetrofitService

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

    // ---------- POST ---------- //

    suspend fun updateAgent(agent: AgentResults) = KtorRetrofitService.service.uploadAgent(agent)

    suspend fun uploadAgents(agents: List<AgentResults>) = KtorRetrofitService.service.uploadAllAgents(agents)

    suspend fun uploadPoiRealty(poiRealtyList: List<PoiRealtyResults>) = KtorRetrofitService.service.uploadAllPoiRealty(poiRealtyList)

    suspend fun uploadRealty(realtyList: List<RealtyResults>) = KtorRetrofitService.service.uploadAllRealty(realtyList)
}