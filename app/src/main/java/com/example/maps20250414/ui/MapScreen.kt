package com.example.maps20250414.ui

import android.content.Intent
import android.net.Uri
import android.widget.VideoView
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import coil.compose.AsyncImage
import com.example.maps20250414.R
import com.example.maps20250414.model.LocationViewModel
import com.example.maps20250414.model.MarkerViewModel
import com.example.maps20250414.model.NamedMarker
import com.example.maps20250414.model.PermanentMarkerViewModel
import com.example.maps20250414.strage.saveMarkers
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.Tile
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update


class MapViewModel() : ViewModel() {
    private val _uiState = MutableStateFlow(MapsUiState())
    val uiState: StateFlow<MapsUiState> = _uiState

    fun changeIsFollowing() {
        _uiState.update { it.copy(isFollowing = !it.isFollowing) }
    }


    fun changeIsEditPanelOpen() {
        _uiState.update { it.copy(isEditPanelOpen = !it.isEditPanelOpen) }
    }

    fun changeIsPanelOpen() {
        _uiState.update { it.copy(isPanelOpen = !it.isFollowing) }
    }

    fun changeIsSearchOpen() {
        _uiState.update { it.copy(isSearchOpen = !it.isSearchOpen) }
    }

    fun changeTitleQuery(
        Answer: String
    ) {
        _uiState.update { it.copy(titleQuery = Answer) }
    }

    fun changeMemoQuery(
        Answer: String
    ) {
        _uiState.update { it.copy(memoQuery = Answer) }
    }


    fun changeSelectedMarker(
        updateMarker: NamedMarker?
    ) {
        _uiState.update { it.copy(selectedMarker = updateMarker) }
    }

    fun changeTempMarkerName(
        Answer: String?
    ) {
        _uiState.update { it.copy(tempMarkerName = Answer) }
    }

    fun changeTempMarkerPosition(
        Answer: LatLng?
    ) {
        _uiState.update { it.copy(tempMarkerPosition = Answer) }
    }


    fun UpdateSearchList(
        titleQuery: String?,
        memoQuery: String?,
        permanentMarkers: List<NamedMarker>,
    ) {
        val lowerTitle = titleQuery?.lowercase()
        val lowerMemo = memoQuery?.lowercase()

        _uiState.update { it.copy(titleResults = emptyList()) }
        _uiState.update { it.copy(memoResults = emptyList()) }


        if (!lowerTitle.isNullOrBlank()) {
            val Tilefiltered = permanentMarkers.filter {
                it.title.lowercase().contains(lowerTitle)
            }
            _uiState.update { it.copy(titleResults = Tilefiltered) }
        }
        if (!lowerMemo.isNullOrBlank()) {
            val Memofiltered = permanentMarkers.filter {
                it.title.lowercase().contains(lowerMemo)
            }
            _uiState.update { it.copy(memoResults = Memofiltered) }
        }



//        if (!lowerMemo.isNullOrBlank()) {
//            memoResults += permanentMarkers.filter {
//                it.memo?.lowercase()?.contains(lowerMemo) == true
//            }
//        }
    }

}


data class MapsUiState(
    val isPermissionGranted: Boolean = false,
    val isPanelOpen: Boolean = false,
    val tempMarkerName: String? = null,
    val selectedAddress: String? = null,
    val selectedMarker: NamedMarker? = null,
    val isEditPanelOpen: Boolean = false,
    val isFollowing: Boolean = false,
    val userLocation: LatLng? = null,
    val tempMarkerPosition: LatLng? = null,

    val isSearchOpen: Boolean = false,
    val titleQuery: String? = null,
    val memoQuery: String? = null,

    val titleResults: List<NamedMarker> = emptyList(),
    val memoResults: List<NamedMarker> = emptyList(),
    val visibleMarkers: List<NamedMarker> = emptyList(),

    )


