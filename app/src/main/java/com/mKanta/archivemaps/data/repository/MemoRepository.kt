package com.mKanta.archivemaps.data.repository

import android.util.Log
import com.mKanta.archivemaps.network.MemoEmbeddingInsertRequest
import com.mKanta.archivemaps.network.OpenAiApi
import com.mKanta.archivemaps.network.SimilarMemoRequest
import com.mKanta.archivemaps.network.SimilarMemoResponse
import com.mKanta.archivemaps.network.SupabaseApi
import com.mKanta.archivemaps.network.fetchEmbedding
import javax.inject.Inject

class MemoRepository
    @Inject
    constructor(
        private val openAiApi: OpenAiApi,
        private val supabaseApi: SupabaseApi,
    ) {
        suspend fun saveMemoEmbedding(
            markerId: String,
            memoText: String,
        ): Boolean {
            val embedding = fetchEmbedding(openAiApi, memoText) ?: return false

            val request =
                MemoEmbeddingInsertRequest(
                    markerId = markerId,
                    memo = memoText,
                    embedding = embedding,
                )

            return try {
                val response = supabaseApi.upsertMemoEmbedding(request)
                Log.d("Supabase", "マーカーのメモを更新: $memoText と $embedding")
                response.isSuccessful
            } catch (e: Exception) {
                Log.d("Supabase", "マーカーのメモを更新できませんでした")
                e.printStackTrace()
                false
            }
        }

        suspend fun getSimilarMarkerIds(memoText: String): List<String>? {
            val embedding = fetchEmbedding(openAiApi, memoText) ?: return null
            val similarMemos = getSimilarMemos(embedding) ?: return null
            return similarMemos.map { it.markerId }
        }

        private suspend fun getSimilarMemos(embedding: List<Float>): List<SimilarMemoResponse>? =
            try {
                val response = supabaseApi.getSimilarMemos(SimilarMemoRequest(embedding))
                if (response.isSuccessful) {
                    Log.e("MemoRepository", "getSimilarMemos: 成功 ${response.body() ?: "何もないよ"}")
                    response.body()
                } else {
                    Log.e("MemoRepository", "getSimilarMemos: エラー ${response.errorBody()?.string()}")
                    null
                }
            } catch (e: Exception) {
                Log.e("MemoRepository", "getSimilarMemos: 例外", e)
                null
            }
    }
