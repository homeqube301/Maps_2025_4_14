package com.example.maps_2025_4_14.model

import com.google.android.gms.maps.model.LatLng
import kotlinx.serialization.Serializable
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.UUID

@Serializable
data class NamedMarker(
    val id: String = UUID.randomUUID().toString(),
    val position: LatLngSerializable,
    val title: String,
    val createdAt: String = "",
    val imageUri: String? = null // ← 画像URIを保持
){
    fun ensureCreatedAt(): NamedMarker {
        return if (createdAt.isBlank()) {
            this.copy(
                createdAt = LocalDateTime.now().format(
                    DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss")
                )
            )
        } else this
    }
}


@Serializable
data class LatLngSerializable(val latitude: Double, val longitude: Double) {
    fun toLatLng() = LatLng(latitude, longitude)
    companion object {
        fun from(latLng: LatLng) = LatLngSerializable(latLng.latitude, latLng.longitude)
    }
}
