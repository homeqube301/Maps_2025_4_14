package com.mKanta.archivemaps.data.repository

import android.util.Log
import com.mKanta.archivemaps.network.MemoEmbeddingInsertRequest
import com.mKanta.archivemaps.network.OpenAiApi
import com.mKanta.archivemaps.network.SupabaseApi
import com.mKanta.archivemaps.network.fetchEmbedding
import javax.inject.Inject

class MemoRepository @Inject constructor(
    private val openAiApi: OpenAiApi,
    private val supabaseApi: SupabaseApi,
) {
    suspend fun saveMemoEmbedding(markerId: String, memoText: String): Boolean {
        val embedding = fetchEmbedding(openAiApi, memoText) ?: return false

        val request = MemoEmbeddingInsertRequest(
            marker_id = markerId,
            memo = memoText,
            embedding = embedding
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
}