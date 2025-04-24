package com.example.maps20250414.domain.repoitory

import com.example.maps20250414.domain.model.NamedMarker

interface MarkerRepository {
    suspend fun loadMarkers(): List<NamedMarker>
    suspend fun saveMarkers(markers: List<NamedMarker>)
}