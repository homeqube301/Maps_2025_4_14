package com.mKanta.archivemaps

import android.Manifest
import android.app.Application
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.rememberNavController
import com.mKanta.archivemaps.domain.repository.AuthRepository
import com.mKanta.archivemaps.navigation.AppNavHost
import com.mKanta.archivemaps.ui.theme.ArchivemapsTheme
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private var isPermissionGranted by mutableStateOf(false)
    private var isPermissionChecked by mutableStateOf(false)
    private var isLoading by mutableStateOf(true)

    @Inject
    lateinit var authRepository: AuthRepository

    private val locationPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            isPermissionGranted = permissions.entries.any { it.value }
            isPermissionChecked = true
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (hasLocationPermission()) {
            isPermissionGranted = true
            isPermissionChecked = true
        } else {
            locationPermissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                ),
            )
        }

        lifecycleScope.launch {
            try {
                val isAuthenticated = authRepository.isAuthenticated()
                isLoading = false

                setContent {
                    ArchivemapsTheme(
                        darkTheme = isSystemInDarkTheme(),
                        dynamicColor = true,
                    ) {
                        val navController = rememberNavController()

                        when {
                            isLoading -> {
                                Box(
                                    modifier =
                                        Modifier
                                            .fillMaxSize()
                                            .background(MaterialTheme.colorScheme.background),
                                    contentAlignment = Alignment.Center,
                                ) {
                                    CircularProgressIndicator()
                                }
                            }

                            !isPermissionChecked -> {
                                Box(
                                    modifier =
                                        Modifier
                                            .fillMaxSize()
                                            .background(MaterialTheme.colorScheme.background),
                                )
                            }

                            isPermissionGranted -> {
                                AppNavHost(
                                    navController = navController,
                                    startDestination = if (isAuthenticated) "marker" else "logIn",
                                )
                            }

                            else -> {
                                AppNavHost(
                                    navController = navController,
                                    startDestination = "Location_Permission",
                                )
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                Log.e("MainActivity", "認証状態の確認に失敗しました", e)
                isLoading = false
                setContent {
                    ArchivemapsTheme(
                        darkTheme = isSystemInDarkTheme(),
                        dynamicColor = false,
                    ) {
                        val navController = rememberNavController()
                        AppNavHost(
                            navController = navController,
                            startDestination = "logIn",
                        )
                    }
                }
            }
        }
    }

    private fun hasLocationPermission(): Boolean =
        ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION,
        ) == PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION,
            ) == PackageManager.PERMISSION_GRANTED
}

@HiltAndroidApp
class MyApplication : Application()
