package com.example.maps20250414.ui.screen.map

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.example.maps20250414.R
import com.example.maps20250414.ui.stateholder.LocationViewModel
import com.example.maps20250414.ui.stateholder.MapViewModel
import com.example.maps20250414.ui.stateholder.MarkerViewModel
import com.example.maps20250414.ui.stateholder.PermanentMarkerViewModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState

//@SuppressLint("MissingPermission")
@Composable
fun MapScreen(
    latitude: Double = 0.0,
    longitude: Double = 0.0,
    navController: NavHostController,
    viewModel: PermanentMarkerViewModel = hiltViewModel(),
    locationViewModel: LocationViewModel = hiltViewModel(),
    mapViewModel: MapViewModel = hiltViewModel(),
) {

    val uiState by mapViewModel.uiState.collectAsState()
    val context = LocalContext.current
    val cameraPositionState = rememberCameraPositionState()
    val permanentMarkers = viewModel.permanentMarkers
    val markerViewModel: MarkerViewModel = hiltViewModel()
    // サイドパネルの表示フラグ
    val focusManager = LocalFocusManager.current

    LaunchedEffect(latitude, longitude) {
        if (latitude != 0.0 && longitude != 0.0) {
            cameraPositionState.animate(
                update = CameraUpdateFactory.newLatLngZoom(LatLng(latitude, longitude), 16f),
                durationMs = 1000
            )
        }
    }

    LaunchedEffect(Unit) {
        locationViewModel.startLocationUpdates(
            context = context,
            cameraPositionState = cameraPositionState,
            onLocationUpdate = { mapViewModel.changeUserLocation(it) })
    }




    LaunchedEffect(Unit) {
        viewModel.loadMarkers()
    }


    LaunchedEffect(Unit) {
        snapshotFlow { cameraPositionState.isMoving }
            .collect { isMoving ->
                if (!isMoving) {
                    val bounds = cameraPositionState.projection?.visibleRegion?.latLngBounds
                    bounds?.let {
                        val filtered = permanentMarkers.filter { it.position.toLatLng() in bounds }
                        mapViewModel.addAllVisibleMarkers(filtered)
                    }
                }
            }
    }


    Box(modifier = Modifier.fillMaxSize()) {

        LaunchedEffect(uiState.titleQuery, uiState.memoQuery) {
            mapViewModel.updateSearchList(
                uiState.titleQuery,
                uiState.memoQuery,
                permanentMarkers,
            )
        }


        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState,
            properties = MapProperties(
                isMyLocationEnabled = uiState.isPermissionGranted,
                mapStyleOptions = MapStyleOptions.loadRawResourceStyle(context, R.raw.map_style)
            ),
            onMapClick = { latLng ->
                mapViewModel.changeTempMarkerPosition(latLng)
                mapViewModel.changeIsPanelOpen()
            }) {

            // カメラの表示範囲にある永続マーカーのみ表示

            for (marker in uiState.visibleMarkers) {
                Marker(
                    state = MarkerState(position = marker.position.toLatLng()),
                    title = marker.title,
                    icon = BitmapDescriptorFactory.defaultMarker(marker.colorHue),
                    onClick = {
                        //selectedMarker = marker
                        mapViewModel.changeSelectedMarker(marker)
                        markerViewModel.fetchAddressForLatLng(
                            marker.position.latitude,
                            marker.position.longitude,
                        )
                        //isEditPanelOpen = true
                        mapViewModel.changeIsEditPanelOpen()
                        true // consume click
                    })
            }
            // 一時マーカー
            uiState.tempMarkerPosition?.let { temp ->
                Marker(
                    state = MarkerState(position = temp),
                    title = "一時マーカー",
                    icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE),
                    draggable = false

                )
            }
        }

        // 右下の追従モードボタン
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Bottom,
            horizontalAlignment = Alignment.End
        ) {
            Button(onClick = {
                locationViewModel.toggleFollowing()
                mapViewModel.changeIsFollowing()
            }) {
                Text(if (uiState.isFollowing) "追従中" else "追従してないよ")
            }
        }

        if (uiState.isEditPanelOpen || uiState.isPanelOpen || uiState.isSearchOpen) {
            DismissOverlay(
                onClosePanel = {
                    when {
                        uiState.isPanelOpen -> mapViewModel.changeIsPanelOpen()
                        uiState.isSearchOpen -> mapViewModel.changeIsSearchOpen()
                        else -> mapViewModel.changeIsEditPanelOpen()
                    }

                    mapViewModel.changeSelectedMarker(null)
                })
        }

        // 検索ボタンとパネル
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.End
        ) {
            Button(onClick = {
                mapViewModel.changeIsSearchOpen()
                mapViewModel.changeTitleQuery("")
                mapViewModel.changeMemoQuery("")

            }) {
                Text(if (uiState.isSearchOpen) "閉じる" else "検索")
            }
        }

        // 検索パネル
        if (uiState.isSearchOpen) {
            SearchMaker(
                titleResults = uiState.titleResults,
                memoResults = uiState.memoResults,
                titleQuery = uiState.titleQuery,
                memoQuery = uiState.memoQuery,
                onTitleQueryChanged = { mapViewModel.changeTitleQuery(it) },
                onMemoQueryChanged = { mapViewModel.changeMemoQuery(it) },
                onMarkerTapped = { marker ->
                    mapViewModel.changeSelectedMarker(marker)
                    mapViewModel.changeIsEditPanelOpen()
                    cameraPositionState.move(
                        CameraUpdateFactory.newLatLngZoom(
                            marker.position.toLatLng(), 17f
                        )
                    )
                    mapViewModel.changeIsSearchOpen()
                    mapViewModel.changeTitleQuery("")
                    mapViewModel.changeMemoQuery("")
                },
                onMemoTapped = { marker ->
                    mapViewModel.changeSelectedMarker(marker)
                    mapViewModel.changeIsEditPanelOpen()
                    cameraPositionState.move(
                        CameraUpdateFactory.newLatLngZoom(
                            marker.position.toLatLng(), 17f
                        )
                    )
                    mapViewModel.changeIsSearchOpen()
                    mapViewModel.changeTitleQuery("")
                    mapViewModel.changeMemoQuery("")
                },
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // マーカー一覧ボタン
        Button(onClick = {
            navController.navigate("marker_list")
        }) {
            Text("マーカー一覧")
        }

        // 右側から出るパネル
        AnimatedVisibility(
            visible = uiState.isPanelOpen, modifier = Modifier.align(Alignment.CenterEnd)
        ) {
            SetMarkerPanel(
                //tempMarkerName = null,
                cameraPositionState = cameraPositionState,
                focusManager = focusManager,
                tempMarkerPosition = uiState.tempMarkerPosition,
                tempMarkerName = uiState.tempMarkerName,
                onClose = {
                    mapViewModel.changePanelOpen(false)
                },
                resetTempMarkers = {
                    mapViewModel.changeTempMarkerPosition(null)
                    mapViewModel.changeTempMarkerName(null)
                    mapViewModel.changeIsPanelOpen()
                },
                changeTempMarkerName = { name ->
                    mapViewModel.changeTempMarkerName(name)
                },
                addVisibleMarker = { marker ->
                    mapViewModel.addAllVisibleMarkers(listOf(marker))
                },
                addMarker = { marker ->
                    viewModel.addMarker(marker)
                }

            )

        }

        // 右側から出るパネル(編集用)
        AnimatedVisibility(
            visible = uiState.isEditPanelOpen && uiState.selectedMarker != null,
            modifier = Modifier.align(Alignment.CenterEnd)
        ) {
            EditPanel(
                //uiState = uiState,
                selectedMarker = uiState.selectedMarker,
                selectedAddress = markerViewModel.selectedAddress,
                permanentMarkers = permanentMarkers,
                focusManager = focusManager,
                context = context,
                onMarkerUpdate = { updatedMarker ->
                    viewModel.updateMarker(updatedMarker)
                    mapViewModel.changeSelectedMarker(updatedMarker)
                    mapViewModel.updateVisibleMarkers(cameraPositionState, permanentMarkers)
                },
                onMarkerDelete = { marker ->
                    viewModel.removeMarker(marker.id)
                    mapViewModel.removeVisibleMarkers(marker)
                    mapViewModel.changeSelectedMarker(null)
                    mapViewModel.changeIsEditPanelOpen()
                },
                onPanelClose = {
                    mapViewModel.changeIsEditPanelOpen()
                    mapViewModel.changeSelectedMarker(null)
                },
            )

        }

    }
}



