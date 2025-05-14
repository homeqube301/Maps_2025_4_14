package com.mKanta.archivemaps.data.repository

import android.util.Log
import com.mKanta.archivemaps.network.EmbeddingRequest
import com.mKanta.archivemaps.network.OpenAiApi
import retrofit2.HttpException
import javax.inject.Inject

interface EmbeddingRepository {
    suspend fun fetchEmbedding(inputText: String): List<Float>?
}

class EmbeddingRepositoryImpl
    @Inject
    constructor(
        private val api: OpenAiApi,
    ) : EmbeddingRepository {
        override suspend fun fetchEmbedding(inputText: String): List<Float>? =
            try {
                val request = EmbeddingRequest(input = inputText)
                val response = api.getEmbedding(request)
                Log.d("EmbeddingRepository", "取得成功: $response")
                response.data.firstOrNull()?.embedding
            } catch (e: HttpException) {
                Log.e("EmbeddingRepository", "HTTPエラー: ${e.message()}")
                null
            } catch (e: Exception) {
                Log.e("EmbeddingRepository", "予期しないエラー", e)
                null
            }
    }
