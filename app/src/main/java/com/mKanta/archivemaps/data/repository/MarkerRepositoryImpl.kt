package com.mKanta.archivemaps.data.repository

import android.content.Context
import com.mKanta.archivemaps.data.local.MarkerLocalDataSource
import com.mKanta.archivemaps.domain.model.NamedMarker
import com.mKanta.archivemaps.domain.repoitory.MarkerRepository
import javax.inject.Inject

class MarkerRepositoryImpl @Inject constructor(
    private val context: Context,
) : MarkerRepository {

    override suspend fun loadMarkers(): List<NamedMarker> {

        return MarkerLocalDataSource.loadMarkers(context)
    }

    override suspend fun saveMarkers(markers: List<NamedMarker>) {
        MarkerLocalDataSource.saveMarkers(context, markers)
    }

}



