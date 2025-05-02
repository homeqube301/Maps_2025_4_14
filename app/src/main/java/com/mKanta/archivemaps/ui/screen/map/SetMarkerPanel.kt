package com.mKanta.archivemaps.ui.screen.map

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.CameraPositionState
import com.google.maps.android.compose.rememberCameraPositionState
import com.mKanta.archivemaps.domain.model.LatLngSerializable
import com.mKanta.archivemaps.domain.model.NamedMarker
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Composable
fun SetMarkerPanel(
    cameraPositionState: CameraPositionState,
    focusManager: FocusManager,
    tempMarkerName: String?,
    tempMarkerPosition: LatLng?,
    onClose: () -> Unit,
    resetTempMarkers: () -> Unit,
    changeTempMarkerName: (String) -> Unit,
    addVisibleMarker: (NamedMarker) -> Unit,
    addMarker: (NamedMarker) -> Unit,
) {
    Column(
        modifier =
            Modifier
                .padding(16.dp)
                .fillMaxWidth(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text("マーカー名を入力してください")
        OutlinedTextField(
            value = tempMarkerName ?: "",
            onValueChange = {
                changeTempMarkerName(it)
            },
            label = { Text("マーカー名") },
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
            keyboardActions =
                KeyboardActions(
                    onDone = {
                        focusManager.clearFocus()
                    },
                ),
        )
        Spacer(modifier = Modifier.height(16.dp))

        var selectedHue by remember { mutableFloatStateOf(BitmapDescriptorFactory.HUE_RED) }

        Text("マーカーの色を選んでください")
        Row(
            horizontalArrangement = Arrangement.SpaceAround,
            modifier = Modifier.fillMaxWidth(),
        ) {
            val colorOptions =
                listOf(
                    BitmapDescriptorFactory.HUE_RED to "赤",
                    BitmapDescriptorFactory.HUE_BLUE to "青",
                    BitmapDescriptorFactory.HUE_GREEN to "緑",
                    BitmapDescriptorFactory.HUE_YELLOW to "黄",
                )
            colorOptions.forEach { (hue, label) ->
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    RadioButton(
                        selected = selectedHue == hue,
                        onClick = { selectedHue = hue },
                    )
                    Text(label)
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = {
            focusManager.clearFocus() // ← 変換中なら確定

            tempMarkerPosition?.let { pos ->
                val newMarker =
                    NamedMarker(
                        position = LatLngSerializable.from(pos),
                        title = tempMarkerName ?: "",
                        createdAt =
                            LocalDateTime.now().format(
                                DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss"),
                            ),
                        colorHue = selectedHue,
                    )
                addMarker(newMarker)
                val bounds =
                    cameraPositionState.projection?.visibleRegion?.latLngBounds
                if (bounds != null && bounds.contains(pos)) {
                    addVisibleMarker(newMarker)
                }
            }
            resetTempMarkers()
        }) {
            Text("マーカーを設置する")
        }
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = {
            resetTempMarkers()
            onClose()
        }) {
            Text("閉じる")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewSetMarkerPanel() {
    val dummyCameraPositionState =
        rememberCameraPositionState {
            position = CameraPosition.fromLatLngZoom(LatLng(35.681236, 139.767125), 10f)
        }

    val dummyFocusManager = LocalFocusManager.current

    SetMarkerPanel(
        cameraPositionState = dummyCameraPositionState,
        focusManager = dummyFocusManager,
        tempMarkerName = "サンプルマーカー",
        tempMarkerPosition = LatLng(35.681236, 139.767125),
        onClose = {},
        resetTempMarkers = {},
        changeTempMarkerName = {},
        addVisibleMarker = {},
        addMarker = {},
    )
}
