package com.example.maps20250414.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.maps20250414.data.MapsUiState
import com.example.maps20250414.model.LatLngSerializable
import com.example.maps20250414.model.MapViewModel
import com.example.maps20250414.model.NamedMarker
import com.google.android.gms.maps.model.BitmapDescriptorFactory

@Composable
fun DismissOverlay(
    onClosePanel: () -> Unit
) {

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.001f)) // ほぼ透明なオーバーレイ
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            ) {
                onClosePanel()
            }
    )
}

@Preview(showBackground = true)
@Composable
fun DismissOverlayPreview() {
    DismissOverlay(
        onClosePanel = {}
    )
}


