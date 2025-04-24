package com.example.maps20250414.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.maps20250414.ui.screen.map.MapScreen
import com.example.maps20250414.ui.screen.markerList.MarkerListScreen

@Composable
fun NiaNavHost(
    navController: NavHostController,
    startDestination: String = "map"
) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable("map") {
            // デフォルト位置で地図表示
            MapScreen(navController = navController, latitude = 0.0, longitude = 0.0)
        }
        composable("map/{latitude}/{longitude}") { backStackEntry ->
            // ナビゲーションパラメータ（緯度、経度）を取得
            val latitude =
                backStackEntry.arguments?.getString("latitude")?.toDoubleOrNull() ?: 0.0
            val longitude =
                backStackEntry.arguments?.getString("longitude")?.toDoubleOrNull() ?: 0.0
            MapScreen(navController = navController, latitude = latitude, longitude = longitude)
        }


        composable("marker_list") {
            MarkerListScreen(navController = navController)
        }
    }
}