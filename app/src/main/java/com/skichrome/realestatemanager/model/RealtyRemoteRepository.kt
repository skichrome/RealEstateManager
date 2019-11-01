package com.skichrome.realestatemanager.model

import com.skichrome.realestatemanager.model.retrofit.AgentResults
import com.skichrome.realestatemanager.utils.GoogleRetrofitService
import com.skichrome.realestatemanager.utils.KtorRetrofitService

class RealtyRemoteRepository
{
    // ---------- GET ---------- //

    suspend fun getLatLngFromPhysicalAddress(address: String, postCode: Int, city: String) =
        GoogleRetrofitService.service.getLatLngFromPhysicalAddress(address = "$address+$postCode+$city")

    suspend fun getAllRealtyTypes() = KtorRetrofitService.service.getAllRealtyTypes()

    suspend fun getAllPoi() = KtorRetrofitService.service.getAllPoi()

    suspend fun getAllAgents() = KtorRetrofitService.service.getAllAgents()

    // ---------- POST ---------- //

    suspend fun uploadAgents(agents: List<AgentResults>) = KtorRetrofitService.service.uploadAllAgents(agents)
}