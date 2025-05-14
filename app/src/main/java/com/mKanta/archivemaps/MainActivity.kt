package com.mKanta.archivemaps

import android.Manifest
import android.app.Application
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
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
                    val navController = rememberNavController()
                    if (isPermissionGranted) {
                        AppNavHost(navController = navController)
                    } else {
                        AppNavHost(
                            navController = navController,
                            startDestination = "Location_Permission",
                        )
                    }
                }
            }
        }
    }
}

@HiltAndroidApp
class MyApplication : Application()
