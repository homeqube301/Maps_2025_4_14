package com.mKanta.archivemaps.utils

import android.content.Context
import androidx.compose.runtime.snapshotFlow
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.maps.android.compose.CameraPositionState
import com.mKanta.archivemaps.domain.model.NamedMarker
import kotlinx.coroutines.delay

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
    ) -> Unit,
    filterMarkers: (
        markers: List<NamedMarker>,
        bounds: LatLngBounds?,
        startDate: String?,
        endDate: String?,
        markerName: String?,
        memo: String?,
        similarMarkerIds: List<String>,
    ) -> List<NamedMarker>,
) {
    startLocationUpdates(
        context,
        cameraPositionState,
    )

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
        filterMarkers = filterMarkers,
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
    filterMarkers: (
        markers: List<NamedMarker>,
        bounds: LatLngBounds?,
        startDate: String?,
        endDate: String?,
        markerName: String?,
        memo: String?,
        similarMarkerIds: List<String>,
    ) -> List<NamedMarker>,
) {
    delay(300)

    snapshotFlow { cameraPositionState.isMoving to permanentMarkers }.collect { (isMoving, markers) ->
        if (!isMoving) {
            val bounds =
                cameraPositionState.projection?.visibleRegion?.latLngBounds ?: return@collect
            val filtered =
                filterMarkers(
                    markers,
                    bounds,
                    startDate,
                    endDate,
                    markerName,
                    memo,
                    emptyList(),
                )
            setVisibleMarkers(filtered)
        }
    }
}
