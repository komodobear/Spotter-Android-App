package com.example.spotter.location

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

object LocationRetrofit{
    private const val BaseURL = "https://maps.googleapis.com/"

    fun createR(): GeoCodingApiService{
        val retrofit = Retrofit
            .Builder()
            .baseUrl(BaseURL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        return retrofit.create(GeoCodingApiService::class.java)
    }
}

interface GeoCodingApiService{
    @GET("maps/api/geocode/json")
    suspend fun getAddressFromCoordinates(
        @Query("latlng") latlng: String,
        @Query("key") apiKey: String
    ): GeocodingResponse
}