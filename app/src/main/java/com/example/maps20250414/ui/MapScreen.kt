package com.example.maps20250414.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.maps20250414.R
import com.example.maps20250414.model.LocationViewModel
import com.example.maps20250414.model.MapViewModel
import com.example.maps20250414.model.MarkerViewModel
import com.example.maps20250414.model.NamedMarker
import com.example.maps20250414.model.PermanentMarkerViewModel
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
    viewModel: PermanentMarkerViewModel = hiltViewModel(),
    locationViewModel: LocationViewModel = hiltViewModel(),
    mapViewModel: MapViewModel = hiltViewModel(),

    ) {

    val uiState by mapViewModel.uiState.collectAsState()


    val context = LocalContext.current
    //val fusedLocationClient = remember {
    //    LocationServices.getFusedLocationProviderClient(context)
    //}

    //val isFollowing by locationViewModel.isFollowing.collectAsState()


    var userLocation by remember { mutableStateOf<LatLng?>(null) }
    val cameraPositionState = rememberCameraPositionState()
    //var isFollowing by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        locationViewModel.startLocationUpdates(
            context = context,
            isFollowing = uiState.isFollowing,
            cameraPositionState = cameraPositionState,
            onLocationUpdate = { userLocation = it })
    }

    // タップされた位置の一時マーカー
    //var tempMarkerPosition by remember { mutableStateOf<LatLng?>(null) }

    // 永続マーカーのリスト
    //val permanentMarkers = remember { mutableStateListOf<NamedMarker>() }
    val permanentMarkers = viewModel.permanentMarkers
    val markerViewModel: MarkerViewModel = hiltViewModel()

    LaunchedEffect(Unit) {
        //val loaded = loadMarkers(context)
        //permanentMarkers.addAll(loaded)

        viewModel.loadMarkers()
    }

    // サイドパネルの表示フラグ

    val focusManager = LocalFocusManager.current




    LaunchedEffect(cameraPositionState.isMoving) {
        // マップ移動終了後に更新
        snapshotFlow { cameraPositionState.isMoving }.collect { isMoving ->
            if (!isMoving) {
                // カメラが止まったときに現在の可視領域を取得
                val bounds = cameraPositionState.projection?.visibleRegion?.latLngBounds
                if (bounds != null) {
                    val filtered =
                        permanentMarkers.filter { bounds.contains(it.position.toLatLng()) }
                    //visibleMarkers.clear()
                    //visibleMarkers.addAll(filtered)
                    //uiState.copy(visibleMarkers = filtered)
                    mapViewModel.addAllVisibleMarkers(filtered)
                }
            }
        }
    }


    Box(modifier = Modifier.fillMaxSize()) {


        LaunchedEffect(uiState.titleQuery, uiState.memoQuery) {
            mapViewModel.UpdateSearchList(
                uiState.titleQuery,
                uiState.memoQuery,
                permanentMarkers,
            )
        }

        //val context2 = LocalContext.current


        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState,
            properties = MapProperties(
                isMyLocationEnabled = uiState.isPermissionGranted,
                mapStyleOptions = MapStyleOptions.loadRawResourceStyle(context, R.raw.map_style)
            ),
            onMapClick = { latLng ->
                //tempMarkerPosition = latLng
                mapViewModel.changeTempMarkerPosition(latLng)
                //uiState.isPanelOpen = true
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
                            marker.position.latitude, marker.position.longitude
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
            DismissOverlay()
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
//                titleQuery = "",
//                memoQuery = "",
//                titleResults = emptyList(),
//                memoResults = emptyList(),
//                onMarkerNameChanged = { },
//                onMemoNameChanged = {},
//                onMarkerTapped = {},
//                onMemoTapped = {},
                //cameraPositionState = cameraPositionState,
                uiState = uiState,
                onTitleQueryChanged = { mapViewModel.changeTitleQuery(it) },
                onMemoQueryChanged = { mapViewModel.changeMemoQuery(it) },
                onMarkerTapped = { marker ->
                    mapViewModel.changeSelectedMarker(marker)
                    mapViewModel.changeIsEditPanelOpen()
                    cameraPositionState.move(
                        CameraUpdateFactory.newLatLngZoom(
                            marker.position.toLatLng(),
                            17f
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
                            marker.position.toLatLng(),
                            17f
                        )
                    )
                    mapViewModel.changeIsSearchOpen()
                    mapViewModel.changeTitleQuery("")
                    mapViewModel.changeMemoQuery("")
                },
            )
        }

        // 右側から出るパネル
        AnimatedVisibility(
            visible = uiState.isPanelOpen,
            modifier = Modifier.align(Alignment.CenterEnd)
        ) {
            SetMarkerPanel(
                tempMarkerName = null,
                cameraPositionState = cameraPositionState,
                focusManager = focusManager,
                onClose = {
                    mapViewModel.changePanelOpen(false)
                }
            )
        }


        // 右側から出るパネル(編集用)
        AnimatedVisibility(
            visible = uiState.isEditPanelOpen && uiState.selectedMarker != null,
            modifier = Modifier.align(Alignment.CenterEnd)
        ) {
            EditPanel(
                uiState = uiState,
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
                onMediaPicked = { marker, uri, mimeType ->
                    val updatedMarker = when {
                        mimeType?.startsWith("image/") == true -> marker.copy(imageUri = uri.toString())
                        mimeType?.startsWith("video/") == true -> marker.copy(videoUri = uri.toString())
                        else -> marker
                    }
                    viewModel.updateMarker(updatedMarker)
                    mapViewModel.changeSelectedMarker(updatedMarker)
                    mapViewModel.updateVisibleMarkers(cameraPositionState, permanentMarkers)
                },
                onMediaDelete = { marker ->
                    val updatedMarker = marker.copy(imageUri = null, videoUri = null)
                    viewModel.updateMarker(updatedMarker)
                    mapViewModel.changeSelectedMarker(updatedMarker)
                    mapViewModel.updateVisibleMarkers(cameraPositionState, permanentMarkers)
                }
            )

//            EditPanel(
//                focusManager = focusManager,
//                permanentMarkers = permanentMarkers,
//                context = context,
//                cameraPositionState = cameraPositionState
//            )
        }


    }
}



