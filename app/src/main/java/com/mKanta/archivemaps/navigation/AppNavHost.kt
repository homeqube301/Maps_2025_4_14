package com.mKanta.archivemaps.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.mKanta.archivemaps.ui.screen.map.MapScreen
import com.mKanta.archivemaps.ui.screen.markerList.DetailSearchScreen
import com.mKanta.archivemaps.ui.screen.markerList.MarkerListScreen
import com.mKanta.archivemaps.ui.stateholder.ListViewModel
import com.mKanta.archivemaps.ui.stateholder.MapViewModel

@Composable
fun AppNavHost(
    navController: NavHostController,
    startDestination: String = "map/{latitude}/{longitude}",
    listViewModel: ListViewModel = hiltViewModel(),
    mapViewModel: MapViewModel = hiltViewModel(),
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
    ) {
        composable("map/{latitude}/{longitude}") { backStackEntry ->
            // ナビゲーションパラメータ（緯度、経度）を取得
            val latitude =
                backStackEntry.arguments?.getString("latitude")?.toDoubleOrNull() ?: 0.0
            val longitude =
                backStackEntry.arguments?.getString("longitude")?.toDoubleOrNull() ?: 0.0
            val uiState by mapViewModel.uiState.collectAsState()
            val listState by listViewModel.listState.collectAsState()
            MapScreen(
                navController = navController,
                latitude = latitude,
                longitude = longitude,
                // mapViewModel = mapViewModel,
                uiState = uiState,
                listState = listState,
                changeIsFollowing = { mapViewModel.changeIsFollowing() },
                changeIsEditPanelOpen = { mapViewModel.changeIsEditPanelOpen() },
                changeIsPanelOpen = { mapViewModel.changeIsPanelOpen() },
                changeIsSearchOpen = { mapViewModel.changeIsSearchOpen() },
                changeTitleQuery = { mapViewModel.changeTitleQuery(it) },
                changeMemoQuery = { mapViewModel.changeMemoQuery(it) },
                changeSelectedMarker = { mapViewModel.changeSelectedMarker(it) },
                changeTempMarkerName = { mapViewModel.changeTempMarkerName(it) },
                changeTempMarkerPosition = { mapViewModel.changeTempMarkerPosition(it) },
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
                startLocationUpdates = { context, cameraPositionState, onLocationUpdate ->
                    mapViewModel.startLocationUpdates(
                        context,
                        cameraPositionState,
                        onLocationUpdate,
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
                permanentMarkers = mapViewModel.permanentMarkers,
            )
        }
        composable("marker_list?") {
            // MarkerListScreenに検索条件を渡す
            MarkerListScreen(
                navController = navController,
                markerName = listViewModel.listState.value.markerName ?: "",
                startDate = listViewModel.listState.value.startDate ?: "",
                endDate = listViewModel.listState.value.endDate ?: "",
                memo = listViewModel.listState.value.memo ?: "",
                embeddingMemo = listViewModel.listState.value.embeddingMemo ?: "",
                permanetMarkers = mapViewModel.permanentMarkers,
                similarMarkerIds = listViewModel.listState.value.similarMarkerIds,
                changeEmbeddingMemo = { embeddingMemo ->
                    listViewModel.changeEmbeddingMemo(embeddingMemo)
                },
                searchSimilarMarkers = { listViewModel.searchSimilarMarkers() },
            )
        }
        composable("detail_search") {
            val listState by listViewModel.listState.collectAsState()
            DetailSearchScreen(
                navController = navController,
                listState = listState,
                chengeStartDatePicker = { listViewModel.chengeStartDatePicker() },
                chengeEndDatePicker = { listViewModel.chengeEndDatePicker() },
                changeMarkerName = { listViewModel.changeMarkerName(it) },
                changeStartDate = { listViewModel.changeStartDate(it) },
                changeEndDate = { listViewModel.changeEndDate(it) },
                changeEmbeddingMemo = { listViewModel.changeEmbeddingMemo(it) },
                changeMemo = { listViewModel.changeMemo(it) },
            )
        }
    }
}
