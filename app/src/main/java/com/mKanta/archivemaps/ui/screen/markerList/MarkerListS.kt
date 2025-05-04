package com.mKanta.archivemaps.ui.screen.markerList

import android.util.Log
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.mKanta.archivemaps.domain.model.NamedMarker
import com.mKanta.archivemaps.ui.state.EmbeddingUiState
import com.mKanta.archivemaps.ui.state.MarkerListUiState
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

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
    listUIState: MarkerListUiState,
    embeddingUiState: EmbeddingUiState,
    checkListUIState: (List<NamedMarker>) -> Unit = {},
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
            } catch (_: Exception) {
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
                } catch (_: Exception) {
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

    checkListUIState(filteredMarkerList)

    when {
        listUIState is MarkerListUiState.Loading -> {
            CircularProgressIndicator(modifier = Modifier.fillMaxSize())
        }

        embeddingUiState is EmbeddingUiState.Loading && embeddingMemo.isNotEmpty() -> {
            CircularProgressIndicator(modifier = Modifier.fillMaxSize())
        }

        listUIState is MarkerListUiState.Error -> {
            Text(
                text = listUIState.message ?: "リストの読み込み中にエラーが発生しました",
                color = Color.Red,
                modifier = Modifier.padding(16.dp),
            )
        }

        embeddingUiState is EmbeddingUiState.Error -> {
            Text(
                text = embeddingUiState.message ?: "ベクトル検索中にエラーが発生しました",
                color = Color.Red,
                modifier = Modifier.padding(16.dp),
            )
        }

        listUIState is MarkerListUiState.Success -> {
            MarkerListContent(
                navController = navController,
                showListIntro = showListIntro,
                changeShowListIntro = changeShowListIntro,
                filteredMarkerList = filteredMarkerList,
            )
        }

        else -> {
            Text(
                text = "不明な状態です",
                color = Color.Gray,
                modifier = Modifier.padding(16.dp),
            )
        }
    }
}
