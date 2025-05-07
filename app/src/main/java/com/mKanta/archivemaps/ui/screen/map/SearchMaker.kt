package com.mKanta.archivemaps.ui.screen.map

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mKanta.archivemaps.R
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
    var isSearchingByTitle by remember { mutableStateOf(true) }

    Box(
        modifier =
            Modifier
                .fillMaxSize()
                .padding(16.dp),
    ) {
        Column(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .align(Alignment.TopStart)
                    .padding(bottom = 72.dp),
        ) {
            Text(
                text = "簡易検索",
                fontSize = 16.sp,
                modifier = Modifier.align(Alignment.CenterHorizontally),
            )

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier =
                    Modifier
                        .fillMaxWidth(),
            ) {
                OutlinedTextField(
                    value = if (isSearchingByTitle) titleQuery.orEmpty() else memoQuery.orEmpty(),
                    onValueChange = {
                        if (isSearchingByTitle) onTitleQueryChanged(it) else onMemoQueryChanged(it)
                    },
                    label = {
                        Text(if (isSearchingByTitle) "マーカー名で検索" else "メモ内容で検索")
                    },
                    modifier = Modifier.weight(1f),
                )
                Spacer(modifier = Modifier.width(3.dp))
                FloatingActionButton(onClick = { isSearchingByTitle = !isSearchingByTitle }) {
                    Icon(
                        painter = painterResource(id = R.drawable.cached_24px),
                        contentDescription = "切り替え",
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            val results = if (isSearchingByTitle) titleResults else memoResults
            if (results.isEmpty()) {
                Text(
                    text = "検索結果がありません",
                    fontSize = 16.sp,
                    fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                    color = Color.Gray,
                    modifier =
                        Modifier
                            .align(Alignment.CenterHorizontally)
                            .padding(40.dp),
                )
            } else {
                Card(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp),
                    colors =
                        CardDefaults.cardColors(
                            containerColor = Color(0xFFE0F7FA),
                        ),
                    shape = RoundedCornerShape(8.dp),
                    elevation = CardDefaults.cardElevation(4.dp),
                ) {
                    LazyColumn(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    ) {
                        items(results) { marker ->
                            Box(
                                modifier =
                                    Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp)
                                        .border(1.dp, Color.Gray, RoundedCornerShape(4.dp))
                                        .clickable {
                                            if (isSearchingByTitle) {
                                                onMarkerTapped(marker)
                                            } else {
                                                onMemoTapped(marker)
                                            }
                                        }
                                        .padding(16.dp),
                            ) {
                                Text(
                                    text = if (isSearchingByTitle) marker.title else "${marker.title}（メモ一致）",
                                    fontSize = 16.sp,
                                    fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                                )
                            }
                        }
                    }
                }
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
