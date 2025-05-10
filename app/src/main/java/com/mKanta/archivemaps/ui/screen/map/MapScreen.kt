package com.mKanta.archivemaps.ui.screen.map

import android.content.Context
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.canopas.lib.showcase.IntroShowcase
import com.canopas.lib.showcase.component.ShowcaseStyle
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.maps.android.compose.CameraPositionState
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import com.mKanta.archivemaps.R
import com.mKanta.archivemaps.domain.model.NamedMarker
import com.mKanta.archivemaps.ui.state.ListState
import com.mKanta.archivemaps.ui.state.MapState
import com.mKanta.archivemaps.ui.state.MapsUiState
import com.mKanta.archivemaps.ui.theme.ArchivemapsTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapScreen(
    latitude: Double,
    longitude: Double,
    navController: NavHostController,
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
    changeShowMapIntro: () -> Unit,
    changeShowConfirmDialog: () -> Unit,
    checkGoogleMapState: (Boolean) -> Unit,
    googleMapState: MapState,
) {
    ArchivemapsTheme {
        val context = LocalContext.current
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
            initializeMapLogic(
                context,
                cameraPositionState,
                permanentMarkers,
                listState,
                addAllVisibleMarkers,
                setVisibleMarkers,
                changeUserLocation,
                loadMarkers,
                startLocationUpdates,
            )
        }

        Scaffold { innerPadding ->
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
                        permanentMarkers,
                    )
                }

                MapView(
                    cameraPositionState = cameraPositionState,
                    isPermissionGranted = uiState.isPermissionGranted,
                    visibleMarkers = uiState.visibleMarkers,
                    tempMarkerPosition = uiState.tempMarkerPosition,
                    changeTempMarkerPosition = { changeTempMarkerPosition(it) },
                    changeIsPanelOpen = { changeIsPanelOpen() },
                    checkGoogleMapState = { checkGoogleMapState(it) },
                    fetchAddressForLatLng = { lat, lon -> fetchAddressForLatLng(lat, lon) },
                    context = context,
                    changeIsEditPanelOpen = { changeIsEditPanelOpen() },
                    changeSelectedMarker = { changeSelectedMarker(it) },
                )

                PanelDismissOverlay(
                    isEditPanelOpen = uiState.isEditPanelOpen,
                    isPanelOpen = uiState.isPanelOpen,
                    isSearchOpen = uiState.isSearchOpen,
                    changeShowConfirmDialog = { changeShowConfirmDialog() },
                    showConfirmDialog = uiState.showConfirmDialog,
                    changeIsEditPanelOpen = { changeIsEditPanelOpen() },
                    changeIsPanelOpen = { changeIsPanelOpen() },
                    changeIsSearchOpen = { changeIsSearchOpen() },
                    changeSelectedMarker = { changeSelectedMarker(it) },
                )

                when (googleMapState) {
                    MapState.Success(true) -> {
                        MapFloatingButtons(
                            modifier =
                                Modifier
                                    .align(Alignment.BottomCenter)
                                    .padding(top = 50.dp, end = 5.dp, bottom = 60.dp),
                            showIntroShowCase = uiState.showMapIntro,
                            changeShowMapIntro = { changeShowMapIntro() },
                            changeIsSearchOpen = { changeIsSearchOpen() },
                            changeIsFollowing = { changeIsFollowing() },
                            toggleFollowing = { toggleFollowing() },
                            navController = navController,
                            isFollowing = uiState.isFollowing,
                        )
                    }

                    else -> {}
                }
                MapPanel(
                    modifier =
                        Modifier
                            .align(Alignment.BottomCenter)
                            .fillMaxWidth(),
                    isSearchOpen = uiState.isSearchOpen,
                    changeIsSearchOpen = { changeIsSearchOpen() },
                    titleResults = uiState.titleResults,
                    memoResults = uiState.memoResults,
                    titleQuery = uiState.titleQuery,
                    memoQuery = uiState.memoQuery,
                    changeTitleQuery = { changeTitleQuery(it) },
                    changeMemoQuery = { changeMemoQuery(it) },
                    changeSelectedMarker = { changeSelectedMarker(it) },
                    changeIsEditPanelOpen = { changeIsEditPanelOpen() },
                    cameraPositionState = cameraPositionState,
                    tempMarkerPosition = uiState.tempMarkerPosition,
                    tempMarkerName = uiState.tempMarkerName,
                    changeTempMarkerPosition = { changeTempMarkerPosition(it) },
                    changeTempMarkerName = { changeTempMarkerName(it) },
                    changeIsPanelOpen = { changeIsPanelOpen() },
                    changePanelOpen = { changePanelOpen(it) },
                    permanentMarkers = permanentMarkers,
                    addAllVisibleMarkers = { addAllVisibleMarkers(it) },
                    addMarker = { addMarker(it) },
                    removeMarker = { removeMarker(it) },
                    updateMarker = { updateMarker(it) },
                    updateMarkerMemoEmbedding = { marker, newMemo ->
                        updateMarkerMemoEmbedding(marker, newMemo)
                    },
                    changeShowConfirmDialog = { changeShowConfirmDialog() },
                    showConfirmDialog = uiState.showConfirmDialog,
                    context = context,
                    selectedAddress = selectedAddress,
                    isPanelOpen = uiState.isPanelOpen,
                    isEditPanelOpen = uiState.isEditPanelOpen,
                    removeVisibleMarkers = { removeVisibleMarkers(it) },
                    selectedMarker = uiState.selectedMarker,
                    updateVisibleMarkers = { camera, markers ->
                        updateVisibleMarkers(camera, markers)
                    },
                    saveMarkers = { saveMarkers() },
                )
            }

            when (googleMapState) {
                MapState.Success(true) -> {}
                MapState.Loading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                    ) {
                        Column(
                            modifier = Modifier.align(Alignment.Center),
                            horizontalAlignment = Alignment.CenterHorizontally,
                        ) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(48.dp),
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(text = "地図を読み込み...", fontSize = 16.sp)
                        }
                    }
                }

                else -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            text = "エラーが発生しました",
                            color = Color.Red,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                        )
                    }
                }
            }
        }
    }
}

