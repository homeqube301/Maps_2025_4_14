package com.mKanta.archivemaps.data.repository

import android.util.Log
import com.mKanta.archivemaps.data.local.MarkerMapper
import com.mKanta.archivemaps.data.local.MemoEmbeddingDto
import com.mKanta.archivemaps.domain.model.NamedMarker
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.postgrest
import javax.inject.Inject
import javax.inject.Singleton

interface MarkerDBRepository {
    suspend fun loadDBMarkers(): List<NamedMarker>
}

@Singleton
class MarkerDBRepositoryImpl
    @Inject
    constructor(
        private val supabaseClient: SupabaseClient,
        private val markerMapper: MarkerMapper,
    ) : MarkerDBRepository {
        override suspend fun loadDBMarkers(): List<NamedMarker> {
            val userId = supabaseClient.auth.currentUserOrNull()?.id ?: return emptyList()

            return try {
                supabaseClient
                    .postgrest["memo_embeddings"]
                    .select {
                        filter {
                            eq("user_id", userId)
                        }
                    }.decodeList<MemoEmbeddingDto>()
                    .map { markerMapper.toDomain(it) }
            } catch (e: Exception) {
                Log.e("MarkerRepository", "マーカーの読み込み失敗", e)
                emptyList()
            }
        }
    }

