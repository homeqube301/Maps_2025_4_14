package com.mKanta.archivemaps.navigation

import androidx.compose.runtime.Composable
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
            MapScreen(
                navController = navController,
                latitude = latitude,
                longitude = longitude,
                listViewModel = listViewModel,
                mapViewModel = mapViewModel,
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
            DetailSearchScreen(navController = navController, listViewModel = listViewModel)
        }
    }
}
