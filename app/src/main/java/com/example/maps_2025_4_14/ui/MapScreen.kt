package com.example.maps_2025_4_14.ui

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.widget.VideoView
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import coil.compose.AsyncImage
import com.example.maps_2025_4_14.model.LatLngSerializable
import com.example.maps_2025_4_14.model.NamedMarker
import com.example.maps_2025_4_14.strage.loadMarkers
import com.example.maps_2025_4_14.strage.saveMarkers

import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter



@SuppressLint("MissingPermission")
@Composable
fun MapScreen(isPermissionGranted: Boolean) {

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

    LaunchedEffect(Unit) {
        val loaded = loadMarkers(context)
        permanentMarkers.addAll(loaded)
    }

    // サイドパネルの表示フラグ
    var isPanelOpen by remember { mutableStateOf(false) }

    var tempMarkerName by remember { mutableStateOf("") }

    var selectedMarker by remember { mutableStateOf<NamedMarker?>(null) }
    var isEditPanelOpen by remember { mutableStateOf(false) }

    val focusManager = LocalFocusManager.current




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

    val visibleMarkers = remember { mutableStateListOf<NamedMarker>() }

    fun updateVisibleMarkers() {
        val bounds = cameraPositionState.projection?.visibleRegion?.latLngBounds
        if (bounds != null) {
            val filtered = permanentMarkers.filter { bounds.contains(it.position.toLatLng()) }
            visibleMarkers.clear()
            visibleMarkers.addAll(filtered)
        }
    }

    val mediaPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri: Uri? ->
        uri?.let {
            context.contentResolver.takePersistableUriPermission(
                it,
                Intent.FLAG_GRANT_READ_URI_PERMISSION
            )

            val mimeType = context.contentResolver.getType(it)

            selectedMarker?.let { marker ->
                val index = permanentMarkers.indexOfFirst { it.id == marker.id }
                if (index != -1) {
                    val updatedMarker = when {
                        mimeType?.startsWith("image/") == true -> marker.copy(imageUri = it.toString())
                        mimeType?.startsWith("video/") == true -> marker.copy(videoUri = it.toString())
                        else -> marker // サポート外
                    }
                    permanentMarkers[index] = updatedMarker
                    selectedMarker = updatedMarker
                    updateVisibleMarkers()
                    saveMarkers(context, permanentMarkers)
                }
            }
        }
    }

    LaunchedEffect(cameraPositionState.isMoving) {
        // マップ移動終了後に更新
        snapshotFlow { cameraPositionState.isMoving }
            .collect { isMoving ->
                if (!isMoving) {
                    // カメラが止まったときに現在の可視領域を取得
                    val bounds = cameraPositionState.projection?.visibleRegion?.latLngBounds
                    if (bounds != null) {
                        val filtered = permanentMarkers.filter { bounds.contains(it.position.toLatLng()) }
                        visibleMarkers.clear()
                        visibleMarkers.addAll(filtered)
                    }
                }
            }
    }


    Box(modifier = Modifier.fillMaxSize()) {
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState,
            properties = MapProperties(isMyLocationEnabled = isPermissionGranted),
            onMapClick = { latLng ->
                tempMarkerPosition = latLng
                isPanelOpen = true
            }
        ) {

            // カメラの表示範囲にある永続マーカーのみ表示

            for (marker in visibleMarkers) {
                Marker(
                    state = MarkerState(position = marker.position.toLatLng()),
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

        if (isEditPanelOpen || isPanelOpen) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.001f)) // ほぼ透明
                    .clickable(
                        indication = null,
                        interactionSource = remember { MutableInteractionSource() }
                    ) {
                        isEditPanelOpen = false
                        isPanelOpen = false
                        selectedMarker = null
                    }
            )
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
                        label = { Text("マーカー名") },
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                        keyboardActions = KeyboardActions(
                            onDone = {
                                focusManager.clearFocus() // ← IME入力を確定
                            }
                        )
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(onClick = {

                        focusManager.clearFocus() // ← 変換中なら確定

                        tempMarkerPosition?.let { pos ->
                            val newMarker = NamedMarker(
                                position = LatLngSerializable.from(pos),
                                title = tempMarkerName,
                                createdAt = LocalDateTime.now().format(
                                    DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss")
                                )
                            )
                            permanentMarkers.add(newMarker)
                            saveMarkers(context, permanentMarkers)

                            // 👇 表示範囲に入っていれば visibleMarkers にも追加！
                            val bounds = cameraPositionState.projection?.visibleRegion?.latLngBounds
                            if (bounds != null && bounds.contains(pos)) {
                                visibleMarkers.add(newMarker)
                            }
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

                val scrollState = rememberScrollState()

                Column(
                    modifier = Modifier
                        .padding(16.dp)
                        .verticalScroll(scrollState),
                verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    selectedMarker?.let { marker ->
                        var editedName by remember(marker) { mutableStateOf(marker.title) }
                        var memoText by remember(marker) { mutableStateOf(marker.memo ?: "") }

                        Text("マーカーを編集")

                        Text(
                            text = "設置日時: ${marker.createdAt}",
                            modifier = Modifier.padding(vertical = 8.dp),
                            style = MaterialTheme.typography.bodyMedium
                        )


                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {

                            OutlinedTextField(
                                value = editedName,
                                onValueChange = { editedName = it },
                                label = { Text("マーカー名") },
                                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                                keyboardActions = KeyboardActions(
                                    onDone = {
                                        focusManager.clearFocus()
                                    }
                                ),
                                modifier = Modifier.weight(1f)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Button(onClick = {

                                focusManager.clearFocus() // ← 変換中なら確定

                                selectedMarker?.let { marker ->
                                    val index = permanentMarkers.indexOfFirst { it.id == marker.id }
                                    if (index != -1) {
                                        permanentMarkers[index] = marker.copy(
                                            title = editedName,
                                        )
                                        saveMarkers(context, permanentMarkers)
                                    }
                                    isEditPanelOpen = false
                                    selectedMarker = null
                                    updateVisibleMarkers()
                                }
                            },modifier = Modifier.wrapContentWidth()
                            ) {
                                Text("更新")
                            }
                        }

                        Button(onClick = {
                            mediaPickerLauncher.launch(arrayOf("image/*", "video/*"))
                        }) {
                            Text("メディアを追加")
                        }

                        marker.imageUri?.let { uri ->
                            Spacer(modifier = Modifier.height(16.dp))
                            AsyncImage(
                                model = uri,
                                contentDescription = "マーカー画像",
                                modifier = Modifier
                                    .size(200.dp)
                                    .clip(RoundedCornerShape(8.dp))
                            )

                        }


                        selectedMarker?.videoUri?.let { videoUri ->
                            AndroidView(
                                factory = {
                                    VideoView(it).apply {
                                        setVideoURI(Uri.parse(videoUri))
                                        setOnPreparedListener { mediaPlayer ->
                                            mediaPlayer.isLooping = true
                                            start()
                                        }
                                    }
                                },
                                modifier = Modifier
                                    .size(200.dp)
                                    .clip(RoundedCornerShape(8.dp))
                            )
                        }

                        if (selectedMarker?.imageUri != null || selectedMarker?.videoUri != null) {
                            Spacer(modifier = Modifier.height(16.dp))
                            Button(onClick = {
                                selectedMarker?.let { marker ->
                                    val index = permanentMarkers.indexOfFirst { it.id == marker.id }
                                    if (index != -1) {
                                        val updatedMarker = marker.copy(
                                            imageUri = null,
                                            videoUri = null
                                        )
                                        permanentMarkers[index] = updatedMarker
                                        selectedMarker = updatedMarker
                                        updateVisibleMarkers()
                                        saveMarkers(context, permanentMarkers)
                                    }
                                }
                            }) {
                                Text("メディアを削除")
                            }
                        }



                        Spacer(modifier = Modifier.height(16.dp))

                        Text("メモ（任意）", style = MaterialTheme.typography.bodyMedium)

                        OutlinedTextField(
                            value = memoText,
                            onValueChange = { newText ->
                                memoText = newText

                                selectedMarker?.let { marker ->
                                    val index = permanentMarkers.indexOfFirst { it.id == marker.id }
                                    if (index != -1) {
                                        val updatedMarker = marker.copy(memo = newText)
                                        permanentMarkers[index] = updatedMarker
                                        selectedMarker = updatedMarker // UI更新
                                        updateVisibleMarkers()
                                        saveMarkers(context, permanentMarkers)
                                    }
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(150.dp)
                                .background(Color.White),
                            placeholder = { Text("ここにメモを書いてください") },
                            singleLine = false,
                            maxLines = 10,
                            keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Default),
                        )

                        Spacer(modifier = Modifier.height(16.dp))
                        Button(onClick = {
                            permanentMarkers.removeIf { it.id == marker.id }
                            saveMarkers(context, permanentMarkers)

                            visibleMarkers.remove(marker)
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
                            Text("戻る")
                        }
                    }
                }
            }
        }


    }
}
