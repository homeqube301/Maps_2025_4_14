package com.mKanta.archivemaps.ui.screen.markerList

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.canopas.lib.showcase.IntroShowcase
import com.canopas.lib.showcase.component.ShowcaseStyle
import com.mKanta.archivemaps.R
import com.mKanta.archivemaps.domain.model.LatLngSerializable
import com.mKanta.archivemaps.domain.model.NamedMarker
import com.mKanta.archivemaps.ui.theme.ArchivemapsTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MarkerListContent(
    onNavigateToMap: () -> Unit = {},
    onNavigateToMarker: (LatLngSerializable) -> Unit = {},
    onNavigateToDetailSearch: () -> Unit = {},
    showListIntro: Boolean,
    changeShowListIntro: () -> Unit = {},
    filteredMarkerList: List<NamedMarker>,
) {
    BackHandler {
        onNavigateToMap()
    }

    IntroShowcase(
        showIntroShowCase = showListIntro,
        dismissOnClickOutside = true,
        onShowCaseCompleted = {
            changeShowListIntro()
        },
    ) {
        ArchivemapsTheme {
            Scaffold(
                topBar = {
                    TopAppBar(
                        title = {
                            Text(
                                stringResource(id = R.string.listCn_title),
                                fontWeight = FontWeight.Bold,
                            )
                        },
                        colors =
                            TopAppBarDefaults.topAppBarColors(
                                containerColor = MaterialTheme.colorScheme.tertiary,
                                titleContentColor = Color.White,
                                actionIconContentColor = Color.White,
                                navigationIconContentColor = Color.White,
                            ),
                        navigationIcon = {
                            IconButton(onClick = onNavigateToMap) {
                                Icon(
                                    Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = stringResource(id = R.string.back),
                                )
                            }
                        },
                        actions = {
                            IconButton(
                                onClick = onNavigateToDetailSearch,
                                modifier =
                                    Modifier
                                        .introShowCaseTarget(
                                            index = 0,
                                            style =
                                                ShowcaseStyle.Default.copy(
                                                    backgroundColor = Color.Black,
                                                    backgroundAlpha = 0.94f,
                                                    targetCircleColor = Color.White,
                                                ),
                                            content = {
                                                Column {
                                                    Text(
                                                        text = stringResource(id = R.string.listCn_detailSearch_title),
                                                        color = Color.White,
                                                        fontSize = 24.sp,
                                                        fontWeight = FontWeight.Bold,
                                                    )
                                                    Text(
                                                        text = stringResource(id = R.string.listCn_detailSearch_description),
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
                            ) {
                                Icon(
                                    painter = painterResource(id = R.drawable.manage_search_24px),
                                    modifier = Modifier.size(32.dp),
                                    contentDescription = stringResource(id = R.string.listCn_detailSearch_Button),
                                )
                            }
                        },
                    )
                },
            ) { padding ->
                Box(
                    modifier = Modifier.fillMaxSize(),
                ) {
                    if (filteredMarkerList.isEmpty()) {
                        Text(
                            text = stringResource(id = R.string.listCn_no_marker),
                            fontSize = 18.sp,
                            color = Color.Gray,
                            modifier = Modifier.align(Alignment.Center),
                        )
                    } else {
                        LazyColumn(
                            modifier = Modifier.padding(padding),
                        ) {
                            items(filteredMarkerList) { marker ->
                                MarkerItem(
                                    marker = marker,
                                    onClick = { onNavigateToMarker(marker.position) },
                                    modifier =
                                        Modifier
                                            .fillMaxWidth()
                                            .padding(horizontal = 16.dp),
                                )
                                HorizontalDivider(
                                    modifier = Modifier.padding(horizontal = 16.dp),
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MarkerItem(
    marker: NamedMarker,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier,
        color = Color.Transparent,
        onClick = onClick,
    ) {
        Column(
            modifier = Modifier.padding(vertical = 12.dp),
        ) {
            Text(
                text = marker.title,
                style = MaterialTheme.typography.titleLarge,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
            )
            if (!marker.memo.isNullOrBlank()) {
                Text(
                    text = marker.memo,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(top = 4.dp),
                )
            }
            Text(
                text = stringResource(id = R.string.listCn_setTime) + marker.createdAt,
                color = Color.Gray,
                style = MaterialTheme.typography.labelSmall,
                modifier = Modifier.padding(top = 4.dp),
            )
        }
    }
}

@Preview
@Composable
fun PreviewMarkerListContent() {
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

    MarkerListContent(
        onNavigateToMap = {},
        onNavigateToMarker = {},
        onNavigateToDetailSearch = {},
        showListIntro = false,
        changeShowListIntro = {},
        filteredMarkerList = dummyMarkers,
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
