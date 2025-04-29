package com.mKanta.archivemaps.ui.screen.map

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import com.mKanta.archivemaps.R
import com.mKanta.archivemaps.ui.stateholder.ListViewModel
import com.mKanta.archivemaps.ui.stateholder.LocationViewModel
import com.mKanta.archivemaps.ui.stateholder.MapViewModel
import com.mKanta.archivemaps.ui.stateholder.MarkerViewModel
import com.mKanta.archivemaps.ui.stateholder.PermanentMarkerViewModel
import kotlinx.coroutines.delay
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapScreen(
    latitude: Double = 0.0,
    longitude: Double = 0.0,
    navController: NavHostController,
    permanentViewModel: PermanentMarkerViewModel,
    locationViewModel: LocationViewModel,
    mapViewModel: MapViewModel,
    listviewModel: ListViewModel,
    markerViewModel: MarkerViewModel = hiltViewModel(),
) {
    val uiState by mapViewModel.uiState.collectAsState()
    val listState by listviewModel.listState.collectAsState()
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current
    val cameraPositionState = rememberCameraPositionState()

    LaunchedEffect(latitude, longitude) {
        if (latitude != 0.0 && longitude != 0.0) {
            cameraPositionState.animate(
                update = CameraUpdateFactory.newLatLngZoom(LatLng(latitude, longitude), 16f),
                durationMs = 1000,
            )
        }
    }

    LaunchedEffect(Unit) {
        locationViewModel.startLocationUpdates(
            context = context,
            cameraPositionState = cameraPositionState,
            onLocationUpdate = { mapViewModel.changeUserLocation(it) },
        )
    }

    LaunchedEffect(Unit) {
        permanentViewModel.loadMarkers()
    }

    LaunchedEffect(Unit) {
        delay(300) // projection が null でないように少し待つ
        cameraPositionState.projection?.visibleRegion?.latLngBounds?.let { bounds ->
            val filtered =
                permanentViewModel.permanentMarkers.filter { it.position.toLatLng() in bounds }
            mapViewModel.addAllVisibleMarkers(filtered)
        }
        snapshotFlow { cameraPositionState.isMoving }
            .collect { isMoving ->
                if (!isMoving) {
                    val bounds = cameraPositionState.projection?.visibleRegion?.latLngBounds
                    bounds?.let {
                        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
                        val originalFormatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss")

                        val startDateTime =
                            if (!listState.startDate.isNullOrEmpty()) {
                                try {
                                    LocalDate.parse(listState.startDate, formatter)
                                } catch (e: Exception) {
                                    null
                                }
                            } else {
                                null
                            }

                        val endDateTime =
                            if (!listState.endDate.isNullOrEmpty()) {
                                try {
                                    LocalDate.parse(listState.endDate, formatter)
                                } catch (e: Exception) {
                                    null
                                }
                            } else {
                                null
                            }

                        val filtered =
                            permanentViewModel.permanentMarkers.filter { marker ->
                                val markerDate: LocalDate? =
                                    try {
                                        LocalDateTime
                                            .parse(marker.createdAt, originalFormatter)
                                            .toLocalDate()
                                    } catch (e: Exception) {
                                        null
                                    }

                                // val matchesBounds = marker.position.toLatLng() in bounds
                                val matchesDate =
                                    markerDate?.let {
                                        (startDateTime == null || !it.isBefore(startDateTime)) &&
                                                (endDateTime == null || !it.isAfter(endDateTime))
                                    } ?: false
                                val matchesName =
                                    listState.markerName.isNullOrEmpty() ||
                                            marker.title.contains(
                                                listState.markerName!!,
                                                ignoreCase = true,
                                            )
                                val matchesMemo =
                                    listState.memo.isNullOrEmpty() ||
                                            marker.memo?.contains(
                                                listState.memo!!,
                                                ignoreCase = true,
                                            ) == true

                                // matchesBounds &&
                                matchesDate && matchesName && matchesMemo
                            }

                        mapViewModel.addAllVisibleMarkers(filtered)
                    }
                }
            }
    }
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Blue.copy(alpha = 0.5f)
                ),
                actions = {
                    // 簡易検索ボタン
                    IconButton(onClick = {
                        mapViewModel.changeIsSearchOpen()
                    }) {
                        Icon(Icons.Default.Search, contentDescription = "検索")
                    }
                }
            )
        },
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .systemBarsPadding()
        ) {
            LaunchedEffect(uiState.titleQuery, uiState.memoQuery) {
                mapViewModel.updateSearchList(
                    uiState.titleQuery,
                    uiState.memoQuery,
                    uiState.visibleMarkers,
                )
            }

            GoogleMap(
                modifier = Modifier.fillMaxSize(),
                cameraPositionState = cameraPositionState,
                properties =
                    MapProperties(
                        isMyLocationEnabled = uiState.isPermissionGranted,
                        mapStyleOptions = MapStyleOptions.loadRawResourceStyle(
                            context,
                            R.raw.map_style
                        ),
                    ),
                onMapClick = { latLng ->
                    mapViewModel.changeTempMarkerPosition(latLng)
                    mapViewModel.changeIsPanelOpen()
                },
            ) {
                // カメラの表示範囲にある永続マーカーのみ表示

                for (marker in uiState.visibleMarkers) {
                    Marker(
                        state = MarkerState(position = marker.position.toLatLng()),
                        title = marker.title,
                        icon = BitmapDescriptorFactory.defaultMarker(marker.colorHue),
                        onClick = {
                            // selectedMarker = marker
                            mapViewModel.changeSelectedMarker(marker)
                            markerViewModel.fetchAddressForLatLng(
                                marker.position.latitude,
                                marker.position.longitude,
                            )
                            // isEditPanelOpen = true
                            mapViewModel.changeIsEditPanelOpen()
                            true // consume click
                        },
                    )
                }
                // 一時マーカー
                uiState.tempMarkerPosition?.let { temp ->
                    Marker(
                        state = MarkerState(position = temp),
                        title = "一時マーカー",
                        icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE),
                        draggable = false,
                    )
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
                    },
                )
            }


            FloatingActionButton(
                onClick = { navController.navigate("marker_list") },
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(start = 16.dp, top = 5.dp)
            ) {
                Icon(Icons.Default.Menu, contentDescription = "マーカ一覧")
            }


            FloatingActionButton(
                onClick = {
                    locationViewModel.toggleFollowing()
                    mapViewModel.changeIsFollowing()
                },
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(end = 16.dp, top = 5.dp)
            ) {
                Icon(
                    imageVector = if (uiState.isFollowing) Icons.Default.LocationOn else Icons.Default.LocationOn,
                    contentDescription = "追従"
                )
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
                                marker.position.toLatLng(),
                                17f,
                            ),
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
                                marker.position.toLatLng(),
                                17f,
                            ),
                        )
                        mapViewModel.changeIsSearchOpen()
                        mapViewModel.changeTitleQuery("")
                        mapViewModel.changeMemoQuery("")
                    },
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // 右側から出るパネル
            AnimatedVisibility(
                visible = uiState.isPanelOpen,
                modifier = Modifier.align(Alignment.CenterEnd),
            ) {
                SetMarkerPanel(
                    // tempMarkerName = null,
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
                        permanentViewModel.addMarker(marker)
                    },
                )
            }

            // 右側から出るパネル(編集用)
            AnimatedVisibility(
                visible = uiState.isEditPanelOpen && uiState.selectedMarker != null,
                modifier = Modifier.align(Alignment.CenterEnd),
            ) {
                EditPanel(
                    // uiState = uiState,
                    selectedMarker = uiState.selectedMarker,
                    selectedAddress = markerViewModel.selectedAddress,
                    permanentMarkers = permanentViewModel.permanentMarkers,
                    mapsSaveMarker = {
                        permanentViewModel.saveMarkers()
                    },
                    focusManager = focusManager,
                    context = context,
                    onMarkerUpdate = { updatedMarker ->
                        permanentViewModel.updateMarker(updatedMarker)
                        mapViewModel.changeSelectedMarker(updatedMarker)
                        mapViewModel.updateVisibleMarkers(
                            cameraPositionState,
                            permanentViewModel.permanentMarkers,
                        )
                    },
                    onMarkerDelete = { marker ->
                        permanentViewModel.removeMarker(marker.id)
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
}

