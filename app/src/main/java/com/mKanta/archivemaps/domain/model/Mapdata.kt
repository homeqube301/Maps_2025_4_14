package com.mKanta.archivemaps.domain.model

import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
data class NamedMarker(
    val id: String = UUID.randomUUID().toString(),
    val position: LatLngSerializable,
    val title: String,
    val createdAt: String = "",
    val imageUri: String? = null, // ← 画像URIを保持
    val videoUri: String? = null,
    val memo: String? = null,
    val colorHue: Float = BitmapDescriptorFactory.HUE_RED,
) {
//    fun ensureCreatedAt(): NamedMarker {
//        return if (createdAt.isBlank()) {
//            this.copy(
//                createdAt = LocalDateTime.now().format(
//                    DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss")
//                )
//            )
//        } else this
//    }
}

@Serializable
data class LatLngSerializable(val latitude: Double, val longitude: Double) {
    fun toLatLng() = LatLng(latitude, longitude)

    companion object {
        fun from(latLng: LatLng) = LatLngSerializable(latLng.latitude, latLng.longitude)
    }
}
