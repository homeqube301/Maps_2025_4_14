package com.mKanta.archivemaps.data.repository

import android.util.Log
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.mKanta.archivemaps.domain.model.LatLngSerializable
import com.mKanta.archivemaps.domain.model.NamedMarker
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
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
                    .map { it.toNamedMarker() }
            } catch (e: Exception) {
                Log.e("MarkerRepository", "マーカーの読み込み失敗", e)
                emptyList()
            }
        }
    }

@Serializable
data class MemoEmbeddingDto(
    @SerialName("marker_id")
    val markerId: String,
    val memo: String? = null,
    @SerialName("position_lat")
    val positionLat: Double,
    @SerialName("position_lng")
    val positionLng: Double,
    val title: String,
    @SerialName("color_hue")
    val colorHue: Float? = BitmapDescriptorFactory.HUE_RED,
    @SerialName("created_marker")
    val createdMarker: String? = null,
)

fun MemoEmbeddingDto.toNamedMarker(): NamedMarker =
    NamedMarker(
        id = markerId,
        memo = memo,
        position = LatLngSerializable(positionLat, positionLng),
        title = title,
        colorHue = colorHue ?: BitmapDescriptorFactory.HUE_RED,
        createdAt = createdMarker ?: "",
    )
