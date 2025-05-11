package com.mKanta.archivemaps.ui.screen.map

import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mKanta.archivemaps.R
import com.mKanta.archivemaps.domain.model.LatLngSerializable
import com.mKanta.archivemaps.domain.model.NamedMarker
import com.mKanta.archivemaps.ui.theme.ArchivemapsTheme

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
    ArchivemapsTheme {
        var isSearchingByTitle by remember { mutableStateOf(true) }

        Box(
            modifier =
                Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
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
                    text = stringResource(id = R.string.searchMaker_title),
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                )

                SearchMakerName(
                    isSearchingByTitle = isSearchingByTitle,
                    changeSearchingByTitle = { isSearchingByTitle = it },
                    titleQuery = titleQuery,
                    memoQuery = memoQuery,
                    onTitleQueryChanged = onTitleQueryChanged,
                    onMemoQueryChanged = onMemoQueryChanged,
                )

                Spacer(modifier = Modifier.height(16.dp))

                ShowSearchResult(
                    isSearchingByTitle = isSearchingByTitle,
                    titleResults = titleResults,
                    memoResults = memoResults,
                    onMarkerTapped = onMarkerTapped,
                    onMemoTapped = onMemoTapped,
                    modifier =
                        Modifier
                            .align(Alignment.CenterHorizontally)
                            .padding(bottom = 40.dp),
                )
            }
        }
    }
}

@Composable
private fun ShowSearchResult(
    modifier: Modifier = Modifier,
    isSearchingByTitle: Boolean,
    titleResults: List<NamedMarker>,
    memoResults: List<NamedMarker>,
    onMarkerTapped: (NamedMarker) -> Unit,
    onMemoTapped: (NamedMarker) -> Unit,
) {
    val results = if (isSearchingByTitle) titleResults else memoResults
    if (results.isEmpty()) {
        Text(
            text = stringResource(id = R.string.searchMaker_result_no),
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Gray,
            modifier = modifier,
        )
    } else {
        Card(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp),
            colors =
                CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.tertiary,
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
                                }.padding(16.dp),
                    ) {
                        Text(
                            text =
                                if (isSearchingByTitle) {
                                    marker.title
                                } else {
                                    marker.title + (
                                        stringResource(
                                            id = R.string.searchMaker_match_memo,
                                        )
                                    )
                                },
                            color = Color.White,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SearchMakerName(
    isSearchingByTitle: Boolean,
    changeSearchingByTitle: (Boolean) -> Unit,
    titleQuery: String?,
    memoQuery: String?,
    onTitleQueryChanged: (String) -> Unit,
    onMemoQueryChanged: (String) -> Unit,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier =
            Modifier
                .fillMaxWidth(),
    ) {
        OutlinedTextField(
            colors =
                OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                ),
            value = if (isSearchingByTitle) titleQuery.orEmpty() else memoQuery.orEmpty(),
            onValueChange = {
                if (isSearchingByTitle) onTitleQueryChanged(it) else onMemoQueryChanged(it)
            },
            label = {
                Text(
                    if (isSearchingByTitle) {
                        stringResource(id = R.string.searchMaker_search_name)
                    } else {
                        stringResource(
                            id = R.string.searchMaker_search_memo,
                        )
                    },
                    color = Color.Gray,
                )
            },
            modifier =
                Modifier
                    .weight(1f)
                    .padding(10.dp),
        )
        FloatingActionButton(
            containerColor = MaterialTheme.colorScheme.primary,
            onClick = { changeSearchingByTitle(!isSearchingByTitle) },
        ) {
            Icon(
                painter = painterResource(id = R.drawable.cached_24px),
                contentDescription = stringResource(id = R.string.searchMaker_switch),
                tint = Color.White,
            )
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
