package com.mKanta.archivemaps.ui.screen.map

import android.content.Context
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
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.maps.android.compose.CameraPositionState
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import com.mKanta.archivemaps.R
import com.mKanta.archivemaps.domain.model.NamedMarker
import com.mKanta.archivemaps.ui.state.ListState
import com.mKanta.archivemaps.ui.state.MapsUiState
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.StateFlow
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapScreen(
    latitude: Double = 0.0,
    longitude: Double = 0.0,
    navController: NavHostController,
    // mapViewModel: MapViewModel,
    uiState: MapsUiState,
    listState: ListState,
    changeIsFollowing: () -> Unit,
    changeIsEditPanelOpen: () -> Unit,
    changeIsPanelOpen: () -> Unit,
    changeIsSearchOpen: () -> Unit,
    changeTitleQuery: (String) -> Unit,
    changeMemoQuery: (String) -> Unit,
    changeSelectedMarker: (NamedMarker?) -> Unit,
    changeTempMarkerName: (String?) -> Unit,
    changeTempMarkerPosition: (LatLng?) -> Unit,
    updateVisibleMarkers: (CameraPositionState, List<NamedMarker>) -> Unit,
    removeVisibleMarkers: (NamedMarker) -> Unit,
    addAllVisibleMarkers: (List<NamedMarker>) -> Unit,
    changeUserLocation: (LatLng) -> Unit,
    changePanelOpen: (Boolean) -> Unit,
    loadMarkers: () -> Unit,
    saveMarkers: () -> Unit,
    updateMarker: (NamedMarker) -> Unit,
    toggleFollowing: () -> Unit,
    startLocationUpdates: (
        context: Context,
        cameraPositionState: CameraPositionState,
        onLocationUpdate: (LatLng) -> Unit,
    ) -> Unit,
    fetchAddressForLatLng: (lat: Double, lon: Double) -> Unit,
    updateSearchList: (
        titleQuery: String?,
        memoQuery: String?,
        visibleMarkers: List<NamedMarker>,
    ) -> Unit,
    selectedAddress: StateFlow<String>,
    permanentMarkers: List<NamedMarker>,
    setVisibleMarkers: (List<NamedMarker>) -> Unit,
    addMarker: (NamedMarker) -> Unit,
    removeMarker: (String) -> Unit,
    updateMarkerMemoEmbedding: (NamedMarker, String) -> Unit,
) {
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
        startLocationUpdates(
            context,
            cameraPositionState,
        ) { changeUserLocation(it) }
    }

    LaunchedEffect(Unit) {
        loadMarkers()
    }

    LaunchedEffect(Unit) {
        delay(300) // projection が null でないように少し待つ
        cameraPositionState.projection?.visibleRegion?.latLngBounds?.let { bounds ->
            val filtered =
                permanentMarkers.filter { it.position.toLatLng() in bounds }
            addAllVisibleMarkers(filtered)
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
                            permanentMarkers.filter { marker ->
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
                                            listState.markerName,
                                            ignoreCase = true,
                                        )
                                val matchesMemo =
                                    listState.memo.isNullOrEmpty() ||
                                        marker.memo?.contains(
                                            listState.memo,
                                            ignoreCase = true,
                                        ) == true

                                matchesDate && matchesName && matchesMemo
                            }

                        setVisibleMarkers(filtered)
                    }
                }
            }
    }
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("") },
                colors =
                    TopAppBarDefaults.topAppBarColors(
                        containerColor = Color.Blue.copy(alpha = 0.5f),
                    ),
                actions = {
                    // 簡易検索ボタン
                    IconButton(onClick = {
                        changeIsSearchOpen()
                    }) {
                        Icon(Icons.Default.Search, contentDescription = "検索")
                    }
                },
            )
        },
    ) { innerPadding ->
        Box(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .systemBarsPadding(),
        ) {
            LaunchedEffect(uiState.titleQuery, uiState.memoQuery) {
                updateSearchList(
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
                        mapStyleOptions =
                            MapStyleOptions.loadRawResourceStyle(
                                context,
                                R.raw.map_style,
                            ),
                    ),
                onMapClick = { latLng ->
                    changeTempMarkerPosition(latLng)
                    changeIsPanelOpen()
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
                            changeSelectedMarker(marker)
                            fetchAddressForLatLng(
                                marker.position.latitude,
                                marker.position.longitude,
                            )
                            // isEditPanelOpen = true
                            changeIsEditPanelOpen()
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
                            uiState.isPanelOpen -> changeIsPanelOpen()
                            uiState.isSearchOpen -> changeIsSearchOpen()
                            else -> changeIsEditPanelOpen()
                        }

                        changeSelectedMarker(null)
                    },
                )
            }

            FloatingActionButton(
                onClick = { navController.navigate("marker_list") },
                modifier =
                    Modifier
                        .align(Alignment.TopStart)
                        .padding(start = 16.dp, top = 5.dp),
            ) {
                Icon(Icons.Default.Menu, contentDescription = "マーカ一覧")
            }

            FloatingActionButton(
                onClick = {
                    toggleFollowing()
                    changeIsFollowing()
                },
                modifier =
                    Modifier
                        .align(Alignment.TopEnd)
                        .padding(end = 16.dp, top = 5.dp),
            ) {
                Icon(
                    imageVector = if (uiState.isFollowing) Icons.Default.LocationOn else Icons.Default.LocationOn,
                    contentDescription = "追従",
                )
            }

            // 検索パネル
            if (uiState.isSearchOpen) {
                SearchMaker(
                    titleResults = uiState.titleResults,
                    memoResults = uiState.memoResults,
                    titleQuery = uiState.titleQuery,
                    memoQuery = uiState.memoQuery,
                    onTitleQueryChanged = { changeTitleQuery(it) },
                    onMemoQueryChanged = { changeMemoQuery(it) },
                    onMarkerTapped = { marker ->
                        changeSelectedMarker(marker)
                        changeIsEditPanelOpen()
                        cameraPositionState.move(
                            CameraUpdateFactory.newLatLngZoom(
                                marker.position.toLatLng(),
                                17f,
                            ),
                        )
                        changeIsSearchOpen()
                        changeTitleQuery("")
                        changeMemoQuery("")
                    },
                    onMemoTapped = { marker ->
                        changeSelectedMarker(marker)
                        changeIsEditPanelOpen()
                        cameraPositionState.move(
                            CameraUpdateFactory.newLatLngZoom(
                                marker.position.toLatLng(),
                                17f,
                            ),
                        )
                        changeIsSearchOpen()
                        changeTitleQuery("")
                        changeMemoQuery("")
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
                        changePanelOpen(false)
                    },
                    resetTempMarkers = {
                        changeTempMarkerPosition(null)
                        changeTempMarkerName(null)
                        changeIsPanelOpen()
                    },
                    changeTempMarkerName = { name ->
                        changeTempMarkerName(name)
                    },
                    addVisibleMarker = { marker ->
                        addAllVisibleMarkers(listOf(marker))
                    },
                    addMarker = { marker ->
                        addMarker(marker)
                    },
                )
            }

            // 右側から出るパネル(編集用)
            AnimatedVisibility(
                visible = uiState.isEditPanelOpen && uiState.selectedMarker != null,
                modifier = Modifier.align(Alignment.CenterEnd),
            ) {
                EditPanel(
                    selectedMarker = uiState.selectedMarker,
                    selectedAddress = selectedAddress,
                    permanentMarkers = permanentMarkers,
                    mapsSaveMarker = {
                        saveMarkers()
                    },
                    focusManager = focusManager,
                    context = context,
                    onMarkerUpdate = { updatedMarker ->
                        updateMarker(updatedMarker)
                        changeSelectedMarker(updatedMarker)
                        updateVisibleMarkers(
                            cameraPositionState,
                            permanentMarkers,
                        )
                    },
                    onMarkerDelete = { marker ->
                        removeMarker(marker.id)
                        removeVisibleMarkers(marker)
                        changeSelectedMarker(null)
                        changeIsEditPanelOpen()
                    },
                    onPanelClose = {
                        changeIsEditPanelOpen()
                        changeSelectedMarker(null)
                    },
                    memoEmbedding = updateMarkerMemoEmbedding,
                )
            }
        }
    }
}
