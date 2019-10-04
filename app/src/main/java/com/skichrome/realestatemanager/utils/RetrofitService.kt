package com.skichrome.realestatemanager.utils

import com.skichrome.realestatemanager.BuildConfig
import com.skichrome.realestatemanager.model.retrofit.MainGeocoding
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface RetrofitService
{
    companion object
    {
        private const val BASE_URL = "https://maps.googleapis.com/maps/api/geocode/"
        private const val FORMAT = "json"
        private const val API_KEY = BuildConfig.GEOCODING_API_KEY

        val retrofitService: RetrofitService = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(RetrofitService::class.java)
    }

    @GET("{outputFormat}")
    suspend fun getLatLngFromPhysicalAddress(
        @Path("outputFormat") format: String = FORMAT,
        @Query("address") address: String,
        @Query("key") apiKey: String = API_KEY
    ): Response<MainGeocoding>
}