package com.mKanta.archivemaps.ui.screen.map

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
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.net.toUri
import coil.compose.AsyncImage
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.mKanta.archivemaps.domain.model.LatLngSerializable
import com.mKanta.archivemaps.domain.model.NamedMarker
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

@Composable
fun EditPanel(
    selectedMarker: NamedMarker?,
    onMarkerUpdate: (NamedMarker) -> Unit,
    onMarkerDelete: (NamedMarker) -> Unit,
    onPanelClose: () -> Unit,
    permanentMarkers: List<NamedMarker>,
    mapsSaveMarker: () -> Unit,
    focusManager: FocusManager,
    context: Context,
    selectedAddress: StateFlow<String>,
) {
    val address by selectedAddress.collectAsState()
    val mediaPickerLauncher =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.OpenDocument(),
        ) { uri: Uri? ->
            uri?.let {
                context.contentResolver.takePersistableUriPermission(
                    it,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION,
                )

                val mimeType = context.contentResolver.getType(it)

                // uiState.selectedMarker
                selectedMarker?.let { marker ->
                    // val index = permanentMarkers.indexOfFirst { it.id == marker.id }
                    val index =
                        permanentMarkers.indexOfFirst { markerItem -> markerItem.id == marker.id }
                    if (index != -1) {
//                    onMediaPicked(marker, uri, mimeType)
                        val updatedMarker =
                            when {
                                mimeType?.startsWith("image/") == true -> marker.copy(imageUri = it.toString())
                                mimeType?.startsWith("video/") == true -> marker.copy(videoUri = it.toString())
                                else -> marker // サポート外
                            }

                        onMarkerUpdate(updatedMarker)
                    }
                }
            }
        }

    Surface(
        tonalElevation = 4.dp,
        modifier =
            Modifier
                .width(300.dp)
                .fillMaxHeight(),
    ) {
        val scrollState = rememberScrollState()

        Column(
            modifier =
                Modifier
                    .padding(16.dp)
                    .verticalScroll(scrollState),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            // uiState.selectedMarker
            selectedMarker?.let { marker ->
                var editedName by remember(marker) { mutableStateOf(marker.title) }
                var memoText by remember(marker) { mutableStateOf(marker.memo ?: "") }
                var selectedColorHue by remember(marker) { mutableFloatStateOf(marker.colorHue) }

                Text("マーカーを編集")

                Text(
                    text = "設置日時: ${marker.createdAt}",
                    modifier = Modifier.padding(vertical = 8.dp),
                    style = MaterialTheme.typography.bodyMedium,
                )

                Text(
                    text = "住所: ${address.ifBlank { "住所が取得できませんでした" }}",
                    // text = "住所: ${uiState.selectedAddress?.ifBlank { "住所が取得できませんでした" }}"
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    OutlinedTextField(
                        value = editedName,
                        onValueChange = { editedName = it },
                        label = { Text("マーカー名") },
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                        keyboardActions =
                            KeyboardActions(
                                onDone = {
                                    focusManager.clearFocus()
                                },
                            ),
                        modifier = Modifier.weight(1f),
                    )

                    Spacer(modifier = Modifier.height(8.dp))
                    Button(
                        onClick = {
                            focusManager.clearFocus() // ← 変換中なら確定

                            // uiState.selectedMarker
                            selectedMarker.let { marker ->
                                val index =
                                    permanentMarkers.indexOfFirst { it.id == marker.id }
                                if (index != -1) {
                                    val updatedMarker =
                                        marker.copy(
                                            title = editedName,
                                        )
                                    onMarkerUpdate(updatedMarker)
                                    // markerRepository.saveMarkers(permanentMarkers)
                                    mapsSaveMarker()
                                }
                                onPanelClose()
                            }
                        },
                        modifier = Modifier.wrapContentWidth(),
                    ) {
                        Text("更新")
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
                Text("マーカーの色を変更", style = MaterialTheme.typography.bodyMedium)

                val colorOptions =
                    listOf(
                        BitmapDescriptorFactory.HUE_RED to "赤",
                        BitmapDescriptorFactory.HUE_BLUE to "青",
                        BitmapDescriptorFactory.HUE_GREEN to "緑",
                        BitmapDescriptorFactory.HUE_YELLOW to "黄",
                    )

                Row(
                    horizontalArrangement = Arrangement.SpaceAround,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    colorOptions.forEach { (hue, label) ->
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            RadioButton(
                                selected = selectedColorHue == hue,
                                onClick = {
                                    selectedColorHue = hue

                                    // マーカーの色を即時変更
                                    // uiState.selectedMarker
                                    selectedMarker.let { marker ->
                                        val index =
                                            permanentMarkers.indexOfFirst { it.id == marker.id }
                                        if (index != -1) {
                                            val updatedMarker =
                                                marker.copy(colorHue = hue)
                                            // permanentMarkers[index] = updatedMarker
//                                            viewModel.updateMarker(updatedMarker)
//                                            //selectedMarker = updatedMarker // UIも更新
//                                            mapViewModel.changeSelectedMarker(updatedMarker)
//                                            mapViewModel.updateVisibleMarkers(
//                                                cameraPositionState,
//                                                permanentMarkers
//                                            )
                                            onMarkerUpdate(updatedMarker)
                                            // saveMarkers(context, permanentMarkers)
                                        }
                                    }
                                },
                            )
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
                        modifier =
                            Modifier
                                .size(200.dp)
                                .clip(RoundedCornerShape(8.dp)),
                    )
                }

                // uiState.selectedMarker
                selectedMarker.videoUri?.let { videoUri ->
                    AndroidView(
                        factory = {
                            VideoView(it).apply {
                                setVideoURI(videoUri.toUri())
                                setOnPreparedListener { mediaPlayer ->
                                    mediaPlayer.isLooping = true
                                    start()
                                }
                            }
                        },
                        modifier =
                            Modifier
                                .size(200.dp)
                                .clip(RoundedCornerShape(8.dp)),
                    )
                }

                if (selectedMarker.imageUri != null || selectedMarker.videoUri != null) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(onClick = {
                        // uiState.selectedMarker
                        selectedMarker.let { marker ->
                            val index =
                                permanentMarkers.indexOfFirst { it.id == marker.id }
                            if (index != -1) {
                                // onMediaDelete(marker)
                                val updatedMarker =
                                    marker.copy(
                                        imageUri = null,
                                        videoUri = null,
                                    )
                                // permanentMarkers[index] = updatedMarker
                                onMarkerUpdate(updatedMarker)
//                                viewModel.updateMarker(updatedMarker)
//                                //selectedMarker = updatedMarker
//                                mapViewModel.changeSelectedMarker(updatedMarker)
//                                mapViewModel.updateVisibleMarkers(
//                                    cameraPositionState,
//                                    permanentMarkers
//                                )

                                // saveMarkers(context, permanentMarkers)
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

                        // uiState.selectedMarker
                        selectedMarker.let { marker ->
                            val index =
                                permanentMarkers.indexOfFirst { it.id == marker.id }
                            if (index != -1) {
                                val updatedMarker = marker.copy(memo = newText)
                                onMarkerUpdate(updatedMarker)
                            }
                        }
                    },
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .height(150.dp),
                    placeholder = { Text("ここにメモを書いてください") },
                    singleLine = false,
                    maxLines = 10,
                    keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Default),
                )

                Spacer(modifier = Modifier.height(16.dp))
                Button(onClick = {
                    onMarkerDelete(marker)
                }) {
                    Text("削除する")
                }
                Spacer(modifier = Modifier.height(16.dp))
                Button(onClick = {
                    // isEditPanelOpen = false
                    // selectedMarker = null
                    onPanelClose()
//                    mapViewModel.changeIsEditPanelOpen()
//                    mapViewModel.changeSelectedMarker(null)
                }) {
                    Text("戻る")
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun EditPanelPreview() {
    val dummyMarker =
        NamedMarker(
            id = "1",
            title = "サンプル",
            memo = "メモの例",
            colorHue = BitmapDescriptorFactory.HUE_RED,
            position = LatLngSerializable(35.0, 139.0),
            createdAt = "2025-04-21",
        )

    val dummyAddress: StateFlow<String> = MutableStateFlow("東京都千代田区永田町")

    EditPanel(
        // uiState = dummyUiState,
        selectedMarker = dummyMarker,
        onMarkerUpdate = {},
        onMarkerDelete = {},
        onPanelClose = {},
        permanentMarkers = listOf(dummyMarker),
        focusManager = LocalFocusManager.current,
        context = LocalContext.current,
        selectedAddress = dummyAddress,
        mapsSaveMarker = {},
    )
}
