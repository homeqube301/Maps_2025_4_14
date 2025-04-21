package com.example.maps20250414.model

import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

object NominatimApiClient {
    val apiService: NominatimApiService by lazy {
        Retrofit.Builder()
            .baseUrl("https://nominatim.openstreetmap.org/")
            .addConverterFactory(MoshiConverterFactory.create())
            .build()
            .create(NominatimApiService::class.java)
    }
}