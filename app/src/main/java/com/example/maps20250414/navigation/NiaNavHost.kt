package com.example.maps20250414.navigation

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.maps20250414.ui.screen.map.MapScreen
import com.example.maps20250414.ui.screen.markerList.DetailSearchScreen
import com.example.maps20250414.ui.screen.markerList.MarkerListScreen
import com.example.maps20250414.ui.stateholder.ListViewModel
import com.example.maps20250414.ui.stateholder.PermanentMarkerViewModel

@Composable
fun NiaNavHost(
    navController: NavHostController,
    startDestination: String = "map",
    viewModel: PermanentMarkerViewModel = hiltViewModel(),
    listViewModel: ListViewModel = hiltViewModel()
) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable("map") {
            // デフォルト位置で地図表示
            MapScreen(
                navController = navController,
                latitude = 0.0,
                longitude = 0.0,
                listviewModel = listViewModel
            )
        }
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
                listviewModel = listViewModel
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
                permanetMarkers = viewModel.permanentMarkers,
            )
        }
        composable("detail_search") {
            DetailSearchScreen(navController = navController, listViewModel = listViewModel)
        }
    }
}