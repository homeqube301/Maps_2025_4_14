package com.example.maps20250414.ui.screen.markerList

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.maps20250414.ui.stateholder.ListViewModel
import java.util.Calendar

@Composable
fun DetailSearchScreen(
    navController: NavHostController,
    listViewModel: ListViewModel = hiltViewModel()
) {
    val listState by listViewModel.listState.collectAsState()

    // 作成日（開始日）選択用
    if (listState.openStartDatePicker) {
        DatePickerDialog(
            onDismissRequest = {
                listViewModel.chengeStartDatePicker(false)
            },
            onDateSelected = { year, month, dayOfMonth ->
                listViewModel.changeStartDate(
                    "$year-${String.format("%02d", month + 1)}-${
                        String.format(
                            "%02d",
                            dayOfMonth
                        )
                    }"
                )
                listViewModel.chengeStartDatePicker(false)
            }
        )
    }

    // 作成日（終了日）選択用
    if (listState.openEndDatePicker) {
        DatePickerDialog(
            onDismissRequest = {
                listViewModel.chengeEndDatePicker(false)
            },
            onDateSelected = { year, month, dayOfMonth ->
                listViewModel.changeEndDate(
                    "$year-${String.format("%02d", month + 1)}-${
                        String.format(
                            "%02d",
                            dayOfMonth
                        )
                    }"
                )
                listViewModel.chengeEndDatePicker(false)
            }
        )
    }

    // 詳細検索画面のコンテンツ
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // タイトル
        Text(text = "詳細検索", style = MaterialTheme.typography.bodySmall)

        // マーカー名検索フィールド
        TextField(
            value = listState.markerName ?: "",
            onValueChange = {
                //markerName = it
                listViewModel.changeMarkerName(it)
            },
            label = { Text("マーカー名") },
            modifier = Modifier.fillMaxWidth()
        )

        // 作成日（開始日）検索フィールド
        OutlinedButton(
            onClick = {
                listViewModel.chengeStartDatePicker(true)
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = if (listState.startDate.isNullOrEmpty()) {
                    "作成日（開始日）を選択"
                } else {
                    listState.startDate!!
                }
            )
        }

        // 作成日（終了日）検索フィールド
        OutlinedButton(
            onClick = {
                listViewModel.chengeEndDatePicker(true)
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = if (listState.endDate.isNullOrEmpty()) {
                    "作成日（開始日）を選択"
                } else {
                    listState.endDate!!
                }
            )
        }

        // メモ検索フィールド
        TextField(
            value = listState.memo ?: "",
            onValueChange = {
                listViewModel.changeMemo(it)
            },
            label = { Text("メモ") },
            modifier = Modifier.fillMaxWidth()
        )

        // 戻るボタン
        Button(onClick = { navController.popBackStack() }) {
            Text("戻る")
        }

        // 検索ボタン
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = {
                // 検索条件を渡して遷移
                navController.navigate(
                    "marker_list"
//                            +
//                            "markerName=${Uri.encode(listState.markerName ?: "")}&" +
//                            "startDate=${Uri.encode(listState.startDate ?: "")}&" +
//                            "endDate=${Uri.encode(listState.endDate ?: "")}&" +
//                            "memo=${Uri.encode(listState.memo ?: "")}"
                )
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("検索する")
        }
    }
}

@Composable
fun DatePickerDialog(
    onDismissRequest: () -> Unit,
    onDateSelected: (year: Int, month: Int, dayOfMonth: Int) -> Unit
) {
    val context = LocalContext.current
    val calendar = Calendar.getInstance()

    val datePickerDialog = android.app.DatePickerDialog(
        context,
        { _, year, month, dayOfMonth ->
            onDateSelected(year, month, dayOfMonth)
        },
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)
    )

    // ダイアログの表示
    LaunchedEffect(context) {
        datePickerDialog.show()
    }

    // ダイアログが閉じられた時に呼び出される処理
    DisposableEffect(onDismissRequest) {
        onDispose {
            datePickerDialog.dismiss()
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DetailSearchScreenPreview() {
    val dummyNavController = rememberNavController()
    DetailSearchScreen(navController = dummyNavController)
}

@Preview(showBackground = true)
@Composable
fun DatePickerDialogPreview() {
    DatePickerDialog(
        onDismissRequest = {},
        onDateSelected = { _, _, _ -> }
    )
}
