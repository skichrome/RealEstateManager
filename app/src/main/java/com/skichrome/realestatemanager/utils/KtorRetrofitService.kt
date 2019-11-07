package com.skichrome.realestatemanager.utils

import com.skichrome.realestatemanager.model.retrofit.*
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

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

    // ---------- GET ---------- //

    @GET("currency-conversion-rate")
    suspend fun getCurrentConversionRate(): Response<ConversionRate>

    @GET("all-realty-types")
    suspend fun getAllRealtyTypes(): Response<RemoteRealtyType>

    @GET("all-poi")
    suspend fun getAllPoi(): Response<RemotePoi>

    @GET("all-agents")
    suspend fun getAllAgents(): Response<RemoteAgent>

    @GET("all-poi-realty")
    suspend fun getAllPoiRealty(): Response<RemotePoiRealty>

    @GET("all-realty")
    suspend fun getAllRealty(): Response<RemoteRealty>

    // ---------- POST ---------- //

    @POST("agent/update")
    suspend fun uploadAgent(@Body agent: AgentResults): Response<PostResponse>

    @POST("all-agents")
    suspend fun uploadAllAgents(@Body agents: List<AgentResults>): Response<PostResponse>

    @POST("all-poi-realty")
    suspend fun uploadAllPoiRealty(@Body poiRealty: List<PoiRealtyResults>): Response<PostResponse>

    @POST("all-realty")
    suspend fun uploadAllRealty(@Body realty: List<RealtyResults>): Response<PostResponse>
}