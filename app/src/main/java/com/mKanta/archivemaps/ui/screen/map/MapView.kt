package com.mKanta.archivemaps.ui.screen.map

import android.content.Context
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.maps.android.compose.CameraPositionState
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.mKanta.archivemaps.R
import com.mKanta.archivemaps.domain.model.NamedMarker
import kotlinx.coroutines.launch

@Composable
fun MapView(
    cameraPositionState: CameraPositionState,
    isPermissionGranted: Boolean,
    visibleMarkers: List<NamedMarker>,
    tempMarkerPosition: LatLng?,
    changeTempMarkerPosition: (LatLng) -> Unit,
    changeIsPanelOpen: () -> Unit,
    checkGoogleMapState: (Boolean) -> Unit,
    fetchAddressForLatLng: (lat: Double, lon: Double) -> Unit,
    context: Context,
    changeIsEditPanelOpen: () -> Unit,
    changeSelectedMarker: (NamedMarker?) -> Unit,
) {
    val coroutineScope = rememberCoroutineScope()
    GoogleMap(
        modifier =
            Modifier.Companion
                .fillMaxSize(),
        cameraPositionState = cameraPositionState,
        properties =
            MapProperties(
                isMyLocationEnabled = isPermissionGranted,
                mapStyleOptions =
                    MapStyleOptions.loadRawResourceStyle(
                        context,
                        R.raw.map_style,
                    ),
            ),
        uiSettings =
            MapUiSettings(
                myLocationButtonEnabled = false,
                zoomControlsEnabled = false,
            ),
        onMapClick = { latLng ->
            changeTempMarkerPosition(latLng)
            changeIsPanelOpen()
        },
        onMapLoaded = {
            coroutineScope.launch {
                checkGoogleMapState(true)
            }
        },
    ) {
        for (marker in visibleMarkers) {
            Marker(
                state = MarkerState(position = marker.position.toLatLng()),
                title = marker.title,
                icon = BitmapDescriptorFactory.defaultMarker(marker.colorHue),
                onClick = {
                    changeSelectedMarker(marker)
                    fetchAddressForLatLng(
                        marker.position.latitude,
                        marker.position.longitude,
                    )
                    changeIsEditPanelOpen()
                    true
                },
            )
        }
        tempMarkerPosition?.let { temp ->
            Marker(
                state = MarkerState(position = temp),
                title = stringResource(R.string.map_temp_marker),
                icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE),
                draggable = false,
            )
        }
    }
}
