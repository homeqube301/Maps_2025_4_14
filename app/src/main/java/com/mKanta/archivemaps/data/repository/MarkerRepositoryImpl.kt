package com.mKanta.archivemaps.data.repository

import android.content.Context
import com.mKanta.archivemaps.data.local.MarkerLocalDataSource
import com.mKanta.archivemaps.domain.model.NamedMarker
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class MarkerRepositoryImpl
@Inject
constructor(
    @ApplicationContext private val context: Context,
    private val markerLocalDataSource: MarkerLocalDataSource,
) : MarkerRepository {
    override suspend fun loadMarkers(): List<NamedMarker> =
        markerLocalDataSource.loadMarkers(context)

    override suspend fun saveMarkers(markers: List<NamedMarker>) {
        markerLocalDataSource.saveMarkers(context, markers)
    }
}
