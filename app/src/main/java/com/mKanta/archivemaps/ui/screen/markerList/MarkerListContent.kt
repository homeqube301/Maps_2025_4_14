package com.mKanta.archivemaps.ui.screen.markerList

import androidx.compose.foundation.clickable
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
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.canopas.lib.showcase.IntroShowcase
import com.canopas.lib.showcase.component.ShowcaseStyle
import com.mKanta.archivemaps.R
import com.mKanta.archivemaps.domain.model.LatLngSerializable
import com.mKanta.archivemaps.domain.model.NamedMarker

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MarkerListContent(
    navController: NavHostController,
    showListIntro: Boolean,
    changeShowListIntro: () -> Unit = {},
    filteredMarkerList: List<NamedMarker>,
) {
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
                    title = { Text("マーカーリスト") },
                    navigationIcon = {
                        IconButton(onClick = { navController.navigate("map/{latitude}/{longitude}") }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "戻る")
                        }
                    },
                    actions = {
                        IconButton(
                            onClick = { navController.navigate("detail_search") },
                            modifier =
                                Modifier
                                    .introShowCaseTarget(
                                        index = 0,
                                        style =
                                            ShowcaseStyle.Default.copy(
                                                backgroundColor = Color(0xFF000000),
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
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.manage_search_24px),
                                modifier = Modifier.size(32.dp),
                                contentDescription = "詳細検索",
                            )
                        }
                    },
                )
            },
        ) { padding ->

            Box(
                modifier =
                    Modifier
                        .fillMaxSize(),
            ) {
                if (filteredMarkerList.isEmpty()) {
                    Text(
                        text = "表示できるマーカーがありません",
                        fontSize = 18.sp,
                        color = Color.Gray,
                        modifier = Modifier.align(Alignment.Center),
                    )
                } else {
                    LazyColumn(
                        modifier =
                            Modifier
                                .padding(padding)
                                .padding(16.dp),
                    ) {
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
                .clickable { onClick() },
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
            )
        }
        Text(
            text = "作成日時: ${marker.createdAt}",
            color = Color.Gray,
            style = MaterialTheme.typography.labelSmall,
            modifier = Modifier.padding(top = 4.dp),
        )
        Text(text = "")
    }
}

@Preview
@Composable
fun PreviewMarkerListContent() {
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

    MarkerListContent(
        navController = dummyNavController,
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
