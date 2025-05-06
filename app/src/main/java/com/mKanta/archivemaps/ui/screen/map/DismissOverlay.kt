package com.mKanta.archivemaps.ui.screen.map

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun DismissOverlay(
    showConfirmDialog: Boolean,
    changeShowConfirmDialog: () -> Unit,
    onClosePanel: () -> Unit,
) {
    Box(
        modifier =
            Modifier
                .fillMaxSize()
                .background(Color.Transparent)
                .clickable(
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() },
                ) {
                    changeShowConfirmDialog()
                },
    )

    if (showConfirmDialog) {
        AlertDialog(
            onDismissRequest = { changeShowConfirmDialog() },
            title = { Text("設定を中断しますか？") },
            text = { Text("入力した内容は破棄されます。") },
            confirmButton = {
                Button(onClick = {
                    changeShowConfirmDialog()
                    onClosePanel()
                }) {
                    Text("はい")
                }
            },
            dismissButton = {
                Button(onClick = {
                    changeShowConfirmDialog()
                }) {
                    Text("いいえ")
                }
            },
        )
    }
}

@Preview(showBackground = true)
@Composable
fun DismissOverlayPreview() {
    DismissOverlay(
        showConfirmDialog = true,
        changeShowConfirmDialog = {},
        onClosePanel = {},
    )
}
