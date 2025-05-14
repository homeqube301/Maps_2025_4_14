package com.mKanta.archivemaps.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import com.mKanta.archivemaps.domain.model.LatLngSerializable
import com.mKanta.archivemaps.ui.PermissionDeniedScreen
import com.mKanta.archivemaps.ui.screen.map.MapScreen
import com.mKanta.archivemaps.ui.screen.markerList.DetailSearchScreen
import com.mKanta.archivemaps.ui.screen.markerList.MarkerListScreen
import com.mKanta.archivemaps.ui.stateholder.ListViewModel
import com.mKanta.archivemaps.ui.stateholder.MapViewModel

@Composable
fun AppNavHost(
    navController: NavHostController,
    startDestination: String = "marker",
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
    ) {
        navigation(startDestination = "map/{latitude}/{longitude}", route = "marker") {
            composable("map/{latitude}/{longitude}") { backStackEntry ->
                val parentEntry =
                    remember(backStackEntry) {
                        navController.getBackStackEntry("marker")
                    }

                val markerViewModel: ListViewModel = hiltViewModel(parentEntry)

                val mapViewModel: MapViewModel = hiltViewModel()
                val latitude =
                    backStackEntry.arguments?.getString("latitude")?.toDoubleOrNull() ?: 0.0
                val longitude =
                    backStackEntry.arguments?.getString("longitude")?.toDoubleOrNull() ?: 0.0
                val uiState by mapViewModel.uiState.collectAsState()
                val listState by markerViewModel.listState.collectAsState()
                MapScreen(
                    onNavigateToMarkerList = { navController.navigate("marker_list") },
                    latitude = latitude,
                    longitude = longitude,
                    uiState = uiState,
                    startDate = listState.startDate ?: "",
                    endDate = listState.endDate ?: "",
                    markerName = listState.markerName ?: "",
                    memo = listState.memo ?: "",
                    changeShowConfirmDialog = { mapViewModel.changeShowConfirmDialog() },
                    changeIsFollowing = { mapViewModel.changeIsFollowing() },
                    changeIsEditPanelOpen = { mapViewModel.changeIsEditPanelOpen() },
                    changeIsPanelOpen = { mapViewModel.changeIsPanelOpen() },
                    changeIsSearchOpen = { mapViewModel.changeIsSearchOpen() },
                    changeTitleQuery = { mapViewModel.changeTitleQuery(it) },
                    changeMemoQuery = { mapViewModel.changeMemoQuery(it) },
                    changeSelectedMarker = { mapViewModel.changeSelectedMarker(it) },
                    changeTempMarkerName = { mapViewModel.changeTempMarkerName(it) },
                    changeTempMarkerPosition = { mapViewModel.changeTempMarkerPosition(it) },
                    changeShowMapIntro = { mapViewModel.changeShowMapIntro() },
                    updateVisibleMarkers = { cameraPositionState, permanentMarkers ->
                        mapViewModel.updateVisibleMarkers(
                            cameraPositionState,
                            permanentMarkers,
                        )
                    },
                    removeVisibleMarkers = { mapViewModel.removeVisibleMarkers(it) },
                    addAllVisibleMarkers = { mapViewModel.addAllVisibleMarkers(it) },
                    changeUserLocation = { mapViewModel.changeUserLocation(it) },
                    changePanelOpen = { mapViewModel.changePanelOpen(it) },
                    loadMarkers = { mapViewModel.loadMarkers() },
                    saveMarkers = { mapViewModel.saveMarkers() },
                    updateMarker = { mapViewModel.updateMarker(it) },
                    updateMarkerMemoEmbedding = { marker, newMemo ->
                        mapViewModel.updateMarkerMemoEmbedding(marker, newMemo)
                    },
                    addMarker = { mapViewModel.addMarker(it) },
                    removeMarker = { mapViewModel.removeMarker(it) },
                    toggleFollowing = { mapViewModel.toggleFollowing() },
                    startLocationUpdates = { context, cameraPositionState ->
                        mapViewModel.startLocationUpdates(
                            context,
                            cameraPositionState,
                        )
                    },
                    fetchAddressForLatLng = { lat, lon ->
                        mapViewModel.fetchAddressForLatLng(lat, lon)
                    },
                    setVisibleMarkers = { mapViewModel.setVisibleMarkers(it) },
                    updateSearchList = { titleQuery, memoQuery, visibleMarkers ->
                        mapViewModel.updateSearchList(
                            titleQuery,
                            memoQuery,
                            visibleMarkers,
                        )
                    },
                    selectedAddress = mapViewModel.selectedAddress,
                    permanentMarkers = uiState.permanentMarkers,
                    checkGoogleMapState = { mapViewModel.checkGoogleMapState(it) },
                    filterMarkers = { markers, bounds, startDate, endDate, markerName, memo, similarMarkerIds ->
                        markerViewModel.filterMarkers(
                            markers,
                            bounds,
                            startDate,
                            endDate,
                            markerName,
                            memo,
                            similarMarkerIds,
                        )
                    },
                )
            }
            composable("marker_list") { backStackEntry ->
                val parentEntry =
                    remember(backStackEntry) {
                        navController.getBackStackEntry("marker")
                    }

                val markerViewModel: MapViewModel = hiltViewModel(parentEntry)

                val listViewModel: ListViewModel = hiltViewModel()
                val listUIState by listViewModel.listUIState.collectAsState()
                val uiState by markerViewModel.uiState.collectAsState()
                val embeddingUiState by listViewModel.embeddingUiState.collectAsState()
                MarkerListScreen(
                    onNavigateToMap = { navController.navigate("map/{latitude}/{longitude}") },
                    onNavigateToMarker = { position: LatLngSerializable ->
                        navController.navigate("map/${position.latitude}/${position.longitude}")
                    },
                    onNavigateToDetailSearch = { navController.navigate("detail_search") },
                    onNavigateBack = { navController.popBackStack() },
                    markerName = listViewModel.listState.value.markerName ?: "",
                    startDate = listViewModel.listState.value.startDate ?: "",
                    endDate = listViewModel.listState.value.endDate ?: "",
                    memo = listViewModel.listState.value.memo ?: "",
                    embeddingMemo = listViewModel.listState.value.embeddingMemo ?: "",
                    showListIntro = listViewModel.listState.value.showListIntro,
                    permanentMarkers = uiState.permanentMarkers,
                    similarMarkerIds = listViewModel.listState.value.similarMarkerIds,
                    changeEmbeddingMemo = { embeddingMemo ->
                        listViewModel.changeEmbeddingMemo(embeddingMemo)
                    },
                    searchSimilarMarkers = { listViewModel.searchSimilarMarkers() },
                    changeShowListIntro = { listViewModel.changeShowListIntro() },
                    checkListUIState = { filteredMarkerList ->
                        listViewModel.checkListUIState(filteredMarkerList)
                    },
                    listUIState = listUIState,
                    embeddingUiState = embeddingUiState,
                    filterMarkers = { markers, bounds, startDate, endDate, markerName, memo, similarMarkerIds ->
                        listViewModel.filterMarkers(
                            markers,
                            bounds,
                            startDate,
                            endDate,
                            markerName,
                            memo,
                            similarMarkerIds,
                        )
                    },
                )
            }
            composable("detail_search") {
                val listViewModel: ListViewModel = hiltViewModel()
                val listState by listViewModel.listState.collectAsState()
                DetailSearchScreen(
                    onNavigateBack = { navController.popBackStack() },
                    onNavigateToMarkerList = { navController.navigate("marker_list") },
                    listState = listState,
                    changeStartDatePicker = { listViewModel.changeStartDatePicker() },
                    changeEndDatePicker = { listViewModel.changeEndDatePicker() },
                    changeMarkerName = { listViewModel.changeMarkerName(it) },
                    changeStartDate = { listViewModel.changeStartDate(it) },
                    changeEndDate = { listViewModel.changeEndDate(it) },
                    changeEmbeddingMemo = { listViewModel.changeEmbeddingMemo(it) },
                    changeMemo = { listViewModel.changeMemo(it) },
                    changeShowDetailIntro = { listViewModel.changeShowDetailIntro() },
                )
            }
        }
        composable("Location_Permission") {
            PermissionDeniedScreen()
        }
    }
}
