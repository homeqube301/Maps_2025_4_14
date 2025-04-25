package com.example.maps20250414.ui.screen.markerList

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailSearchScreen(navController: NavHostController) {
    // 入力された検索条件を保持するための状態
    var markerName by remember { mutableStateOf("") }
    var startDate by remember { mutableStateOf("") }
    var endDate by remember { mutableStateOf("") }
    var memo by remember { mutableStateOf("") }
    // DatePicker用の状態
    val openStartDatePicker = remember { mutableStateOf(false) }
    val openEndDatePicker = remember { mutableStateOf(false) }

    // 作成日（開始日）選択用
    if (openStartDatePicker.value) {
        DatePickerDialog(
            onDismissRequest = { openStartDatePicker.value = false },
            onDateSelected = { year, month, dayOfMonth ->
                startDate = "$year-${String.format("%02d", month + 1)}-${
                    String.format(
                        "%02d",
                        dayOfMonth
                    )
                }"  // 月は0から始まるため、+1が必要
                openStartDatePicker.value = false
            }
        )
    }

    // 作成日（終了日）選択用
    if (openEndDatePicker.value) {
        DatePickerDialog(
            onDismissRequest = { openEndDatePicker.value = false },
            onDateSelected = { year, month, dayOfMonth ->
                endDate = "$year-${String.format("%02d", month + 1)}-${
                    String.format(
                        "%02d",
                        dayOfMonth
                    )
                }"  // 月は0から始まるため、+1が必要
                openEndDatePicker.value = false
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
            value = markerName,
            onValueChange = { markerName = it },
            label = { Text("マーカー名") },
            modifier = Modifier.fillMaxWidth()
        )

        // 作成日（開始日）検索フィールド
        OutlinedButton(
            onClick = { openStartDatePicker.value = true },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = if (startDate.isEmpty()) "作成日（開始日）を選択" else startDate)
        }

        // 作成日（終了日）検索フィールド
        OutlinedButton(
            onClick = { openEndDatePicker.value = true },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = if (endDate.isEmpty()) "作成日（終了日）を選択" else endDate)
        }

        // メモ検索フィールド
        TextField(
            value = memo,
            onValueChange = { memo = it },
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
                    "marker_list?markerName=$markerName&startDate=$startDate&endDate=$endDate&memo=$memo"
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
