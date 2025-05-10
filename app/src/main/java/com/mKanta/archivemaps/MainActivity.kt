package com.mKanta.archivemaps

import android.Manifest
import android.app.Application
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.navigation.compose.rememberNavController
import com.mKanta.archivemaps.navigation.AppNavHost
import com.mKanta.archivemaps.ui.theme.ArchivemapsTheme
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.HiltAndroidApp

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private var isPermissionGranted by mutableStateOf(false)

    private val locationPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            isPermissionGranted = granted
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val permission = Manifest.permission.ACCESS_FINE_LOCATION
        isPermissionGranted = ContextCompat.checkSelfPermission(this, permission) ==
            PackageManager.PERMISSION_GRANTED

        if (!isPermissionGranted) {
            locationPermissionLauncher.launch(permission)
        }

        setContent {
            ArchivemapsTheme(
                darkTheme = isSystemInDarkTheme(),
                dynamicColor = false,
            ) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background,
                ) {
                    if (isPermissionGranted) {
                        val navController = rememberNavController()
                        AppNavHost(navController = navController)
                    } else {
                        PermissionDeniedScreen()
                    }
                }
            }
        }
    }
}

@Composable
fun PermissionDeniedScreen() {
    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            "位置情報権限が無い状態ではアプリを使用できません。",
            style = MaterialTheme.typography.bodyLarge,
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            "設定から位置情報のアクセスを許可してください。",
            style = MaterialTheme.typography.bodyMedium,
        )
    }
}

@HiltAndroidApp
class MyApplication : Application()
