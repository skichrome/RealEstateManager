package com.skichrome.realestatemanager.model

import com.skichrome.realestatemanager.utils.GoogleRetrofitService
import com.skichrome.realestatemanager.utils.KtorRetrofitService

class RealtyRemoteRepository
{
    suspend fun getLatLngFromPhysicalAddress(address: String, postCode: Int, city: String) =
        GoogleRetrofitService.service.getLatLngFromPhysicalAddress(address = "$address+$postCode+$city")

    suspend fun getAllRealtyTypes() = KtorRetrofitService.service.getAllRealtyTypes()

    suspend fun getAllPoi() = KtorRetrofitService.service.getAllPoi()
}