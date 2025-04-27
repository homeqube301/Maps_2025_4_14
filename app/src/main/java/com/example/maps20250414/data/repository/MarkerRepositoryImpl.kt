package com.example.maps20250414.data.repository

import android.content.Context
import com.example.maps20250414.data.local.MarkerLocalDataSource
import com.example.maps20250414.domain.model.NamedMarker
import com.example.maps20250414.domain.repoitory.MarkerRepository
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



