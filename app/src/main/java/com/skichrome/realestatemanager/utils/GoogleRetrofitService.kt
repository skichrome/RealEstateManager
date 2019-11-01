package com.skichrome.realestatemanager.utils

import com.skichrome.realestatemanager.BuildConfig
import com.skichrome.realestatemanager.model.retrofit.GeoCoding
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface GoogleRetrofitService
{
    companion object
    {
        private const val BASE_URL = "https://maps.googleapis.com/maps/api/geocode/"
        private const val FORMAT = "json"
        private const val API_KEY = BuildConfig.GEOCODING_API_KEY

        private val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()

        private val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
        private val okHttpClient = OkHttpClient.Builder().apply {
            addInterceptor(logging)
        }

        val service: GoogleRetrofitService = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .client(okHttpClient.build())
            .build()
            .create(GoogleRetrofitService::class.java)
    }

    @GET("{outputFormat}")
    suspend fun getLatLngFromPhysicalAddress(
        @Path("outputFormat") format: String = FORMAT,
        @Query("address") address: String,
        @Query("key") apiKey: String = API_KEY
    ): Response<GeoCoding>
}