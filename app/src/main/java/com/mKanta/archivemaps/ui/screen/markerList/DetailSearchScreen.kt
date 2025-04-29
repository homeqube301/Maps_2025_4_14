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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.mKanta.archivemaps.ui.stateholder.ListViewModel
import java.time.Instant
import java.time.ZoneId

@Composable
fun DetailSearchScreen(
    navController: NavHostController,
    listViewModel: ListViewModel,
) {
    val listState by listViewModel.listState.collectAsState()

    // 作成日（開始日）選択用
    if (listState.openStartDatePicker) {
        ComposeDatePickerDialog(
            onDismissRequest = {
                listViewModel.chengeStartDatePicker()
            },
            onDateSelected = { year, month, dayOfMonth ->
                listViewModel.changeStartDate(
                    "$year-${String.format("%02d", month + 1)}-${
                        String.format(
                            "%02d",
                            dayOfMonth,
                        )
                    }",
                )
                listViewModel.chengeStartDatePicker()
            },
        )
    }

    // 作成日（終了日）選択用
    if (listState.openEndDatePicker) {
        ComposeDatePickerDialog(
            onDismissRequest = {
                listViewModel.chengeEndDatePicker()
            },
            onDateSelected = { year, month, dayOfMonth ->
                listViewModel.changeEndDate(
                    "$year-${String.format("%02d", month + 1)}-${String.format("%02d", dayOfMonth)}"
                )
                listViewModel.chengeEndDatePicker()
            }
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
                listViewModel.changeMarkerName(it)
            },
            label = { Text("マーカー名") },
            modifier = Modifier.fillMaxWidth(),
        )

        // 作成日（開始日）検索フィールド
        OutlinedButton(
            onClick = {
                listViewModel.chengeStartDatePicker()
            },
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text(
                text =
                    if (listState.startDate.isNullOrEmpty()) {
                        "作成日（開始日）を選択"
                    } else {
                        listState.startDate!!
                    },
            )
        }

        // 作成日（終了日）検索フィールド
        OutlinedButton(
            onClick = {
                listViewModel.chengeEndDatePicker()
            },
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text(
                text =
                    if (listState.endDate.isNullOrEmpty()) {
                        "作成日（開始日）を選択"
                    } else {
                        listState.endDate!!
                    },
            )
        }

        // メモ検索フィールド
        TextField(
            value = listState.memo ?: "",
            onValueChange = {
                listViewModel.changeMemo(it)
            },
            label = { Text("メモ") },
            modifier = Modifier.fillMaxWidth(),
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
            tonalElevation = 8.dp
        ) {
            Column(
                modifier = Modifier.padding(24.dp)
            ) {
                DatePicker(state = datePickerState)

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    horizontalArrangement = Arrangement.End,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    TextButton(onClick = {
                        onDismissRequest()
                    }) {
                        Text("キャンセル")
                    }

                    TextButton(onClick = {
                        onDismissRequest()
                        datePickerState.selectedDateMillis?.let { millis ->
                            val date = Instant.ofEpochMilli(millis)
                                .atZone(ZoneId.systemDefault())
                                .toLocalDate()
                            onDateSelected(date.year, date.monthValue - 1, date.dayOfMonth)
                        }
                    })
                    {
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
    DetailSearchScreen(navController = dummyNavController, listViewModel = ListViewModel())
}

@Preview(showBackground = true)
@Composable
fun DatePickerDialogPreview() {
    ComposeDatePickerDialog(
        onDismissRequest = {},
        onDateSelected = { _, _, _ -> },
    )
}
