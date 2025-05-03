package com.mKanta.archivemaps.ui.screen.markerList

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.canopas.lib.showcase.IntroShowcase
import com.canopas.lib.showcase.component.ShowcaseStyle
import com.mKanta.archivemaps.domain.model.LatLngSerializable
import com.mKanta.archivemaps.domain.model.NamedMarker
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MarkerListScreen(
    navController: NavHostController,
    markerName: String,
    startDate: String,
    endDate: String,
    memo: String,
    embeddingMemo: String,
    permanetMarkers: List<NamedMarker>,
    similarMarkerIds: List<String>,
    changeEmbeddingMemo: (String) -> Unit = {},
    searchSimilarMarkers: () -> Unit = {},
    showListIntro: Boolean = true,
    changeShowListIntro: () -> Unit = {},
) {
    LaunchedEffect(embeddingMemo) {
        if (embeddingMemo.isNotEmpty()) {
            changeEmbeddingMemo(embeddingMemo)
            searchSimilarMarkers()
        }
    }

    Log.d(
        "FilterParams",
        "start=$startDate, end=$endDate, name=$markerName, memo=$memo, embeddingMemo=$embeddingMemo",
    )

    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    val originalFormatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss")

    val markerListState =
        remember {
            derivedStateOf {
                permanetMarkers.sortedBy { it.createdAt }
            }
        }

    val startDateTime =
        if (startDate.isNotEmpty()) {
            try {
                LocalDate.parse(startDate, formatter)
            } catch (e: Exception) {
                Log.e("MarkerListScreen", "startDate の変換に失敗しました: $e")
                null
            }
        } else {
            null
        }

    val endDateTime =
        if (endDate.isNotEmpty()) {
            try {
                LocalDate.parse(endDate, formatter)
            } catch (e: Exception) {
                null
            }
        } else {
            null
        }

    val filteredMarkerList =
        markerListState.value.filter { marker ->
            val markerDate: LocalDate? =
                try {
                    val dateTime = LocalDateTime.parse(marker.createdAt, originalFormatter)
                    dateTime.toLocalDate()
                } catch (e: Exception) {
                    Log.e("MarkerListScreen", "marker.createdAtのパースに失敗: ${marker.createdAt}")
                    null
                }

            val matchesDate =
                if (markerDate != null) {
                    (startDateTime == null || !markerDate.isBefore(startDateTime)) &&
                        (endDateTime == null || !markerDate.isAfter(endDateTime))
                } else {
                    false
                }

            val matchesName =
                markerName.isEmpty() || marker.title.contains(markerName, ignoreCase = true)
            val matchesMemo =
                memo.isEmpty() || marker.memo?.contains(memo, ignoreCase = true) == true

            val matchesEmbedding =
                similarMarkerIds.isEmpty() || marker.id in similarMarkerIds

            matchesName && matchesDate && matchesMemo && matchesEmbedding
        }

    IntroShowcase(
        showIntroShowCase = showListIntro,
        dismissOnClickOutside = true,
        onShowCaseCompleted = {
            changeShowListIntro()
        },
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("マーカー一覧") },
                    navigationIcon = {
                        IconButton(onClick = { navController.navigate("map/{latitude}/{longitude}") }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "戻る")
                        }
                    },
                )
            },
            floatingActionButton = {
                FloatingActionButton(
                    onClick = { navController.navigate("detail_search") },
                    modifier =
                        Modifier
                            .introShowCaseTarget(
                                index = 0,
                                style =
                                    ShowcaseStyle.Default.copy(
                                        backgroundColor = Color(0xFF1C0A00),
                                        backgroundAlpha = 0.94f,
                                        targetCircleColor = Color.White,
                                    ),
                                content = {
                                    Column {
                                        Text(
                                            text = "詳細検索ボタン",
                                            color = Color.White,
                                            fontSize = 24.sp,
                                            fontWeight = FontWeight.Bold,
                                        )
                                        Text(
                                            text = "タップすると設置したマーカーを細かい条件で検索できます",
                                            color = Color.White,
                                            fontSize = 16.sp,
                                        )
                                        Spacer(modifier = Modifier.height(10.dp))
                                        Icon(
                                            Icons.Default.Menu,
                                            contentDescription = null,
                                            modifier =
                                                Modifier
                                                    .size(80.dp)
                                                    .align(Alignment.End),
                                            tint = Color.Transparent,
                                        )
                                    }
                                },
                            ),
                    content = {
                        Icon(Icons.Filled.Search, contentDescription = "詳細検索")
                    },
                )
            },
        ) { padding ->
            LazyColumn(modifier = Modifier.padding(padding)) {
                items(filteredMarkerList) { marker ->
                    MarkerItem(
                        marker = marker,
                        onClick = {
                            navController.navigate("map/${marker.position.latitude}/${marker.position.longitude}")
                        },
                    )
                    HorizontalDivider()
                }
            }
        }
    }
}

@Composable
fun MarkerItem(
    marker: NamedMarker,
    onClick: () -> Unit,
) {
    Column(
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .clickable { onClick() },
    ) {
        Text(text = "ID: ${marker.id}", style = MaterialTheme.typography.bodySmall)
        Text(text = marker.title, style = MaterialTheme.typography.titleMedium)
        if (!marker.memo.isNullOrBlank()) {
            Text(
                text = marker.memo,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.bodyMedium,
            )
        }
        Text(
            text = "作成日時: ${marker.createdAt}",
            style = MaterialTheme.typography.labelSmall,
            modifier = Modifier.padding(top = 4.dp),
        )
    }
}

@Preview
@Composable
fun PreviewMarkerListScreen() {
    val dummyNavController = rememberNavController()

    val dummyMarkers =
        listOf(
            NamedMarker(
                title = "テストマーカー1",
                memo = "これはメモ1です",
                createdAt = "2024/04/01 00:00:00",
                position = LatLngSerializable(35.0, 139.0),
            ),
            NamedMarker(
                title = "テストマーカー2",
                memo = "これはメモ2です",
                createdAt = "2024/04/05 00:00:00",
                position = LatLngSerializable(36.0, 140.0),
            ),
        )

    MarkerListScreen(
        navController = dummyNavController,
        markerName = "テスト",
        startDate = "2024-04-01",
        endDate = "2024-04-10",
        memo = "メモ",
        embeddingMemo = "埋め込みメモ",
        permanetMarkers = dummyMarkers,
        similarMarkerIds = emptyList(),
        changeEmbeddingMemo = {},
        searchSimilarMarkers = {},
        showListIntro = false,
        changeShowListIntro = {},
    )
}

@Preview(showBackground = true)
@Composable
fun MarkerItemPreview() {
    val dummyMarker =
        NamedMarker(
            id = 1.toString(),
            title = "東京タワー",
            memo = "観光地の名所です。",
            createdAt = "2025/04/24 18:00:36",
            position = LatLngSerializable(35.6586, 139.7454),
        )

    MaterialTheme {
        MarkerItem(marker = dummyMarker, onClick = {})
    }
}
