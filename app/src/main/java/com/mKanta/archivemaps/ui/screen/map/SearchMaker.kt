package com.mKanta.archivemaps.ui.screen.map

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mKanta.archivemaps.domain.model.LatLngSerializable
import com.mKanta.archivemaps.domain.model.NamedMarker

@Composable
fun SearchMaker(
    titleQuery: String?,
    memoQuery: String?,
    titleResults: List<NamedMarker>,
    memoResults: List<NamedMarker>,
    onMarkerTapped: (NamedMarker) -> Unit,
    onMemoTapped: (NamedMarker) -> Unit,
    onTitleQueryChanged: (String) -> Unit,
    onMemoQueryChanged: (String) -> Unit,
) {
    Column(
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(16.dp),
    ) {
        Text(
            text = "簡易検索",
            fontSize = 15.sp,
            modifier =
                Modifier
                    .align(Alignment.CenterHorizontally),
        )

        // マーカー名検索
        OutlinedTextField(
            value = titleQuery ?: "",
            // uiState.titleQuery ?: "",
            onValueChange = {
                // mapViewModel.changeTitleQuery(it)
                onTitleQueryChanged(it)
            },
            label = { Text("マーカー名で検索") },
            modifier = Modifier.fillMaxWidth(),
        )

        LazyColumn(
            modifier = Modifier.fillMaxWidth(),
        ) {
            items(
                titleResults,
            ) { marker ->
                Text(
                    text = marker.title,
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .clickable {
                                onMarkerTapped(marker)
                            }.padding(8.dp),
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // メモ検索
        OutlinedTextField(
            value = memoQuery ?: "",
            onValueChange = {
                onMemoQueryChanged(it)
            },
            label = { Text("メモ内容で検索") },
            modifier = Modifier.fillMaxWidth(),
        )

        LazyColumn(
            modifier = Modifier.fillMaxWidth(),
        ) {
            items(
                // uiState.memoResults
                memoResults,
            ) { marker ->
                Text(
                    text = marker.title + "（メモ一致）",
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .clickable {
                                onMemoTapped(marker)
                            }.padding(8.dp),
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewSearchMaker() {
    val dummyMarkers =
        listOf(
            NamedMarker(
                id = "1",
                title = "東京タワー",
                memo = "夜景がきれい",
                position = LatLngSerializable(35.3606, 138.7274),
                colorHue = 0f,
            ),
            NamedMarker(
                id = "2",
                title = "スカイツリー",
                memo = "観光地",
                position = LatLngSerializable(35.6252, 139.2430),
                colorHue = 120f,
            ),
        )
    SearchMaker(
        // uiState = dummyState,
        titleQuery = "東京",
        memoQuery = "観光",
        titleResults = dummyMarkers,
        memoResults = dummyMarkers,
        onTitleQueryChanged = {},
        onMemoQueryChanged = {},
        onMarkerTapped = {},
        onMemoTapped = {},
    )
}