private suspend fun initializeMapLogic(
    context: Context,
    cameraPositionState: CameraPositionState,
    permanentMarkers: List<NamedMarker>,
    listState: ListState,
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
        listState = listState,
        addAllVisibleMarkers = addAllVisibleMarkers,
        setVisibleMarkers = setVisibleMarkers,
    )
}

private suspend fun observeCameraAndFilterMarkers(
    cameraPositionState: CameraPositionState,
    permanentMarkers: List<NamedMarker>,
    listState: ListState,
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
                    listState.startDate?.let {
                        try {
                            LocalDate.parse(it, formatter)
                        } catch (_: Exception) {
                            null
                        }
                    }
                val endDateTime =
                    listState.endDate?.let {
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
                            listState.markerName.isNullOrEmpty() ||
                                marker.title.contains(listState.markerName, ignoreCase = true)

                        val matchesMemo =
                            listState.memo.isNullOrEmpty() ||
                                marker.memo?.contains(listState.memo, ignoreCase = true) == true

                        marker.position.toLatLng() in bounds && matchesDate && matchesName && matchesMemo
                    }

                setVisibleMarkers(filtered)
            }
        }
}

@Composable
private fun MapView(
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
    GoogleMap(
        modifier =
            Modifier
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
            checkGoogleMapState(true)
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
                title = "一時マーカー",
                icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE),
                draggable = false,
            )
        }
    }
}

@Composable
private fun PanelDismissOverlay(
    isEditPanelOpen: Boolean,
    isPanelOpen: Boolean,
    isSearchOpen: Boolean,
    changeShowConfirmDialog: () -> Unit,
    showConfirmDialog: Boolean,
    changeIsEditPanelOpen: () -> Unit,
    changeIsPanelOpen: () -> Unit,
    changeIsSearchOpen: () -> Unit,
    changeSelectedMarker: (NamedMarker?) -> Unit,
) {
    if (isEditPanelOpen || isPanelOpen || isSearchOpen) {
        DismissOverlay(
            changeShowConfirmDialog = { changeShowConfirmDialog() },
            showConfirmDialog = showConfirmDialog,
            onClosePanel = {
                when {
                    isPanelOpen -> changeIsPanelOpen()
                    isSearchOpen -> changeIsSearchOpen()
                    else -> changeIsEditPanelOpen()
                }

                changeSelectedMarker(null)
            },
        )
    }
}

