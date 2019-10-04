package com.skichrome.realestatemanager.model.retrofit

import com.skichrome.realestatemanager.utils.RetrofitService

class RealtyRemoteRepository
{
    suspend fun getLatLngFromPhysicalAddress(address: String, postCode: Int, city: String) =
        RetrofitService.retrofitService.getLatLngFromPhysicalAddress(address = "$address+$postCode+$city")
}