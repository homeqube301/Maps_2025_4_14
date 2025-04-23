package com.example.maps20250414.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

@JsonClass(generateAdapter = true)
data class NominatimResponse(
    @Json(name = "display_Name")
    val displayName: String? = null,
)

interface NominatimApiService {
    @GET("reverse")
    fun reverseGeocode(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("format") format: String = "json"
    ): Call<NominatimResponse>
}

//interface OpenAiApiService {
//    @POST("v1/embeddings")
//    fun getEmbedding(
//        @Header("Authorization") apiKey: String,
//        @Body request: EmbeddingRequest
//    ): Call<EmbeddingResponse>
//}