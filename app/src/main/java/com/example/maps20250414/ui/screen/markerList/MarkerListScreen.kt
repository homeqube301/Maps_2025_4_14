package com.example.maps20250414.ui.screen.markerList

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.example.maps20250414.domain.model.LatLngSerializable
import com.example.maps20250414.domain.model.NamedMarker
import com.example.maps20250414.ui.stateholder.PermanentMarkerViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MarkerListScreen(
    navController: NavHostController,
    viewModel: PermanentMarkerViewModel = hiltViewModel(),
) {
    val markerListState = remember {
        derivedStateOf {
            viewModel.permanentMarkers.sortedBy { it.createdAt }
        }
    }
    val markerList = markerListState.value
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("マーカー一覧") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "戻る")
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(modifier = Modifier.padding(padding)) {
            items(markerList) { marker ->
                MarkerItem(
                    marker = marker,
                    onClick = {
                        navController.navigate("map/${marker.position.latitude}/${marker.position.longitude}")
                    }
                )
                HorizontalDivider()
            }
        }
    }
}

@Composable
fun MarkerItem(marker: NamedMarker, onClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .clickable { onClick() }
    ) {

        Text(text = "ID: ${marker.id}", style = MaterialTheme.typography.bodySmall)
        Text(text = marker.title, style = MaterialTheme.typography.titleMedium)
        if (!marker.memo.isNullOrBlank()) {
            Text(
                text = marker.memo,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.bodyMedium
            )
        }
        Text(
            text = "作成日時: ${marker.createdAt}",
            style = MaterialTheme.typography.labelSmall,
            modifier = Modifier.padding(top = 4.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun MarkerItemPreview() {
    val dummyMarker = NamedMarker(
        id = 1.toString(),
        title = "東京タワー",
        memo = "観光地の名所です。",
        createdAt = "2025-04-24 18:00",
        position = LatLngSerializable(35.6586, 139.7454)
    )

    MaterialTheme {
        MarkerItem(marker = dummyMarker, onClick = {})
    }
}


