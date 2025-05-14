package com.mKanta.archivemaps.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun PermissionDeniedScreen() {
    Column(
        modifier =
            Modifier.Companion
                .fillMaxSize()
                .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.Companion.CenterHorizontally,
    ) {
        Text(
            "位置情報権限が無い状態ではアプリを使用できません。",
            style = MaterialTheme.typography.bodyLarge,
        )
        Spacer(modifier = Modifier.Companion.height(16.dp))
        Text(
            "設定から位置情報のアクセスを許可してください。",
            style = MaterialTheme.typography.bodyMedium,
        )
    }
}
