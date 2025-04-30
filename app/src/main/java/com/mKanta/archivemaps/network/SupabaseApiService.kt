package com.mKanta.archivemaps.network

import com.mKanta.archivemaps.BuildConfig
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST

data class MemoEmbeddingInsertRequest(
    val marker_id: String,
    val memo: String,
    val embedding: List<Float>
)

interface SupabaseApi {
    @POST("memo_embeddings")
    suspend fun insertMemoEmbedding(
        @Body request: MemoEmbeddingInsertRequest
    ): Response<Unit>  // Supabaseからの応答に応じて変えてください
}

fun provideSupabaseApi(
    supabaseUrl: String = "https://tnhcquguhtwjpzixskrw.supabase.co",
    supabaseKey: String = BuildConfig.SUPABASE_API_KEY
): SupabaseApi {
    val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()

    val client = OkHttpClient.Builder()
        .addInterceptor { chain ->
            val request = chain.request().newBuilder()
                .addHeader("apikey", supabaseKey)
                .addHeader("Authorization", "Bearer $supabaseKey")
                .addHeader("Content-Type", "application/json")
                .build()
            chain.proceed(request)
        }.build()

    return Retrofit.Builder()
        .baseUrl("$supabaseUrl/rest/v1/")
        .client(client)
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .build()
        .create(SupabaseApi::class.java)
}

suspend fun insertMemoWithEmbedding(
    openAiApi: OpenAiApi,
    supabaseApi: SupabaseApi,
    markerId: String,
    memoText: String
): Boolean {
    val embedding = fetchEmbedding(openAiApi, memoText) ?: return false

    val request = MemoEmbeddingInsertRequest(
        marker_id = markerId,
        memo = memoText,
        embedding = embedding
    )

    return try {
        val response = supabaseApi.insertMemoEmbedding(request)
        response.isSuccessful
    } catch (e: Exception) {
        e.printStackTrace()
        false
    }
}

