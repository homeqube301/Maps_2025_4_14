package com.example.maps20250414.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.maps20250414.model.LatLngSerializable
import com.example.maps20250414.model.MapViewModel
import com.example.maps20250414.model.NamedMarker
import com.example.maps20250414.model.PermanentMarkerViewModel

@Composable
fun SearchMaker(
    titleQuery: String,
    memoQuery: String,
    titleResults: List<NamedMarker>,
    memoResults: List<NamedMarker>,
    onMarkerNameChanged: (String) -> Unit,
    onMarkerTapped: (NamedMarker) -> Unit,
    onMemoNameChanged: (String) -> Unit,
    onMemoTapped: (NamedMarker) -> Unit,
    mapViewModel: MapViewModel = hiltViewModel(),
) {
    val uiState by mapViewModel.uiState.collectAsState()
    Surface(
        modifier = Modifier
            .padding(top = 8.dp)
            .background(Color.White, shape = RoundedCornerShape(8.dp))
            .widthIn(max = 300.dp)
            .padding(12.dp),
        shadowElevation = 4.dp
    ) {
        Column {

            // マーカー名検索
            OutlinedTextField(
                value = titleQuery,
                onValueChange = onMarkerNameChanged,
                label = { Text("マーカー名で検索") },
                modifier = Modifier.fillMaxWidth()
            )

            LazyColumn {
                items(titleResults) { marker ->
                    Text(
                        text = marker.title,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                onMarkerTapped(marker)
                            }
                            .padding(8.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // メモ検索
            OutlinedTextField(
                value = memoQuery,
                onValueChange = onMemoNameChanged,
                label = { Text("メモ内容で検索") },
                modifier = Modifier.fillMaxWidth()
            )

            LazyColumn {
                items(memoResults) { marker ->
                    Text(
                        text = marker.title + "（メモ一致）",
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                onMemoTapped(marker)
                            }
                            .padding(8.dp)
                    )
                }
            }
        }
    }
}

@Preview
@Composable
fun SearchMakerPreview() {
    SearchMaker(
        titleQuery = "",
        memoQuery = "",
        titleResults = listOf(
            NamedMarker(
                position = LatLngSerializable(
                    latitude = 35.681236,
                    longitude = 139.767125
                ),
                title = "マーカー1",
            ),
        ),
        memoResults = listOf(
            NamedMarker(
                position = LatLngSerializable(
                    latitude = 35.681236,
                    longitude = 139.767125
                ),
                title = "マーカー1",
            ),
        ),
        onMarkerNameChanged = {},
        onMarkerTapped = {},
        onMemoNameChanged = {},
        onMemoTapped = {}
    )
}
