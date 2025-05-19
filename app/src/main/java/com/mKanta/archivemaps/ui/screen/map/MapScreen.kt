package com.mKanta.archivemaps.ui.screen.map

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.maps.android.compose.CameraPositionState
import com.google.maps.android.compose.rememberCameraPositionState
import com.mKanta.archivemaps.R
import com.mKanta.archivemaps.domain.model.NamedMarker
import com.mKanta.archivemaps.ui.state.AccountLoadingState
import com.mKanta.archivemaps.ui.state.MapState
import com.mKanta.archivemaps.ui.state.MapsUiState
import com.mKanta.archivemaps.ui.theme.ArchivemapsTheme
import com.mKanta.archivemaps.utils.initializeMapLogic
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapScreen(
    changeLastCameraPosition: (CameraPositionState) -> Unit,
    onNavigateToMarkerList: () -> Unit,
    latitude: Double,
    longitude: Double,
    uiState: MapsUiState,
    changeIsFollowing: () -> Unit,
    changeIsEditPanelOpen: () -> Unit,
    changeIsPanelOpen: () -> Unit,
    changeIsSearchOpen: () -> Unit,
    changeTitleQuery: (String) -> Unit,
    changeMemoQuery: (String) -> Unit,
    changeSelectedMarker: (NamedMarker?) -> Unit,
    changeTempMarkerName: (String?) -> Unit,
    changeTempMarkerMemo: (String?) -> Unit,
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
    changeGoogleMapState: (Boolean) -> Unit,
    startDate: String? = null,
    endDate: String? = null,
    markerName: String? = null,
    memo: String? = null,
    filterMarkers: (
        markers: List<NamedMarker>,
        bounds: LatLngBounds?,
        startDate: String?,
        endDate: String?,
        markerName: String?,
        memo: String?,
        similarMarkerIds: List<String>,
    ) -> List<NamedMarker>,
    onAccountSheetOpenChange: (Boolean) -> Unit,
    onSignOut: () -> Unit,
    onDeleteAccount: () -> Unit,
    onAccountNameChange: (String) -> Unit,
    accountName: String = "",
    accountId: String = "",
    onNavigateToAuth: () -> Unit,
    isAccountLoading: AccountLoadingState,
    isSignOut: Boolean = false,
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

        LaunchedEffect(isAccountLoading, isSignOut) {
            if (isAccountLoading == AccountLoadingState.Success(true) && isSignOut) {
                onNavigateToAuth()
            }
        }

        LaunchedEffect(permanentMarkers) {
            initializeMapLogic(
                context = context,
                cameraPositionState = cameraPositionState,
                startDate = startDate.toString(),
                endDate = endDate.toString(),
                markerName = markerName.toString(),
                memo = memo.toString(),
                permanentMarkers = permanentMarkers,
                addAllVisibleMarkers = { addAllVisibleMarkers(it) },
                setVisibleMarkers = { setVisibleMarkers(it) },
                changeUserLocation = { changeUserLocation(it) },
                loadMarkers = { loadMarkers() },
                startLocationUpdates = { context, camera ->
                    startLocationUpdates(context, camera)
                },
                filterMarkers = filterMarkers,
            )
        }

        LaunchedEffect(cameraPositionState, permanentMarkers) {
            snapshotFlow { cameraPositionState.position }
                .collect {
                    updateVisibleMarkers(cameraPositionState, permanentMarkers)
                }
        }

        Scaffold { innerPadding ->
            Box(
                modifier =
                    Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
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
                    changeGoogleMapState = { changeGoogleMapState(it) },
                    fetchAddressForLatLng = { lat, lon -> fetchAddressForLatLng(lat, lon) },
                    context = context,
                    changeIsEditPanelOpen = { changeIsEditPanelOpen() },
                    changeSelectedMarker = { changeSelectedMarker(it) },
                )

                PanelDismissOverlay(
                    isEditPanelOpen = uiState.isEditPanelOpen,
                    isPanelOpen = uiState.isPanelOpen,
                    isSearchOpen = uiState.isSearchOpen,
                    isAccountSheetOpen = uiState.isAccountSheetOpen,
                    changeShowConfirmDialog = { changeShowConfirmDialog() },
                    showConfirmDialog = uiState.showConfirmDialog,
                    changeIsEditPanelOpen = { changeIsEditPanelOpen() },
                    changeIsPanelOpen = { changeIsPanelOpen() },
                    changeIsSearchOpen = { changeIsSearchOpen() },
                    changeSelectedMarker = { changeSelectedMarker(it) },
                )

                when (uiState.googleMapState) {
                    MapState.Success(true) -> {
                        MapFloatingButtons(
                            changeLastCameraPosition = { changeLastCameraPosition(it) },
                            cameraPositionState = cameraPositionState,
                            modifier =
                                Modifier
                                    .align(Alignment.BottomCenter)
                                    .padding(top = 50.dp, end = 5.dp, bottom = 60.dp),
                            showIntroShowCase = uiState.showMapIntro,
                            changeShowMapIntro = { changeShowMapIntro() },
                            changeIsSearchOpen = { changeIsSearchOpen() },
                            changeIsFollowing = { changeIsFollowing() },
                            toggleFollowing = { toggleFollowing() },
                            onNavigateToMarkerList = { onNavigateToMarkerList() },
                            isFollowing = uiState.isFollowing,
                            onAccountClick = { onAccountSheetOpenChange(true) },
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
                    changeTempMarkerMemo = { changeTempMarkerMemo(it) },
                    tempMarkerMemo = uiState.tempMarkerMemo,
                    isAccountSheetOpen = uiState.isAccountSheetOpen,
                    onAccountSheetOpenChange = onAccountSheetOpenChange,
                    onSignOut = onSignOut,
                    onDeleteAccount = onDeleteAccount,
                    accountName = accountName,
                    accountId = accountId,
                    onAccountNameChange = onAccountNameChange,
                    onNavigateToAuth = onNavigateToAuth,
                )
            }

            when (uiState.googleMapState) {
                MapState.Success(true) -> {
                }

                MapState.Loading -> {
                    Box(
                        modifier =
                            Modifier
                                .fillMaxSize()
                                .background(MaterialTheme.colorScheme.background),
                    ) {
                        Column(
                            modifier = Modifier.align(Alignment.Center),
                            horizontalAlignment = Alignment.CenterHorizontally,
                        ) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(48.dp),
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(text = stringResource(R.string.map_loading), fontSize = 16.sp)
                        }
                    }
                }

                else -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center,
                    ) {
                        Column {
                            Text(
                                text = stringResource(R.string.map_error),
                                color = Color.Red,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = stringResource(R.string.map_error_description),
                                fontSize = 16.sp,
                                modifier = Modifier.align(Alignment.CenterHorizontally),
                            )
                        }
                    }
                }
            }

            when (isAccountLoading) {
                AccountLoadingState.Success(true) -> {
                }

                AccountLoadingState.Loading -> {
                    Box(
                        modifier =
                            Modifier
                                .fillMaxSize()
                                .background(MaterialTheme.colorScheme.background),
                    ) {
                        Column(
                            modifier = Modifier.align(Alignment.Center),
                            horizontalAlignment = Alignment.CenterHorizontally,
                        ) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(48.dp),
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = stringResource(R.string.map_loading),
                                fontSize = 16.sp,
                                color = Color.Red,
                            )
                        }
                    }
                }

                else -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center,
                    ) {
                        Column {
                            Text(
                                text = stringResource(R.string.map_error),
                                color = Color.Red,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = stringResource(R.string.map_error_description),
                                color = Color.Red,
                                fontSize = 16.sp,
                                modifier = Modifier.align(Alignment.CenterHorizontally),
                            )
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MapScreenPreview() {
    val dummyUiState = MapsUiState(googleMapState = MapState.Success(true))
    val dummySelectedAddress = MutableStateFlow("東京都渋谷区")

    MapScreen(
        latitude = 35.6812,
        longitude = 139.7671,
        onNavigateToMarkerList = {},
        uiState = dummyUiState,
        changeIsFollowing = {},
        changeIsEditPanelOpen = {},
        changeIsPanelOpen = {},
        changeIsSearchOpen = {},
        changeTitleQuery = {},
        changeMemoQuery = {},
        changeSelectedMarker = {},
        changeTempMarkerName = {},
        changeTempMarkerMemo = {},
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
        startLocationUpdates = { _, _ -> },
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
        changeGoogleMapState = {},
        startDate = null,
        endDate = null,
        markerName = null,
        memo = null,
        filterMarkers = { _, _, _, _, _, _, _ -> emptyList() },
        changeLastCameraPosition = {},
        onAccountSheetOpenChange = {},
        onSignOut = {},
        onDeleteAccount = {},
        onAccountNameChange = {},
        onNavigateToAuth = {},
        isAccountLoading = AccountLoadingState.Success(true),
    )
}
