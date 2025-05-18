package com.mKanta.archivemaps.data.repository

import android.util.Log
import com.mKanta.archivemaps.network.MemoEmbeddingInsertRequest
import com.mKanta.archivemaps.network.SimilarMemoRequest
import com.mKanta.archivemaps.network.SimilarMemoResponse
import com.mKanta.archivemaps.network.SupabaseApi
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import javax.inject.Inject

interface MemoRepository {
    suspend fun saveMemoEmbedding(
        markerId: String,
        memoText: String,
    ): Boolean

    suspend fun getSimilarMarkerIds(memoText: String): List<String>?

    suspend fun deleteMemoEmbedding(markerId: String): Boolean
}

class MemoRepositoryImpl
    @Inject
    constructor(
        private val supabaseApi: SupabaseApi,
        private val embeddingRepository: EmbeddingRepository,
        private val supabaseClient: SupabaseClient,
    ) : MemoRepository {
        override suspend fun saveMemoEmbedding(
            markerId: String,
            memoText: String,
        ): Boolean {
            val embedding = embeddingRepository.fetchEmbedding(memoText) ?: return false
            val currentUserId =
                supabaseClient.auth.currentUserOrNull()?.id
                    ?: throw Exception("ユーザーが見つかりません")

            val request =
                MemoEmbeddingInsertRequest(
                    markerId = markerId,
                    memo = memoText,
                    embedding = embedding,
                    userId = currentUserId,
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

        override suspend fun getSimilarMarkerIds(memoText: String): List<String>? {
            val embedding = embeddingRepository.fetchEmbedding(memoText) ?: return null
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

        override suspend fun deleteMemoEmbedding(markerId: String): Boolean =
            try {
                val response =
                    supabaseApi.deleteMemoEmbedding(
                        filters = mapOf("marker_id" to "eq.$markerId"),
                    )
                Log.d("Supabase", "マーカーのメモを削除: $markerId")
                response.isSuccessful
            } catch (e: Exception) {
                Log.e("Supabase", "マーカーのメモを削除できませんでした: $markerId", e)
                false
            }
    }