@Composable
private fun MapFloatingButtons(
    modifier: Modifier = Modifier,
    showIntroShowCase: Boolean,
    changeShowMapIntro: () -> Unit,
    changeIsSearchOpen: () -> Unit,
    changeIsFollowing: () -> Unit,
    toggleFollowing: () -> Unit,
    isFollowing: Boolean,
    navController: NavHostController,
) {
    IntroShowcase(
        showIntroShowCase = showIntroShowCase,
        dismissOnClickOutside = true,
        onShowCaseCompleted = {
            changeShowMapIntro()
        },
    ) {
        Box(
            modifier =
                Modifier.fillMaxSize(),
        ) {
            Text(
                text = "マーカーチュートリアル",
                color = Color.Transparent,
                modifier =
                    Modifier
                        .introShowCaseTarget(
                            index = 3,
                            style =
                                ShowcaseStyle.Default.copy(
                                    backgroundColor = Color(0xFF000000),
                                    backgroundAlpha = 0.98f,
                                    targetCircleColor = Color(0xFF343434),
                                ),
                            content = {
                                Column {
                                    Text(
                                        text = "・まずはマーカーをセット！",
                                        color = Color.White,
                                        fontSize = 24.sp,
                                        fontWeight = FontWeight.Bold,
                                    )
                                    Text(
                                        text = "マップ上をタップすると新しくマーカーを設置できます",
                                        color = Color.White,
                                        fontSize = 16.sp,
                                    )
                                    Spacer(modifier = Modifier.height(15.dp))

                                    Text(
                                        text = "・そして",
                                        color = Color.White,
                                        fontSize = 24.sp,
                                        fontWeight = FontWeight.Bold,
                                    )
                                    Text(
                                        text = "設置したマーカーをタップするとマーカーを編集できます",
                                        color = Color.White,
                                        fontSize = 16.sp,
                                    )
                                    Spacer(modifier = Modifier.height(10.dp))

                                    Icon(
                                        Icons.Default.Menu,
                                        contentDescription = null,
                                        modifier =
                                            Modifier
                                                .size(80.dp)
                                                .align(Alignment.End),
                                        tint = Color.Transparent,
                                    )
                                }
                            },
                        )
                        .align(Alignment.Center),
            )
        }

        Row(
            modifier = modifier,
            horizontalArrangement = Arrangement.spacedBy(30.dp),
        ) {
            FloatingActionButton(
                onClick = { changeIsSearchOpen() },
                contentColor = Color.White,
                containerColor = Color(0xFF0889B8),
                modifier =
                    Modifier
                        .size(72.dp)
                        .introShowCaseTarget(
                            index = 2,
                            style =
                                ShowcaseStyle.Default.copy(
                                    backgroundColor = Color(0xFF000000),
                                    backgroundAlpha = 0.95f,
                                    targetCircleColor = Color.White,
                                ),
                            content = {
                                Column {
                                    Text(
                                        text = "簡易検索ボタン",
                                        color = Color.White,
                                        fontSize = 24.sp,
                                        fontWeight = FontWeight.Bold,
                                    )
                                    Text(
                                        text = "タップすると設置したマーカーを簡単な条件で検索できます",
                                        color = Color.White,
                                        fontSize = 16.sp,
                                    )
                                    Spacer(modifier = Modifier.height(10.dp))
                                    Icon(
                                        Icons.Default.Menu,
                                        contentDescription = null,
                                        modifier =
                                            Modifier
                                                .size(80.dp)
                                                .align(Alignment.End),
                                        tint = Color.Transparent,
                                    )
                                }
                            },
                        ),
            ) {
                Icon(
                    Icons.Default.Search,
                    contentDescription = "検索",
                    modifier = Modifier.size(32.dp),
                )
            }

            FloatingActionButton(
                onClick = {
                    toggleFollowing()
                    changeIsFollowing()
                },
                containerColor =
                    if (isFollowing) {
                        Color(0xFF0889B8)
                    } else {
                        Color.White
                    },
                modifier =
                    Modifier
                        .size(72.dp)
                        .introShowCaseTarget(
                            index = 0,
                            style =
                                ShowcaseStyle.Default.copy(
                                    backgroundColor = Color(0xFF000000),
                                    backgroundAlpha = 0.95f,
                                    targetCircleColor = Color.White,
                                ),
                            content = {
                                Column {
                                    Text(
                                        text = "追従ボタン",
                                        color = Color.White,
                                        fontSize = 24.sp,
                                        fontWeight = FontWeight.Bold,
                                    )
                                    Text(
                                        text = "タップすると一定時間ごとに現在地にカメラが戻ります",
                                        color = Color.White,
                                        fontSize = 16.sp,
                                    )
                                    Spacer(modifier = Modifier.height(10.dp))
                                    Icon(
                                        Icons.Default.Menu,
                                        contentDescription = null,
                                        modifier =
                                            Modifier
                                                .size(80.dp)
                                                .align(Alignment.End),
                                        tint = Color.Transparent,
                                    )
                                }
                            },
                        ),
            ) {
                Icon(
                    painterResource(id = R.drawable.location_searching_24px),
                    modifier = Modifier.size(32.dp),
                    contentDescription = "追従",
                    tint =
                        if (isFollowing) {
                            Color.White
                        } else {
                            Color(0xFF0889B8)
                        },
                )
            }

            FloatingActionButton(
                onClick = { navController.navigate("marker_list") },
                contentColor = Color.White,
                containerColor = Color(0xFF0889B8),
                modifier =
                    Modifier
                        .size(72.dp)
                        .introShowCaseTarget(
                            index = 1,
                            style =
                                ShowcaseStyle.Default.copy(
                                    backgroundColor = Color(0xFF000000),
                                    backgroundAlpha = 0.95f,
                                    targetCircleColor = Color.White,
                                ),
                            content = {
                                Column {
                                    Text(
                                        text = "マーカ一覧ボタン",
                                        color = Color.White,
                                        fontSize = 24.sp,
                                        fontWeight = FontWeight.Bold,
                                    )
                                    Text(
                                        text = "タップすると設置したマーカーを一覧で表示されます",
                                        color = Color.White,
                                        fontSize = 16.sp,
                                    )
                                    Spacer(modifier = Modifier.height(10.dp))
                                    Icon(
                                        Icons.Default.Menu,
                                        contentDescription = null,
                                        modifier =
                                            Modifier
                                                .size(80.dp)
                                                .align(Alignment.End),
                                        tint = Color.Transparent,
                                    )
                                }
                            },
                        ),
            ) {
                Icon(
                    Icons.Default.Menu,
                    contentDescription = "マーカ一覧",
                    modifier = Modifier.size(32.dp),
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MapPanel(
    modifier: Modifier = Modifier,
    isSearchOpen: Boolean,
    changeIsSearchOpen: () -> Unit,
    titleResults: List<NamedMarker>,
    memoResults: List<NamedMarker>,
    titleQuery: String?,
    memoQuery: String?,
    changeTitleQuery: (String) -> Unit,
    changeMemoQuery: (String) -> Unit,
    changeSelectedMarker: (NamedMarker?) -> Unit,
    changeIsEditPanelOpen: () -> Unit,
    cameraPositionState: CameraPositionState,
    tempMarkerPosition: LatLng?,
    tempMarkerName: String?,
    changeTempMarkerPosition: (LatLng?) -> Unit,
    changeTempMarkerName: (String?) -> Unit,
    changeIsPanelOpen: () -> Unit,
    changePanelOpen: (Boolean) -> Unit,
    permanentMarkers: List<NamedMarker>,
    addAllVisibleMarkers: (List<NamedMarker>) -> Unit,
    addMarker: (NamedMarker) -> Unit,
    removeMarker: (String) -> Unit,
    updateMarker: (NamedMarker) -> Unit,
    updateMarkerMemoEmbedding: (NamedMarker, String) -> Unit,
    changeShowConfirmDialog: () -> Unit,
    showConfirmDialog: Boolean,
    context: Context,
    selectedAddress: StateFlow<String>,
    isPanelOpen: Boolean,
    isEditPanelOpen: Boolean,
    removeVisibleMarkers: (NamedMarker) -> Unit,
    selectedMarker: NamedMarker?,
    updateVisibleMarkers: (CameraPositionState, List<NamedMarker>) -> Unit,
    saveMarkers: () -> Unit,
) {
    val focusManager = LocalFocusManager.current
    val setSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val editSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val searchSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = false)

    if (isSearchOpen) {
        ModalBottomSheet(
            onDismissRequest = { changeIsSearchOpen() },
            sheetState = searchSheetState,
            modifier = modifier,
        ) {
            SearchMaker(
                titleResults = titleResults,
                memoResults = memoResults,
                titleQuery = titleQuery,
                memoQuery = memoQuery,
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
    }

    if (isPanelOpen) {
        ModalBottomSheet(
            onDismissRequest = { changePanelOpen(false) },
            sheetState = setSheetState,
        ) {
            SetMarkerPanel(
                showConfirmDialog = showConfirmDialog,
                changeShowConfirmDialog = { changeShowConfirmDialog() },
                cameraPositionState = cameraPositionState,
                focusManager = focusManager,
                tempMarkerPosition = tempMarkerPosition,
                tempMarkerName = tempMarkerName,
                onClose = { changePanelOpen(false) },
                resetTempMarkers = {
                    changeTempMarkerPosition(null)
                    changeTempMarkerName(null)
                    changeIsPanelOpen()
                },
                changeTempMarkerName = { changeTempMarkerName(it) },
                addVisibleMarker = { addAllVisibleMarkers(listOf(it)) },
                addMarker = { addMarker(it) },
            )
        }
    }

    if (isEditPanelOpen && selectedMarker != null) {
        ModalBottomSheet(
            onDismissRequest = {
                changeIsEditPanelOpen()
                changeSelectedMarker(null)
            },
            sheetState = editSheetState,
        ) {
            EditPanel(
                selectedMarker = selectedMarker,
                selectedAddress = selectedAddress,
                permanentMarkers = permanentMarkers,
                mapsSaveMarker = { saveMarkers() },
                focusManager = focusManager,
                context = context,
                onMarkerUpdate = { updatedMarker ->
                    updateMarker(updatedMarker)
                    changeSelectedMarker(updatedMarker)
                    updateVisibleMarkers(cameraPositionState, permanentMarkers)
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
                showConfirmDialog = showConfirmDialog,
                changeShowConfirmDialog = { changeShowConfirmDialog() },
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MapScreenPreview() {
    val dummyNavController = rememberNavController()

    val dummyUiState = MapsUiState()
    val dummyListState = ListState()
    val dummySelectedAddress = MutableStateFlow("東京都渋谷区")

    MapScreen(
        latitude = 35.6812,
        longitude = 139.7671,
        navController = dummyNavController,
        uiState = dummyUiState,
        listState = dummyListState,
        changeIsFollowing = {},
        changeIsEditPanelOpen = {},
        changeIsPanelOpen = {},
        changeIsSearchOpen = {},
        changeTitleQuery = {},
        changeMemoQuery = {},
        changeSelectedMarker = {},
        changeTempMarkerName = {},
        changeTempMarkerPosition = {},
        updateVisibleMarkers = { _, _ -> },
        removeVisibleMarkers = {},
        addAllVisibleMarkers = {},
        changeUserLocation = {},
        changePanelOpen = {},
        loadMarkers = {},
        saveMarkers = {},
        updateMarker = {},
        toggleFollowing = {},
        startLocationUpdates = { _, _, _ -> },
        fetchAddressForLatLng = { _, _ -> },
        updateSearchList = { _, _, _ -> },
        selectedAddress = dummySelectedAddress,
        permanentMarkers = emptyList(),
        setVisibleMarkers = {},
        addMarker = {},
        removeMarker = {},
        updateMarkerMemoEmbedding = { _, _ -> },
        changeShowMapIntro = {},
        changeShowConfirmDialog = {},
        checkGoogleMapState = {},
        googleMapState = MapState.Success(true),
    )
}
