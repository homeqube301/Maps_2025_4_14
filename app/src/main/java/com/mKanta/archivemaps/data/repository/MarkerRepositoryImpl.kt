package com.mKanta.archivemaps.data.repository

import com.mKanta.archivemaps.data.local.MarkerLocalDataSource
import com.mKanta.archivemaps.domain.model.NamedMarker
import javax.inject.Inject

class MarkerRepositoryImpl
    @Inject
    constructor(
        private val markerLocalDataSource: MarkerLocalDataSource,
    ) : MarkerRepository {
        override suspend fun loadMarkers(): List<NamedMarker> = markerLocalDataSource.loadMarkers()

        override suspend fun saveMarkers(markers: List<NamedMarker>) {
            markerLocalDataSource.saveMarkers(markers)
        }
    }
