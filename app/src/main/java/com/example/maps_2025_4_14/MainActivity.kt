package com.example.maps_2025_4_14

import android.Manifest
import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.maps_2025_4_14.ui.theme.Maps_2025_4_14Theme
import com.google.android.gms.location.*
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*
import java.util.UUID

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Maps_2025_4_14Theme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MapScreen()
                }
            }
        }
    }
}

@SuppressLint("MissingPermission")
@Composable
fun MapScreen() {
    val context = LocalContext.current
    val fusedLocationClient = remember {
        LocationServices.getFusedLocationProviderClient(context)
    }

    var userLocation by remember { mutableStateOf<LatLng?>(null) }
    val cameraPositionState = rememberCameraPositionState()
    var isFollowing by remember { mutableStateOf(true) }

    // タップされた位置の一時マーカー
    var tempMarkerPosition by remember { mutableStateOf<LatLng?>(null) }

    // 永続マーカーのリスト
    val permanentMarkers = remember { mutableStateListOf<NamedMarker>() }

    // サイドパネルの表示フラグ
    var isPanelOpen by remember { mutableStateOf(false) }

    var tempMarkerName by remember { mutableStateOf("") }

    var selectedMarker by remember { mutableStateOf<NamedMarker?>(null) }
    var isEditPanelOpen by remember { mutableStateOf(false) }

    // リアルタイム現在地取得
    LaunchedEffect(Unit) {
        val locationRequest = LocationRequest.create().apply {
            interval = 3000
            fastestInterval = 2000
            priority = Priority.PRIORITY_HIGH_ACCURACY
        }

        val callback = object : LocationCallback() {
            override fun onLocationResult(result: LocationResult) {
                val location = result.lastLocation
                location?.let {
                    val latLng = LatLng(it.latitude, it.longitude)
                    userLocation = latLng
                    if (isFollowing) {
                        cameraPositionState.move(CameraUpdateFactory.newLatLngZoom(latLng, 17f))
                    }
                }
            }
        }

        fusedLocationClient.requestLocationUpdates(
            locationRequest,
            callback,
            context.mainLooper
        )
    }

    Box(modifier = Modifier.fillMaxSize()) {
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState,
            properties = MapProperties(isMyLocationEnabled = true),
            onMapClick = { latLng ->
                tempMarkerPosition = latLng
                isPanelOpen = true
            }
        ) {
            // 現在位置のマーカー
            userLocation?.let { location ->
                Marker(
                    state = MarkerState(position = location),
                    title = "あなたの位置"
                )
            }

            // 永続マーカー表示
            for (marker in permanentMarkers) {
                Marker(
                    state = MarkerState(position = marker.position),
                    title = marker.title,
                    onClick = {
                        selectedMarker = marker
                        isEditPanelOpen = true
                        true // consume click
                    }
                )
            }

            // 一時マーカー
            tempMarkerPosition?.let { temp ->
                Marker(
                    state = MarkerState(position = temp),
                    title = "一時マーカー",
                    draggable = false
                )
            }
        }

        // 右下の追従モードボタン
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Bottom,
            horizontalAlignment = Alignment.End
        ) {
            Button(onClick = {
                isFollowing = !isFollowing
            }) {
                Text(if (isFollowing) "追従ON" else "追従OFF")
            }
        }


        // 右側から出るパネル
        AnimatedVisibility(
            visible = isPanelOpen,
            modifier = Modifier.align(Alignment.CenterEnd)
        ) {
            Surface(
                tonalElevation = 4.dp,
                modifier = Modifier
                    .width(300.dp)
                    .fillMaxHeight()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("マーカー名を入力してください")
                    OutlinedTextField(
                        value = tempMarkerName,
                        onValueChange = { tempMarkerName = it },
                        label = { Text("マーカー名") }
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(onClick = {
                        tempMarkerPosition?.let { pos ->
                            permanentMarkers.add(NamedMarker(position = pos, title = tempMarkerName))
                        }
                        tempMarkerPosition = null
                        tempMarkerName = ""
                        isPanelOpen = false
                    }) {
                        Text("マーカーを設置する")
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(onClick = {
                        tempMarkerPosition = null
                        tempMarkerName = ""
                        isPanelOpen = false
                    }) {
                        Text("キャンセル")
                    }
                }
            }
        }

        // 右側から出るパネル(編集用)
        AnimatedVisibility(
            visible = isEditPanelOpen && selectedMarker != null,
            modifier = Modifier.align(Alignment.CenterEnd)
        ) {
            Surface(
                tonalElevation = 4.dp,
                modifier = Modifier
                    .width(300.dp)
                    .fillMaxHeight()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    selectedMarker?.let { marker ->
                        var editedName by remember(marker) { mutableStateOf(marker.title) }

                    Text("マーカーを編集")
                    OutlinedTextField(
                        value = editedName,
                        onValueChange = { editedName = it },
                        label = { Text("マーカー名") }
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(onClick = {
                        selectedMarker?.let { marker ->
                            val index = permanentMarkers.indexOfFirst { it.id == marker.id }
                            if (index != -1) {
                                permanentMarkers[index] = marker.copy(title = editedName)
                            }
                            isEditPanelOpen = false
                            selectedMarker = null
                        }
                    })                    {
                        Text("名前を更新")
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(onClick = {
                        permanentMarkers.remove(marker)
                        isEditPanelOpen = false
                        selectedMarker = null
                    }) {
                        Text("削除する")
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(onClick = {
                        isEditPanelOpen = false
                        selectedMarker = null
                    }) {
                        Text("キャンセル")
                    }
                    }
                }
            }
        }


    }
}

data class NamedMarker(
    val id: String = UUID.randomUUID().toString(), // 識別子
    val position: LatLng,
    val title: String
)
