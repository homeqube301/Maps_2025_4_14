package com.mKanta.archivemaps.network

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

@JsonClass(generateAdapter = true)
data class NominatimResponse(
    @Json(name = "display_name")
    val displayName: String? = null,
)

interface NominatimApiService {
    @GET("reverse")
    fun reverseGeocode(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("format") format: String = "json",
    ): Call<NominatimResponse>
}