package com.mKanta.archivemaps.utils

import android.content.Context
import androidx.compose.runtime.snapshotFlow
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.CameraPositionState
import com.mKanta.archivemaps.domain.model.NamedMarker
import kotlinx.coroutines.delay
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

suspend fun initializeMapLogic(
    context: Context,
    cameraPositionState: CameraPositionState,
    startDate: String = "",
    endDate: String = "",
    markerName: String = "",
    memo: String = "",
    permanentMarkers: List<NamedMarker>,
    addAllVisibleMarkers: (List<NamedMarker>) -> Unit,
    setVisibleMarkers: (List<NamedMarker>) -> Unit,
    changeUserLocation: (LatLng) -> Unit,
    loadMarkers: () -> Unit,
    startLocationUpdates: (
        context: Context,
        cameraPositionState: CameraPositionState,
        onLocationUpdate: (LatLng) -> Unit,
    ) -> Unit,
) {
    startLocationUpdates(
        context,
        cameraPositionState,
    ) { changeUserLocation(it) }

    loadMarkers()

    observeCameraAndFilterMarkers(
        cameraPositionState = cameraPositionState,
        permanentMarkers = permanentMarkers,
        addAllVisibleMarkers = addAllVisibleMarkers,
        setVisibleMarkers = setVisibleMarkers,
        startDate = startDate,
        endDate = endDate,
        markerName = markerName,
        memo = memo,
    )
}

suspend fun observeCameraAndFilterMarkers(
    cameraPositionState: CameraPositionState,
    permanentMarkers: List<NamedMarker>,
    startDate: String? = null,
    endDate: String? = null,
    markerName: String? = null,
    memo: String? = null,
    addAllVisibleMarkers: (List<NamedMarker>) -> Unit,
    setVisibleMarkers: (List<NamedMarker>) -> Unit,
) {
    delay(300)
    cameraPositionState.projection?.visibleRegion?.latLngBounds?.let { bounds ->
        val filtered = permanentMarkers.filter { it.position.toLatLng() in bounds }
        addAllVisibleMarkers(filtered)
    }

    snapshotFlow { cameraPositionState.isMoving }
        .collect { isMoving ->
            if (!isMoving) {
                val bounds =
                    cameraPositionState.projection?.visibleRegion?.latLngBounds ?: return@collect
                val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
                val originalFormatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss")

                val startDateTime =
                    startDate?.let {
                        try {
                            LocalDate.parse(it, formatter)
                        } catch (_: Exception) {
                            null
                        }
                    }
                val endDateTime =
                    endDate?.let {
                        try {
                            LocalDate.parse(it, formatter)
                        } catch (_: Exception) {
                            null
                        }
                    }

                val filtered =
                    permanentMarkers.filter { marker ->
                        val markerDate =
                            try {
                                LocalDateTime
                                    .parse(marker.createdAt, originalFormatter)
                                    .toLocalDate()
                            } catch (_: Exception) {
                                null
                            }

                        val matchesDate =
                            markerDate?.let {
                                (startDateTime == null || !it.isBefore(startDateTime)) &&
                                    (endDateTime == null || !it.isAfter(endDateTime))
                            } == true

                        val matchesName =
                            markerName.isNullOrEmpty() ||
                                marker.title.contains(markerName, ignoreCase = true)

                        val matchesMemo =
                            memo.isNullOrEmpty() ||
                                marker.memo?.contains(memo, ignoreCase = true) == true

                        marker.position.toLatLng() in bounds && matchesDate && matchesName && matchesMemo
                    }

                setVisibleMarkers(filtered)
            }
        }
}
