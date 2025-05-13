package com.mKanta.archivemaps.ui.screen.markerList

import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.mKanta.archivemaps.R
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
    permanentMarkers: List<NamedMarker>,
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
                permanentMarkers.sortedBy { it.createdAt }
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
            Box(modifier = Modifier.fillMaxSize()) {
                IconButton(
                    onClick = { navController.popBackStack() },
                    modifier =
                        Modifier
                            .align(Alignment.TopStart)
                            .padding(16.dp),
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = stringResource(id = R.string.listSc_back),
                        tint = Color.Black,
                    )
                }

                Column(
                    modifier = Modifier.align(Alignment.Center),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(48.dp),
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = stringResource(id = R.string.listSc_system_searching),
                        fontSize = 16.sp,
                    )
                }
            }
        }

        listUIState is MarkerListUiState.Error -> {
            Text(
                text =
                    listUIState.message
                        ?: stringResource(id = R.string.listSc_system_list_error),
                color = Color.White,
                modifier = Modifier.padding(16.dp),
            )
        }

        embeddingUiState is EmbeddingUiState.Error -> {
            Box(modifier = Modifier.fillMaxSize()) {
                IconButton(
                    onClick = { navController.popBackStack() },
                    modifier =
                        Modifier
                            .align(Alignment.TopStart)
                            .padding(16.dp),
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = stringResource(id = R.string.listSc_back),
                        tint = Color.Black,
                    )
                }

                Column(
                    modifier = Modifier.align(Alignment.Center),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text(
                        text =
                            embeddingUiState.message
                                ?: stringResource(id = R.string.listSc_system_vector_error),
                        fontSize = 16.sp,
                        color = Color.White,
                    )
                }
            }
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
                text = stringResource(id = R.string.listSc_system_error),
                color = Color.Gray,
                modifier = Modifier.padding(16.dp),
            )
        }
    }
}

@Preview
@Composable
fun PreviewMarkerListScreen() {
    MarkerListScreen(
        navController = NavHostController(LocalContext.current),
        markerName = "",
        startDate = "",
        endDate = "",
        memo = "",
        embeddingMemo = "",
        permanentMarkers = emptyList(),
        similarMarkerIds = emptyList(),
        listUIState = MarkerListUiState.Success(emptyList()),
        embeddingUiState = EmbeddingUiState.Error("検索結果が取得できませんでした"),
    )
}
