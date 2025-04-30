package com.mKanta.archivemaps.network

import com.mKanta.archivemaps.BuildConfig
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

data class MemoEmbeddingInsertRequest(
    val marker_id: String,
    val memo: String,
    val embedding: List<Float>
)

interface SupabaseApi {
    @Headers("Prefer: resolution=merge-duplicates")
    @POST("memo_embeddings?on_conflict=marker_id")
    suspend fun upsertMemoEmbedding(
        @Body request: MemoEmbeddingInsertRequest
    ): Response<Unit>
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
