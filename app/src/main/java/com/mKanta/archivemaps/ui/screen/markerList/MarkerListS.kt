package com.mKanta.archivemaps.ui.screen.markerList

import android.util.Log
import androidx.compose.foundation.background
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.android.gms.maps.model.LatLngBounds
import com.mKanta.archivemaps.R
import com.mKanta.archivemaps.domain.model.LatLngSerializable
import com.mKanta.archivemaps.domain.model.NamedMarker
import com.mKanta.archivemaps.ui.state.EmbeddingUiState
import com.mKanta.archivemaps.ui.state.MarkerListUiState
import com.mKanta.archivemaps.ui.theme.ArchivemapsTheme

@Composable
fun MarkerListScreen(
    onNavigateToMap: () -> Unit = {},
    onNavigateToMarker: (LatLngSerializable) -> Unit = {},
    onNavigateToDetailSearch: () -> Unit = {},
    onNavigateBack: () -> Unit = {},
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
    filterMarkers: (
        markers: List<NamedMarker>,
        bounds: LatLngBounds?,
        startDate: String?,
        endDate: String?,
        markerName: String?,
        memo: String?,
        similarMarkerIds: List<String>,
    ) -> List<NamedMarker> = { _, _, _, _, _, _, _ -> emptyList() },
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

    val filteredMarkerList =
        remember(markerName, startDate, endDate, memo, similarMarkerIds, permanentMarkers) {
            filterMarkers(
                permanentMarkers,
                null,
                startDate.takeIf { it.isNotEmpty() },
                endDate.takeIf { it.isNotEmpty() },
                markerName.takeIf { it.isNotEmpty() },
                memo.takeIf { it.isNotEmpty() },
                similarMarkerIds,
            )
        }

    checkListUIState(filteredMarkerList)

    ArchivemapsTheme {
        Box(
            modifier =
                Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background),
        ) {
            when {
                listUIState is MarkerListUiState.Loading -> {
                    CircularProgressIndicator(modifier = Modifier.fillMaxSize())
                }

                embeddingUiState is EmbeddingUiState.Loading && embeddingMemo.isNotEmpty() -> {
                    Box(modifier = Modifier.fillMaxSize()) {
                        IconButton(
                            onClick = onNavigateBack,
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
                            onClick = onNavigateBack,
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
                        onNavigateToMap = onNavigateToMap,
                        onNavigateToMarker = onNavigateToMarker,
                        onNavigateToDetailSearch = onNavigateToDetailSearch,
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
    }
}

@Preview
@Composable
fun PreviewMarkerListScreen() {
    MarkerListScreen(
        onNavigateToMap = {},
        onNavigateToMarker = {},
        onNavigateToDetailSearch = {},
        onNavigateBack = {},
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
