package com.example.maps_2025_4_14.ui.theme

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

data class NamedMarker(
    val id: String = UUID.randomUUID().toString(),
    val position: LatLng,
    val title: String,
    val createdAt: String = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss"))
)
