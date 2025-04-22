package com.example.maps20250414.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun DismissOverlay(
    mapViewModel: MapViewModel = hiltViewModel()
) {

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.001f)) // ほぼ透明なオーバーレイ
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            ) {
//                isEditPanelOpen = false
//                isPanelOpen = false
//                isSearchOpen = false
//                selectedMarker = null
                mapViewModel.changeIsEditPanelOpen()
                mapViewModel.changeIsPanelOpen()
                mapViewModel.changeIsSearchOpen()
                mapViewModel.changeSelectedMarker(null)
            }
    )
}