package com.example.maps20250414.network

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.Dns
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.HttpException
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
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
        @Query("format") format: String = "json"
    ): Call<NominatimResponse>
}

@JsonClass(generateAdapter = true)
data class EmbeddingRequest(
    @Json(name = "input") val input: String,
    @Json(name = "model") val model: String = "text-embedding-ada-002"
)

data class EmbeddingResponse(
    @Json(name = "data") val data: List<EmbeddingData>
)

@JsonClass(generateAdapter = true)
data class EmbeddingData(
    @Json(name = "embedding") val embedding: List<Float>,
    @Json(name = "index") val index: Int
)

interface OpenAiApi {
    @POST("embeddings")
    suspend fun getEmbedding(
        @Body request: EmbeddingRequest
    ): EmbeddingResponse
}

fun provideOpenAiApi(apiKey: String): OpenAiApi {
    val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()
    val client = OkHttpClient.Builder()
        .dns(Dns.SYSTEM)
        .addInterceptor { chain ->
            val request = chain.request().newBuilder()
                .addHeader("Authorization", "Bearer $apiKey")
                .addHeader("Content-Type", "application/json")
                .build()
            chain.proceed(request)
        }
        .build()

    return Retrofit.Builder()
        .baseUrl("https://api.openai.com/v1/")
        .client(client)
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .build()
        .create(OpenAiApi::class.java)
}

suspend fun fetchEmbedding(api: OpenAiApi, inputText: String): List<Float>? {
    return try {
        val request = EmbeddingRequest(input = inputText)
        val response = api.getEmbedding(request)
        response.data.firstOrNull()?.embedding
    } catch (e: HttpException) {
        val errorBody = e.response()?.errorBody()?.string()
        null
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}

// ベクトルを保存するためのState
//var embedding by remember { mutableStateOf<List<Float>?>(null) }
//
//val apiKey = BuildConfig.OPENAI_API_KEY
//
//LaunchedEffect(Unit) {
//    val openAiApi = provideOpenAiApi(apiKey)
//    val inputText = "こんにちは、世界！"
//    val result = fetchEmbedding(openAiApi, inputText)
//    if (result != null) {
//        embedding = result
//        Log.d("Embeddin", "ベクトル取得成功！サイズ: ${result.size}")
//    } else {
//        Log.e("Embeddin", "ベクトル取得失敗")
//    }
//}
//
//if (embedding != null) {
//    Text("ベクトルサイズ: ${embedding!!.size}")
//    Text("最初の要素: ${embedding!!.first()}")
//} else {
//    Text("Embedding取得中、または失敗しました")
//}
