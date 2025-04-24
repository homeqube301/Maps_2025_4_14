package com.example.maps20250414

import android.Manifest
import android.app.Application
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.compose.rememberNavController
import com.example.maps20250414.navigation.NiaNavHost
import com.example.maps20250414.ui.theme.Maps20250414Theme
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.HiltAndroidApp

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val LOCATION_PERMISSION_REQUEST_CODE = 1001
    private var isPermissionGranted = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        isPermissionGranted = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        if (!isPermissionGranted) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE
            )
        }

        setContent {
            Maps20250414Theme {
                val navController = rememberNavController()
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    // ✅ パーミッション状態を渡す
                    //MapScreen(isPermissionGranted = isPermissionGranted)
                    NiaNavHost(navController = navController)
                }
            }
        }
    }

    // 🔄 権限の結果が返ってきたときに画面を再構成する必要あり
    @Deprecated("Deprecated in Java", ReplaceWith(
        "super.onRequestPermissionsResult(requestCode, permissions, grantResults)",
        "androidx.activity.ComponentActivity"
    )
    )
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }
}

@HiltAndroidApp
class MyApplication : Application()

