package com.mKanta.archivemaps.ui.screen.markerList

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.canopas.lib.showcase.IntroShowcase
import com.canopas.lib.showcase.component.ShowcaseStyle
import com.mKanta.archivemaps.ui.state.ListState
import java.time.Instant
import java.time.ZoneId

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailSearchScreen(
    navController: NavHostController,
    chengeStartDatePicker: () -> Unit = {},
    chengeEndDatePicker: () -> Unit = {},
    changeMarkerName: (String) -> Unit = {},
    changeStartDate: (String) -> Unit = {},
    changeEndDate: (String) -> Unit = {},
    changeEmbeddingMemo: (String) -> Unit = {},
    changeMemo: (String) -> Unit = {},
    listState: ListState,
    changeShowDetailIntro: () -> Unit = {},
) {
    if (listState.openStartDatePicker) {
        ComposeDatePickerDialog(
            onDismissRequest = {
                chengeStartDatePicker()
            },
            onDateSelected = { year, month, dayOfMonth ->
                changeStartDate(
                    "$year-${String.format("%02d", month + 1)}-${
                        String.format(
                            "%02d",
                            dayOfMonth,
                        )
                    }",
                )
                chengeStartDatePicker()
            },
        )
    }

    // 作成日（終了日）選択用
    if (listState.openEndDatePicker) {
        ComposeDatePickerDialog(
            onDismissRequest = {
                chengeEndDatePicker()
            },
            onDateSelected = { year, month, dayOfMonth ->
                changeEndDate(
                    "$year-${String.format("%02d", month + 1)}-${
                        String.format(
                            "%02d",
                            dayOfMonth,
                        )
                    }",
                )
                chengeEndDatePicker()
            },
        )
    }

    IntroShowcase(
        showIntroShowCase = listState.showDetailIntro,
        dismissOnClickOutside = true,
        onShowCaseCompleted = {
            changeShowDetailIntro()
        },
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("詳細検索") },
                    navigationIcon = {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "戻る")
                        }
                    },
                )
            },
        ) { paddingValues ->
            Column(
                modifier =
                    Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .padding(16.dp),
            ) {
                TextField(
                    value = listState.markerName ?: "",
                    onValueChange = {
                        changeMarkerName(it)
                    },
                    label = { Text("マーカー名") },
                    modifier =
                        Modifier
                            .introShowCaseTarget(
                                index = 0,
                                style =
                                    ShowcaseStyle.Default.copy(
                                        backgroundColor = Color(0xFF1C0A00),
                                        backgroundAlpha = 0.95f,
                                        targetCircleColor = Color(0xFF343434),
                                    ),
                                content = {
                                    Column {
                                        Text(
                                            text = "名前で検索",
                                            color = Color.White,
                                            fontSize = 24.sp,
                                            fontWeight = FontWeight.Bold,
                                        )
                                        Text(
                                            text = "マーカーに付けられた名前で検索します(完全一致のみ)",
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
                            ).fillMaxWidth(),
                )

                OutlinedButton(
                    onClick = {
                        chengeStartDatePicker()
                    },
                    modifier =
                        Modifier
                            .introShowCaseTarget(
                                index = 1,
                                style =
                                    ShowcaseStyle.Default.copy(
                                        backgroundColor = Color(0xFF1C0A00),
                                        backgroundAlpha = 0.95f,
                                        targetCircleColor = Color(0xFF343434),
                                    ),
                                content = {
                                    Column {
                                        Text(
                                            text = "開始日以降で検索",
                                            color = Color.White,
                                            fontSize = 24.sp,
                                            fontWeight = FontWeight.Bold,
                                        )
                                        Text(
                                            text = "マーカーが作成されたのが選択日時以降である場合リストに表示します",
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
                            ).fillMaxWidth(),
                ) {
                    Text(
                        text =
                            if (listState.startDate.isNullOrEmpty()) {
                                "検索開始日を選択"
                            } else {
                                listState.startDate
                            },
                    )
                }

                OutlinedButton(
                    onClick = {
                        chengeEndDatePicker()
                    },
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .introShowCaseTarget(
                                index = 2,
                                style =
                                    ShowcaseStyle.Default.copy(
                                        backgroundColor = Color(0xFF1C0A00),
                                        backgroundAlpha = 0.95f,
                                        targetCircleColor = Color(0xFF343434),
                                    ),
                                content = {
                                    Column {
                                        Text(
                                            text = "終了日以前で検索",
                                            color = Color.White,
                                            fontSize = 24.sp,
                                            fontWeight = FontWeight.Bold,
                                        )
                                        Text(
                                            text = "マーカーが作成されたのが選択日時以前である場合リストに表示します",
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
                    Text(
                        text =
                            if (listState.endDate.isNullOrEmpty()) {
                                "検索終了日を選択"
                            } else {
                                listState.endDate
                            },
                    )
                }

                // 意味検索フィールド
                TextField(
                    value = listState.embeddingMemo ?: "",
                    onValueChange = {
                        changeEmbeddingMemo(it)
                    },
                    label = { Text("AIメモ検索(意味検索)") },
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .introShowCaseTarget(
                                index = 3,
                                style =
                                    ShowcaseStyle.Default.copy(
                                        backgroundColor = Color(0xFF1C0A00),
                                        backgroundAlpha = 0.95f,
                                        targetCircleColor = Color(0xFF343434),
                                    ),
                                content = {
                                    Column {
                                        Text(
                                            text = "マーカーのメモ内容をAIで検索",
                                            color = Color.White,
                                            fontSize = 24.sp,
                                            fontWeight = FontWeight.Bold,
                                        )
                                        Text(
                                            text = "検索ワードと記録されたメモ内容が意味的に似ているマーカーを10個表示します",
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
                )

                TextField(
                    value = listState.memo ?: "",
                    onValueChange = {
                        changeMemo(it)
                    },
                    label = { Text("メモ(完全一致)") },
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .height(150.dp)
                            .introShowCaseTarget(
                                index = 4,
                                style =
                                    ShowcaseStyle.Default.copy(
                                        backgroundColor = Color(0xFF1C0A00),
                                        backgroundAlpha = 0.95f,
                                        targetCircleColor = Color(0xFF343434),
                                    ),
                                content = {
                                    Column {
                                        Text(
                                            text = "マーカーをメモ内容で検索",
                                            color = Color.White,
                                            fontSize = 24.sp,
                                            fontWeight = FontWeight.Bold,
                                        )
                                        Text(
                                            text = "検索ワードとマーカーに記録されたメモ内容が完全一致したマーカーを表示します",
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
                )

                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = {
                        navController.navigate("marker_list?")
                    },
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text("検索する")
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ComposeDatePickerDialog(
    onDismissRequest: () -> Unit,
    onDateSelected: (year: Int, month: Int, dayOfMonth: Int) -> Unit,
) {
    val datePickerState = rememberDatePickerState()

    Dialog(
        onDismissRequest = onDismissRequest,
        properties = DialogProperties(usePlatformDefaultWidth = false),
    ) {
        Surface(
            shape = MaterialTheme.shapes.medium,
            tonalElevation = 8.dp,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Column(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(0.dp),
            ) {
                DatePicker(
                    state = datePickerState,
                    modifier = Modifier.fillMaxWidth(),
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    horizontalArrangement = Arrangement.End,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    TextButton(onClick = {
                        onDismissRequest()
                    }) {
                        Text("キャンセル")
                    }

                    TextButton(onClick = {
                        onDismissRequest()
                        datePickerState.selectedDateMillis?.let { millis ->
                            val date =
                                Instant
                                    .ofEpochMilli(millis)
                                    .atZone(ZoneId.systemDefault())
                                    .toLocalDate()
                            onDateSelected(date.year, date.monthValue - 1, date.dayOfMonth)
                            onDismissRequest()
                        }
                    }) {
                        Text("OK")
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DetailSearchScreenPreview() {
    val dummyNavController = rememberNavController()
    DetailSearchScreen(
        navController = dummyNavController,
        chengeStartDatePicker = {},
        chengeEndDatePicker = {},
        changeMarkerName = {},
        changeStartDate = {},
        changeEndDate = {},
        changeEmbeddingMemo = {},
        changeMemo = {},
        listState =
            ListState(
                markerName = "テストマーカー",
                startDate = "2025-01-01",
                endDate = "2025-12-31",
                memo = "これはテスト用メモです",
                embeddingMemo = "意味的検索用テキスト",
                openStartDatePicker = false,
                openEndDatePicker = false,
                similarMarkerIds = listOf("id1", "id2", "id3"),
                showDetailIntro = false,
            ),
    )
}

@Preview(showBackground = true)
@Composable
fun DatePickerDialogPreview() {
    ComposeDatePickerDialog(
        onDismissRequest = {},
        onDateSelected = { _, _, _ -> },
    )
}
