package com.mKanta.archivemaps.network

import com.mKanta.archivemaps.BuildConfig
import com.squareup.moshi.Json
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Query
import retrofit2.http.QueryMap

data class SimilarMemoRequest(
    @Json(name = "query_embedding")
    val queryEmbedding: List<Float>,
    @Json(name = "target_user_id")
    val targetUserId: String,
)

data class SimilarMemoResponse(
    @Json(name = "marker_id")
    val markerId: String,
    val memo: String,
    val similarity: Float,
)

data class MemoEmbeddingInsertRequest(
    @Json(name = "marker_id")
    val markerId: String,
    val memo: String,
    val embedding: List<Float>,
    @Json(name = "user_id")
    val userId: String,
)

interface SupabaseApi {
    @Headers("Prefer: resolution=merge-duplicates")
    @POST("memo_embeddings?on_conflict=marker_id")
    suspend fun upsertMemoEmbedding(
        @Body request: MemoEmbeddingInsertRequest,
    ): Response<Unit>

    @POST("rpc/match_memos_by_embedding")
    suspend fun getSimilarMemos(
        @Body request: SimilarMemoRequest,
    ): Response<List<SimilarMemoResponse>>

    @DELETE("memo_embeddings")
    suspend fun deleteMemoEmbedding(
        @QueryMap filters: Map<String, String>,
        @Query("select") select: String = "*",
    ): Response<Unit>

    @POST("rpc/delete_user")
    suspend fun deleteUserById(
        @Body body: Map<String, String>,
    ): Response<Unit>
}

fun provideSupabaseApi(
    supabaseUrl: String = "https://tnhcquguhtwjpzixskrw.supabase.co",
    supabaseKey: String = BuildConfig.SUPABASE_API_KEY,
): SupabaseApi {
    val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()

    val client =
        OkHttpClient
            .Builder()
            .addInterceptor { chain ->
                val request =
                    chain
                        .request()
                        .newBuilder()
                        .addHeader("apikey", supabaseKey)
                        .addHeader("Authorization", "Bearer $supabaseKey")
                        .addHeader("Content-Type", "application/json")
                        .build()
                chain.proceed(request)
            }.build()

    return Retrofit
        .Builder()
        .baseUrl("$supabaseUrl/rest/v1/")
        .client(client)
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .build()
        .create(SupabaseApi::class.java)
}
