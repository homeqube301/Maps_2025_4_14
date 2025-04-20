package com.example.maps_2025_4_14.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.Call

@JsonClass(generateAdapter = true)
data class NominatimResponse(
    @Json(name = "display_name")
    val display_name: String
)

interface NominatimApiService {
    @GET("reverse")
    fun reverseGeocode(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("format") format: String = "json"
    ): Call<NominatimResponse>
}