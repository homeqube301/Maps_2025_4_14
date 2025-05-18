package com.mKanta.archivemaps.ui.screen.map

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
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
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.CameraPositionState
import com.google.maps.android.compose.rememberCameraPositionState
import com.mKanta.archivemaps.R
import com.mKanta.archivemaps.domain.model.LatLngSerializable
import com.mKanta.archivemaps.domain.model.NamedMarker
import com.mKanta.archivemaps.ui.theme.ArchivemapsTheme
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Composable
fun SetMarkerPanel(
    cameraPositionState: CameraPositionState,
    focusManager: FocusManager,
    tempMarkerName: String?,
    tempMarkerPosition: LatLng?,
    resetTempMarkers: () -> Unit,
    changeTempMarkerName: (String) -> Unit,
    addVisibleMarker: (NamedMarker) -> Unit,
    addMarker: (NamedMarker) -> Unit,
    changeShowConfirmDialog: () -> Unit,
    changeTempMarkerMemo: (String) -> Unit,
    tempMarkerMemo: String?,
    memoEmbedding: (NamedMarker, String) -> Unit,
) {
    ArchivemapsTheme {
        BackHandler(enabled = true) {
            changeShowConfirmDialog()
        }

        Column(
            modifier =
                Modifier
                    .background(MaterialTheme.colorScheme.background)
                    .padding(16.dp)
                    .fillMaxWidth(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            SetMarkerName(
                tempMarkerName = tempMarkerName,
                changeTempMarkerName = changeTempMarkerName,
                focusManager = focusManager,
            )

            Spacer(modifier = Modifier.height(24.dp))

            var selectedHue by remember { mutableFloatStateOf(BitmapDescriptorFactory.HUE_RED) }

            SetMarkerColor(
                selectedHue = selectedHue,
                onHueSelected = { selectedHue = it },
            )

            Spacer(modifier = Modifier.height(24.dp))

            SetMarkerMemo(
                tempMarkerMemo = tempMarkerMemo,
                changeTempMarkerMemo = changeTempMarkerMemo,
            )

            Spacer(modifier = Modifier.height(24.dp))

            SetMarker(
                cameraPositionState = cameraPositionState,
                focusManager = focusManager,
                tempMarkerName = tempMarkerName,
                tempMarkerMemo = tempMarkerMemo,
                tempMarkerPosition = tempMarkerPosition,
                resetTempMarkers = resetTempMarkers,
                addVisibleMarker = addVisibleMarker,
                addMarker = addMarker,
                selectedHue = selectedHue,
                changeShowConfirmDialog = changeShowConfirmDialog,
                memoEmbedding = memoEmbedding,
            )
        }
    }
}

@Composable
private fun SetMarker(
    cameraPositionState: CameraPositionState,
    focusManager: FocusManager,
    tempMarkerName: String?,
    tempMarkerMemo: String?,
    tempMarkerPosition: LatLng?,
    resetTempMarkers: () -> Unit,
    addVisibleMarker: (NamedMarker) -> Unit,
    addMarker: (NamedMarker) -> Unit,
    selectedHue: Float,
    changeShowConfirmDialog: () -> Unit,
    memoEmbedding: (NamedMarker, String) -> Unit,
) {
    val context = LocalContext.current

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Button(
            modifier = Modifier.fillMaxWidth(0.5f),
            onClick = {
                focusManager.clearFocus()
                tempMarkerPosition?.let { pos ->
                    val newMarker =
                        NamedMarker(
                            position = LatLngSerializable.from(pos),
                            title = if (tempMarkerName.isNullOrBlank()) context.getString(R.string.setMarker_noName) else tempMarkerName,
                            memo = tempMarkerMemo,
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
                    memoEmbedding(newMarker, tempMarkerMemo.toString())
                }
                resetTempMarkers()
            },
        ) {
            Text(stringResource(id = R.string.setMarker_set_Button), fontWeight = FontWeight.Bold)
        }
        Spacer(modifier = Modifier.height(8.dp))
        Button(
            colors = ButtonDefaults.buttonColors(Color.Gray),
            modifier = Modifier.fillMaxWidth(0.5f),
            onClick = {
                changeShowConfirmDialog()
            },
        ) {
            Text(
                stringResource(id = R.string.setMarker_cancel_Button),
                fontWeight = FontWeight.Bold,
            )
        }
    }
}

@Composable
private fun SetMarkerMemo(
    tempMarkerMemo: String?,
    changeTempMarkerMemo: (String) -> Unit,
) {
    Text(
        stringResource(id = R.string.edit_memo_title),
        style = MaterialTheme.typography.bodyMedium,
        color = Color.Gray,
        fontWeight = FontWeight.Bold,
    )

    OutlinedTextField(
        colors =
            OutlinedTextFieldDefaults.colors(
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White,
            ),
        value = tempMarkerMemo ?: "",
        onValueChange = { changeTempMarkerMemo(it) },
        modifier =
            Modifier
                .fillMaxWidth()
                .height(150.dp)
                .padding(horizontal = 32.dp),
        placeholder = { Text(stringResource(id = R.string.edit_memo_hint), color = Color.Gray) },
        singleLine = false,
        maxLines = 10,
        keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Default),
    )
}

@Composable
private fun SetMarkerColor(
    selectedHue: Float,
    onHueSelected: (Float) -> Unit,
) {
    Text(
        stringResource(id = R.string.setMarker_set_Color),
        color = Color.Gray,
        fontWeight = FontWeight.Bold,
    )

    Row(
        horizontalArrangement = Arrangement.SpaceAround,
        modifier = Modifier.fillMaxWidth(),
    ) {
        val colorOptions =
            listOf(
                BitmapDescriptorFactory.HUE_RED to stringResource(id = R.string.color_red),
                BitmapDescriptorFactory.HUE_BLUE to stringResource(id = R.string.color_blue),
                BitmapDescriptorFactory.HUE_GREEN to stringResource(id = R.string.color_green),
                BitmapDescriptorFactory.HUE_YELLOW to stringResource(id = R.string.color_yellow),
            )
        colorOptions.forEach { (hue, label) ->
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                RadioButton(
                    selected = selectedHue == hue,
                    onClick = { onHueSelected(hue) },
                )
                Text(label, color = Color.White, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun SetMarkerName(
    tempMarkerName: String?,
    changeTempMarkerName: (String) -> Unit,
    focusManager: FocusManager,
) {
    Text(
        stringResource(id = R.string.setMarker_title),
        color = Color.White,
        fontWeight = FontWeight.Bold,
    )
    Spacer(modifier = Modifier.height(24.dp))
    OutlinedTextField(
        colors =
            OutlinedTextFieldDefaults.colors(
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White,
            ),
        value = tempMarkerName ?: "",
        onValueChange = {
            changeTempMarkerName(it)
        },
        label = { Text(stringResource(id = R.string.setMarker_field_label), color = Color.Gray) },
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
        keyboardActions =
            KeyboardActions(
                onDone = {
                    focusManager.clearFocus()
                },
            ),
    )
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
        resetTempMarkers = {},
        changeTempMarkerName = {},
        addVisibleMarker = {},
        addMarker = {},
        changeShowConfirmDialog = {},
        changeTempMarkerMemo = {},
        tempMarkerMemo = "メモです",
        memoEmbedding = { _, _ -> },
    )
}
