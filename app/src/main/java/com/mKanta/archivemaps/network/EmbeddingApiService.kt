package com.mKanta.archivemaps.network

import com.mKanta.archivemaps.BuildConfig
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST

@JsonClass(generateAdapter = true)
data class EmbeddingRequest(
    @Json(name = "input") val input: String,
    @Json(name = "model") val model: String = "text-embedding-ada-002",
)

data class EmbeddingResponse(
    @Json(name = "data") val data: List<EmbeddingData>,
)

@JsonClass(generateAdapter = true)
data class EmbeddingData(
    @Json(name = "embedding") val embedding: List<Float>,
    @Json(name = "index") val index: Int,
)

interface OpenAiApi {
    @POST("embeddings")
    suspend fun getEmbedding(
        @Body request: EmbeddingRequest,
    ): EmbeddingResponse
}

fun provideOpenAiApi(apiKey: String = BuildConfig.OPENAI_API_KEY): OpenAiApi {
    val moshi =
        Moshi
            .Builder()
            .add(KotlinJsonAdapterFactory())
            .build()
    val client =
        OkHttpClient
            .Builder()
            .addInterceptor { chain ->
                val request =
                    chain
                        .request()
                        .newBuilder()
                        .addHeader("Authorization", "Bearer $apiKey")
                        .addHeader("Content-Type", "application/json")
                        .build()
                chain.proceed(request)
            }.build()

    return Retrofit
        .Builder()
        .baseUrl("https://api.openai.com/v1/")
        .client(client)
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .build()
        .create(OpenAiApi::class.java)
}