package com.mKanta.archivemaps.data.local

import com.google.android.gms.maps.model.BitmapDescriptorFactory
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

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
