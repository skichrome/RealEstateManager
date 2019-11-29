package com.skichrome.realestatemanager.utils

import com.skichrome.realestatemanager.model.retrofit.*
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.*

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

    @GET("all-poi-realty/agent-id={agent}")
    suspend fun getAllPoiRealty(@Path("agent") agent: Long): Response<RemotePoiRealty>

    @GET("all-realty/agent-id={agent}")
    suspend fun getAllRealty(@Path("agent") agent: Long): Response<RemoteRealty>

    @GET("media-references/agent-id={agent}")
    suspend fun getAllMediaReferences(@Path("agent") agent: Long): Response<RemoteMediaReference>

    // ---------- POST ---------- //

    @POST("all-agents")
    suspend fun uploadAllAgents(@Body agents: List<AgentResults>): Response<PostResponse>

    @POST("all-poi-realty")
    suspend fun uploadAllPoiRealty(@Body poiRealty: List<PoiRealtyResults>): Response<PostResponse>

    @POST("all-realty")
    suspend fun uploadAllRealty(@Body realty: List<RealtyResults>): Response<PostResponse>

    @Multipart
    @POST("media-references/upload")
    suspend fun uploadMediaReference(@Part("id") id: RequestBody,
                                     @Part("title") title: RequestBody,
                                     @Part("agent_id") agentId: RequestBody,
                                     @Part("realty_id") realtyId: RequestBody,
                                     @Part img: MultipartBody.Part
    ): Response<PostResponseMediaRef>

    @POST("media-references/delete-delta")
    suspend fun uploadMediaReferences(@Body mediaRefList: List<MediaReferenceResults>): Response<RemoteMediaReference>
}