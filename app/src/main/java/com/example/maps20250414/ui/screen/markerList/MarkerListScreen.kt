package com.example.maps20250414.ui.screen.markerList

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.maps20250414.domain.model.LatLngSerializable
import com.example.maps20250414.domain.model.NamedMarker
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
    permanetMarkers: List<NamedMarker>,
) {

    Log.d("FilterParams", "start=$startDate, end=$endDate, name=$markerName, memo=$memo")
    // 日付のフォーマットを定義
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    val originalFormatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss")

    val markerListState = remember {
        derivedStateOf {
            //viewModel.permanentMarkers.sortedBy { it.createdAt }
            permanetMarkers.sortedBy { it.createdAt }
        }
    }

    // startDate と endDate を LocalDateTime に変換
    val startDateTime = if (startDate.isNotEmpty()) {
        try {
            LocalDate.parse(startDate, formatter)

        } catch (e: Exception) {
            Log.e("MarkerListScreen", "startDate の変換に失敗しました: $e")
            null // パースできなかった場合は null にする
        }
    } else null

    val endDateTime = if (endDate.isNotEmpty()) {
        try {
            LocalDate.parse(endDate, formatter)
        } catch (e: Exception) {
            null // パースできなかった場合は null にする
        }
    } else null

    val filteredMarkerList = markerListState.value.filter { marker ->
        // マーカー名でフィルタリング（空ならスキップ）
        val markerDate: LocalDate? = try {
            val dateTime = LocalDateTime.parse(marker.createdAt, originalFormatter)
            dateTime.toLocalDate()
        } catch (e: Exception) {
            Log.e("MarkerListScreen", "marker.createdAtのパースに失敗: ${marker.createdAt}")
            null
        }

        val matchesDate = if (markerDate != null) {
            (startDateTime == null || !markerDate.isBefore(startDateTime)) &&
                    (endDateTime == null || !markerDate.isAfter(endDateTime))
        } else {
            false
        }

        val matchesName =
            markerName.isEmpty() || marker.title.contains(markerName, ignoreCase = true)
        val matchesMemo = memo.isEmpty() || marker.memo?.contains(memo, ignoreCase = true) == true

        matchesName && matchesDate && matchesMemo

    }


    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("マーカー一覧") },
                navigationIcon = {
                    //IconButton(onClick = { navController.popBackStack() }) {
                    IconButton(onClick = { navController.navigate("map/{latitude}/{longitude}") }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "戻る")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate("detail_search") },
                content = {
                    Icon(Icons.Filled.Search, contentDescription = "詳細検索")
                }
            )
        }
    ) { padding ->
        LazyColumn(modifier = Modifier.padding(padding)) {
            items(filteredMarkerList) { marker ->
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

@Preview
@Composable
fun PreviewMarkerListScreen() {
    val dummyNavController = rememberNavController()

    val dummyMarkers = listOf(
        NamedMarker(
            title = "テストマーカー1",
            memo = "これはメモ1です",
            createdAt = "2024/04/01 00:00:00",
            position = LatLngSerializable(35.0, 139.0)
        ),
        NamedMarker(
            title = "テストマーカー2",
            memo = "これはメモ2です",
            createdAt = "2024/04/05 00:00:00",
            position = LatLngSerializable(36.0, 140.0)
        )
    )

    MarkerListScreen(
        navController = dummyNavController,
        markerName = "テスト",
        startDate = "2024-04-01",
        endDate = "2024-04-10",
        memo = "メモ",
        permanetMarkers = dummyMarkers,
    )
}

@Preview(showBackground = true)
@Composable
fun MarkerItemPreview() {
    val dummyMarker = NamedMarker(
        id = 1.toString(),
        title = "東京タワー",
        memo = "観光地の名所です。",
        createdAt = "2025/04/24 18:00:36",
        position = LatLngSerializable(35.6586, 139.7454)
    )

    MaterialTheme {
        MarkerItem(marker = dummyMarker, onClick = {})
    }
}


