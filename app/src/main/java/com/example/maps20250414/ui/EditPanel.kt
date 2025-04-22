package com.example.maps20250414.ui

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.VideoView
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.example.maps20250414.model.NamedMarker
import com.example.maps20250414.model.PermanentMarkerViewModel
import com.example.maps20250414.strage.saveMarkers
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.maps.android.compose.CameraPositionState

@Composable
fun EditPanel(
    mapViewModel: MapViewModel = hiltViewModel(),
    viewModel: PermanentMarkerViewModel = hiltViewModel(),
    permanentMarkers: List<NamedMarker>,
    focusManager: FocusManager,
    context: Context,
    cameraPositionState: CameraPositionState
) {
    val uiState by mapViewModel.uiState.collectAsState()

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
                    mapViewModel.updateVisibleMarkers(
                        cameraPositionState,
                        permanentMarkers
                    )
                    //saveMarkers(context, permanentMarkers)
                }
            }
        }
    }


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

            uiState.selectedMarker?.let { marker ->
                var editedName by remember(marker) { mutableStateOf(marker.title) }
                var memoText by remember(marker) { mutableStateOf(marker.memo ?: "") }
                var selectedColorHue by remember(marker) { mutableStateOf(marker.colorHue) }

                Text("マーカーを編集")

                Text(
                    text = "設置日時: ${marker.createdAt}",
                    modifier = Modifier.padding(vertical = 8.dp),
                    style = MaterialTheme.typography.bodyMedium
                )

                Text(text = "住所: $uiState.selectedAddress")


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

                            uiState.selectedMarker?.let { marker ->
                                val index =
                                    permanentMarkers.indexOfFirst { it.id == marker.id }
                                if (index != -1) {
                                    val updatedMarker = marker.copy(
                                        title = editedName,
                                    )
                                    saveMarkers(context, permanentMarkers)
                                }
                                //isEditPanelOpen = false
                                //selectedMarker = null
                                mapViewModel.updateVisibleMarkers(
                                    cameraPositionState,
                                    permanentMarkers
                                )
                                mapViewModel.changeIsEditPanelOpen()
                                mapViewModel.changeSelectedMarker(null)

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
                                    uiState.selectedMarker?.let { marker ->
                                        val index =
                                            permanentMarkers.indexOfFirst { it.id == marker.id }
                                        if (index != -1) {
                                            val updatedMarker =
                                                marker.copy(colorHue = hue)
                                            //permanentMarkers[index] = updatedMarker
                                            viewModel.updateMarker(updatedMarker)
                                            //selectedMarker = updatedMarker // UIも更新
                                            mapViewModel.changeSelectedMarker(updatedMarker)
                                            mapViewModel.updateVisibleMarkers(
                                                cameraPositionState,
                                                permanentMarkers
                                            )
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


                uiState.selectedMarker?.videoUri?.let { videoUri ->
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

                if (uiState.selectedMarker?.imageUri != null || uiState.selectedMarker?.videoUri != null) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(onClick = {
                        uiState.selectedMarker?.let { marker ->
                            val index =
                                permanentMarkers.indexOfFirst { it.id == marker.id }
                            if (index != -1) {
                                val updatedMarker = marker.copy(
                                    imageUri = null, videoUri = null
                                )
                                //permanentMarkers[index] = updatedMarker
                                viewModel.updateMarker(updatedMarker)
                                //selectedMarker = updatedMarker
                                mapViewModel.changeSelectedMarker(updatedMarker)
                                mapViewModel.updateVisibleMarkers(
                                    cameraPositionState,
                                    permanentMarkers
                                )
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

                        uiState.selectedMarker?.let { marker ->
                            val index =
                                permanentMarkers.indexOfFirst { it.id == marker.id }
                            if (index != -1) {
                                val updatedMarker = marker.copy(memo = newText)
                                //permanentMarkers[index] = updatedMarker
                                viewModel.updateMarker(updatedMarker)
                                //selectedMarker = updatedMarker // UI更新
                                mapViewModel.changeSelectedMarker(updatedMarker)
                                mapViewModel.updateVisibleMarkers(
                                    cameraPositionState,
                                    permanentMarkers
                                )
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

                    //visibleMarkers.remove(marker)
                    mapViewModel.removeVisibleMarkers(marker)
                    mapViewModel.changeIsEditPanelOpen()
                    mapViewModel.changeSelectedMarker(null)
                    //isEditPanelOpen = false
                    //selectedMarker = null
                }) {
                    Text("削除する")
                }
                Spacer(modifier = Modifier.height(16.dp))
                Button(onClick = {
                    //isEditPanelOpen = false
                    //selectedMarker = null
                    mapViewModel.changeIsEditPanelOpen()
                    mapViewModel.changeSelectedMarker(null)
                }) {
                    Text("戻る")
                }
            }
        }
    }
}