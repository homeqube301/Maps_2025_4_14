package com.mKanta.archivemaps.ui.screen.markerList

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.mKanta.archivemaps.ui.state.ListState
import java.time.Instant
import java.time.ZoneId

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

    // 詳細検索画面のコンテンツ
    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .padding(16.dp),
    ) {
        // タイトル
        Text(text = "詳細検索", style = MaterialTheme.typography.bodySmall)

        // マーカー名検索フィールド
        TextField(
            value = listState.markerName ?: "",
            onValueChange = {
                // markerName = it
                changeMarkerName(it)
            },
            label = { Text("マーカー名") },
            modifier = Modifier.fillMaxWidth(),
        )

        // 作成日（開始日）検索フィールド
        OutlinedButton(
            onClick = {
                chengeStartDatePicker()
            },
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text(
                text =
                    if (listState.startDate.isNullOrEmpty()) {
                        "作成日（開始日）を選択"
                    } else {
                        listState.startDate
                    },
            )
        }

        // 作成日（終了日）検索フィールド
        OutlinedButton(
            onClick = {
                chengeEndDatePicker()
            },
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text(
                text =
                    if (listState.endDate.isNullOrEmpty()) {
                        "作成日（開始日）を選択"
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
                    .fillMaxWidth(),
        )
        // メモ検索フィールド
        TextField(
            value = listState.memo ?: "",
            onValueChange = {
                changeMemo(it)
            },
            label = { Text("メモ(完全一致)") },
            modifier =
                Modifier
                    .fillMaxWidth()
                    .height(150.dp),
        )

        // 検索ボタン
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = {
                // 検索条件を渡して遷移
                navController.navigate("marker_list?")
            },
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text("検索する")
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

    Dialog(onDismissRequest = onDismissRequest) {
        Surface(
            shape = MaterialTheme.shapes.medium,
            tonalElevation = 8.dp,
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
            ) {
                DatePicker(state = datePickerState)

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