//@SuppressLint("MissingPermission")
@Composable
fun MapScreen(
    viewModel: PermanentMarkerViewModel = hiltViewModel(),
    locationViewModel: LocationViewModel = hiltViewModel(),
    mapViewModel: MapViewModel = hiltViewModel()
) {

    val uiState by mapViewModel.uiState.collectAsState()


    val context = LocalContext.current
    //val fusedLocationClient = remember {
    //    LocationServices.getFusedLocationProviderClient(context)
    //}

    //val isFollowing by locationViewModel.isFollowing.collectAsState()


    var userLocation by remember { mutableStateOf<LatLng?>(null) }
    val cameraPositionState = rememberCameraPositionState()
    //var isFollowing by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        locationViewModel.startLocationUpdates(
            context = context,
            isFollowing = uiState.isFollowing,
            cameraPositionState = cameraPositionState,
            onLocationUpdate = { userLocation = it })
    }

    // タップされた位置の一時マーカー
    var tempMarkerPosition by remember { mutableStateOf<LatLng?>(null) }

    // 永続マーカーのリスト
    //val permanentMarkers = remember { mutableStateListOf<NamedMarker>() }
    val permanentMarkers = viewModel.permanentMarkers
    val markerViewModel: MarkerViewModel = hiltViewModel()

    LaunchedEffect(Unit) {
        //val loaded = loadMarkers(context)
        //permanentMarkers.addAll(loaded)

        viewModel.loadMarkers()
    }

    // サイドパネルの表示フラグ

    val focusManager = LocalFocusManager.current


    //// リアルタイム現在地取得
    //LaunchedEffect(Unit) {
    //    val locationRequest = LocationRequest.create().apply {
    //        interval = 3000
    //        fastestInterval = 2000
    //        priority = Priority.PRIORITY_HIGH_ACCURACY
    //    }
//
    //    val callback = object : LocationCallback() {
    //        override fun onLocationResult(result: LocationResult) {
    //            val location = result.lastLocation
    //            location?.let {
    //                val latLng = LatLng(it.latitude, it.longitude)
    //                userLocation = latLng
    //                if (isFollowing) {
    //                    cameraPositionState.move(CameraUpdateFactory.newLatLngZoom(latLng, 17f))
    //                }
    //            }
    //        }
    //    }
