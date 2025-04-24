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
            MapScreen(navController = navController)
        }

        composable("marker_list") {
            MarkerListScreen(navController = navController)
        }
    }
}