package com.mKanta.archivemaps.domain.repoitory

import com.mKanta.archivemaps.domain.model.NamedMarker

interface MarkerRepository {
    suspend fun loadMarkers(): List<NamedMarker>
    suspend fun saveMarkers(markers: List<NamedMarker>)
}
