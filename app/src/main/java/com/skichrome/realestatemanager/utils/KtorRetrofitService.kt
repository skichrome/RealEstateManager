package com.skichrome.realestatemanager.utils

import com.skichrome.realestatemanager.model.retrofit.RemoteAgent
import com.skichrome.realestatemanager.model.retrofit.RemotePoi
import com.skichrome.realestatemanager.model.retrofit.RemoteRealtyType
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET

interface KtorRetrofitService
{
    companion object
    {
        private const val BASE_URL = "https://realestatemanager.ktor.campeoltoni.fr/real-estate/"

        private val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()

        val service: KtorRetrofitService = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
            .create(KtorRetrofitService::class.java)
    }

    @GET("all-realty-types")
    suspend fun getAllRealtyTypes(): Response<RemoteRealtyType>

    @GET("all-poi")
    suspend fun getAllPoi(): Response<RemotePoi>

    @GET("all-agents")
    suspend fun getAllAgents(): Response<RemoteAgent>
}