//
    //    fusedLocationClient.requestLocationUpdates(
    //        locationRequest,
    //        callback,
    //        context.mainLooper
    //    )
    //}

    //val visibleMarkers = remember { mutableStateListOf<NamedMarker>() }

    fun updateVisibleMarkers() {
        val bounds = cameraPositionState.projection?.visibleRegion?.latLngBounds
        if (bounds != null) {
            val filtered = permanentMarkers.filter { bounds.contains(it.position.toLatLng()) }
            //uiState.visibleMarkers.clear()
            //uiState.visibleMarkers.addAll(filtered)
            uiState.copy(visibleMarkers = filtered)
        }
    }

    val mediaPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri: Uri? ->
        uri?.let {
            context.contentResolver.takePersistableUriPermission(
                it, Intent.FLAG_GRANT_READ_URI_PERMISSION
            )

            val mimeType = context.contentResolver.getType(it)

            uiState.selectedMarker?.let { marker ->
                val index = permanentMarkers.indexOfFirst { it.id == marker.id }
                if (index != -1) {
                    val updatedMarker = when {
                        mimeType?.startsWith("image/") == true -> marker.copy(imageUri = it.toString())
                        mimeType?.startsWith("video/") == true -> marker.copy(videoUri = it.toString())
                        else -> marker // サポート外
                    }
                    viewModel.updateMarker(updatedMarker) // ViewModelで更新

                    mapViewModel.changeSelectedMarker(updatedMarker)

                    //selectedMarker = updatedMarker
                    updateVisibleMarkers()
                    //saveMarkers(context, permanentMarkers)
                }
            }
        }
    }

    LaunchedEffect(cameraPositionState.isMoving) {
        // マップ移動終了後に更新
        snapshotFlow { cameraPositionState.isMoving }.collect { isMoving ->
            if (!isMoving) {
                // カメラが止まったときに現在の可視領域を取得
                val bounds = cameraPositionState.projection?.visibleRegion?.latLngBounds
                if (bounds != null) {
                    val filtered =
                        permanentMarkers.filter { bounds.contains(it.position.toLatLng()) }
                    //visibleMarkers.clear()
                    //visibleMarkers.addAll(filtered)
                    uiState.copy(visibleMarkers = filtered)
                }
            }
        }
    }


    Box(modifier = Modifier.fillMaxSize()) {


        LaunchedEffect(uiState.titleQuery, uiState.memoQuery) {
            mapViewModel.UpdateSearchList(
                uiState.titleQuery,
                uiState.memoQuery,
                permanentMarkers,
            )
        }

        //val context2 = LocalContext.current


        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState,
            properties = MapProperties(
                isMyLocationEnabled = uiState.isPermissionGranted,
                mapStyleOptions = MapStyleOptions.loadRawResourceStyle(context, R.raw.map_style)
            ),
            onMapClick = { latLng ->
                tempMarkerPosition = latLng
                //uiState.isPanelOpen = true
                mapViewModel.changeIsPanelOpen()
            }) {

            // カメラの表示範囲にある永続マーカーのみ表示

            for (marker in visibleMarkers) {
                Marker(
                    state = MarkerState(position = marker.position.toLatLng()),
                    title = marker.title,
                    icon = BitmapDescriptorFactory.defaultMarker(marker.colorHue),
                    onClick = {
                        //selectedMarker = marker
                        mapViewModel.changeSelectedMarker(marker)
                        markerViewModel.fetchAddressForLatLng(
                            marker.position.latitude, marker.position.longitude
                        )
                        //isEditPanelOpen = true
                        mapViewModel.changeIsEditPanelOpen()
                        true // consume click
                    })
            }
            // 一時マーカー
            tempMarkerPosition?.let { temp ->
                Marker(
                    state = MarkerState(position = temp),
                    title = "一時マーカー",
                    icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE),
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
                //locationViewModel.toggleFollowing()
                mapViewModel.changeIsFollowing()
            }) {
                Text(if (uiState.isFollowing) "追従中" else "追従してないよ")
            }
        }

        if (uiState.isEditPanelOpen || uiState.isPanelOpen || uiState.isSearchOpen) {
            DismissOverlay()
        }

        // 検索ボタンとパネル
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.End
        ) {
            Button(onClick = {
                mapViewModel.changeIsSearchOpen()
                mapViewModel.changeTitleQuery("")
                mapViewModel.changeMemoQuery("")

            }) {
                Text(if (uiState.isSearchOpen) "閉じる" else "検索")
            }
        }

        // 検索パネル
        if (uiState.isSearchOpen) {
            SearchMaker(
                titleQuery = "",
                memoQuery = "",
                titleResults = emptyList(),
                memoResults = emptyList(),
                onMarkerNameChanged = { },
                onMemoNameChanged = {},
                onMarkerTapped = {},
                onMemoTapped = {}
            )
        }

        // 右側から出るパネル
        AnimatedVisibility(
            visible = uiState.isPanelOpen, modifier = Modifier.align(Alignment.CenterEnd)
        ) {
            SetMarkerPanel(
                tempMarkerName = null,
                cameraPositionState = cameraPositionState,
                focusManager = focusManager
            )
        }


        // 右側から出るパネル(編集用)
        AnimatedVisibility(
            visible = isEditPanelOpen && selectedMarker != null,
            modifier = Modifier.align(Alignment.CenterEnd)
        ) {
            Surface(
                tonalElevation = 4.dp, modifier = Modifier
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
                        var selectedColorHue by remember(marker) { mutableStateOf(marker.colorHue) }

                        Text("マーカーを編集")

                        Text(
                            text = "設置日時: ${marker.createdAt}",
                            modifier = Modifier.padding(vertical = 8.dp),
                            style = MaterialTheme.typography.bodyMedium
                        )

                        Text(text = "住所: $selectedAddress")


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
                                    }),
                                modifier = Modifier.weight(1f)
                            )

                            Spacer(modifier = Modifier.height(8.dp))
                            Button(
                                onClick = {

                                    focusManager.clearFocus() // ← 変換中なら確定

                                    selectedMarker?.let { marker ->
                                        val index =
                                            permanentMarkers.indexOfFirst { it.id == marker.id }
                                        if (index != -1) {
                                            val updatedMarker = marker.copy(
                                                title = editedName,
                                            )
                                            saveMarkers(context, permanentMarkers)
                                        }
                                        isEditPanelOpen = false
                                        selectedMarker = null
                                        updateVisibleMarkers()
                                    }
                                }, modifier = Modifier.wrapContentWidth()
                            ) {
                                Text("更新")
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))
                        Text("マーカーの色を変更", style = MaterialTheme.typography.bodyMedium)

                        val colorOptions = listOf(
                            BitmapDescriptorFactory.HUE_RED to "赤",
                            BitmapDescriptorFactory.HUE_BLUE to "青",
                            BitmapDescriptorFactory.HUE_GREEN to "緑",
                            BitmapDescriptorFactory.HUE_YELLOW to "黄"
                        )

                        Row(
                            horizontalArrangement = Arrangement.SpaceAround,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            colorOptions.forEach { (hue, label) ->
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    RadioButton(
                                        selected = selectedColorHue == hue, onClick = {
                                            selectedColorHue = hue

                                            // マーカーの色を即時変更
                                            selectedMarker?.let { marker ->
                                                val index =
                                                    permanentMarkers.indexOfFirst { it.id == marker.id }
                                                if (index != -1) {
                                                    val updatedMarker =
                                                        marker.copy(colorHue = hue)
                                                    //permanentMarkers[index] = updatedMarker
                                                    viewModel.updateMarker(updatedMarker)
                                                    selectedMarker = updatedMarker // UIも更新
                                                    updateVisibleMarkers()
                                                    //saveMarkers(context, permanentMarkers)
                                                }
                                            }
                                        })
                                    Text(label)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(24.dp))

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
                                }, modifier = Modifier
                                    .size(200.dp)
                                    .clip(RoundedCornerShape(8.dp))
                            )
                        }

                        if (selectedMarker?.imageUri != null || selectedMarker?.videoUri != null) {
                            Spacer(modifier = Modifier.height(16.dp))
                            Button(onClick = {
                                selectedMarker?.let { marker ->
                                    val index =
                                        permanentMarkers.indexOfFirst { it.id == marker.id }
                                    if (index != -1) {
                                        val updatedMarker = marker.copy(
                                            imageUri = null, videoUri = null
                                        )
                                        //permanentMarkers[index] = updatedMarker
                                        viewModel.updateMarker(updatedMarker)
                                        selectedMarker = updatedMarker
                                        updateVisibleMarkers()
                                        //saveMarkers(context, permanentMarkers)
                                    }
                                }
                            }) {
                                Text("メディアを削除")
                            }
                        }



                        Spacer(modifier = Modifier.height(16.dp))

                        Text("メモだよ", style = MaterialTheme.typography.bodyMedium)

                        OutlinedTextField(
                            value = memoText,
                            onValueChange = { newText ->
                                memoText = newText

                                selectedMarker?.let { marker ->
                                    val index =
                                        permanentMarkers.indexOfFirst { it.id == marker.id }
                                    if (index != -1) {
                                        val updatedMarker = marker.copy(memo = newText)
                                        //permanentMarkers[index] = updatedMarker
                                        viewModel.updateMarker(updatedMarker)
                                        selectedMarker = updatedMarker // UI更新
                                        updateVisibleMarkers()
                                        //saveMarkers(context, permanentMarkers)
                                    }
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(150.dp),
                            //.background(Color.White),
                            placeholder = { Text("ここにメモを書いてください") },
                            singleLine = false,
                            maxLines = 10,
                            keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Default),
                        )

                        Spacer(modifier = Modifier.height(16.dp))
                        Button(onClick = {
                            //permanentMarkers.removeIf { it.id == marker.id }
                            //saveMarkers(context, permanentMarkers)
                            viewModel.removeMarker(marker.id)

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



