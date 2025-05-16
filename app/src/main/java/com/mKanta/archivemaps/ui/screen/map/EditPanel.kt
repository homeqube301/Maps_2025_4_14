package com.mKanta.archivemaps.ui.screen.map

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.VideoView
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.RadioButton
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
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.net.toUri
import coil.compose.AsyncImage
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.mKanta.archivemaps.R
import com.mKanta.archivemaps.domain.model.LatLngSerializable
import com.mKanta.archivemaps.domain.model.NamedMarker
import com.mKanta.archivemaps.ui.theme.ArchivemapsTheme
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
    memoEmbedding: (NamedMarker, String) -> Unit,
    changeShowConfirmDialog: () -> Unit,
) {
    ArchivemapsTheme {
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

                    selectedMarker?.let { marker ->
                        val index =
                            permanentMarkers.indexOfFirst { markerItem -> markerItem.id == marker.id }
                        if (index != -1) {
                            val updatedMarker =
                                when {
                                    mimeType?.startsWith("image/") == true -> marker.copy(imageUri = it.toString())
                                    mimeType?.startsWith("video/") == true -> marker.copy(videoUri = it.toString())
                                    else -> marker
                                }

                            onMarkerUpdate(updatedMarker)
                        }
                    }
                }
            }

        val scrollState = rememberScrollState()
        BackHandler(enabled = true) {
            changeShowConfirmDialog()
        }

        Column(
            modifier =
                Modifier
                    .background(MaterialTheme.colorScheme.background)
                    .padding(16.dp)
                    .fillMaxWidth()
                    .verticalScroll(scrollState),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            selectedMarker?.let { marker ->

                Text(
                    stringResource(id = R.string.edit_title),
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                )

                MarkerInfoSection(
                    marker = marker,
                    address = address,
                )

                Spacer(modifier = Modifier.height(16.dp))

                MarkerNameEditor(
                    selectedMarker = selectedMarker,
                    permanentMarkers = permanentMarkers,
                    focusManager = focusManager,
                    onMarkerUpdate = onMarkerUpdate,
                    onPanelClose = onPanelClose,
                    mapsSaveMarker = mapsSaveMarker,
                    marker = marker,
                )

                Spacer(modifier = Modifier.height(24.dp))

                MarkerColorSelector(
                    selectedMarker = selectedMarker,
                    permanentMarkers = permanentMarkers,
                    onMarkerUpdate = onMarkerUpdate,
                    marker = marker,
                )

                Spacer(modifier = Modifier.height(24.dp))

                MediaSelector(
                    selectedMarker = selectedMarker,
                    permanentMarkers = permanentMarkers,
                    onMarkerUpdate = onMarkerUpdate,
                    marker = marker,
                    mediaPickerLauncher = mediaPickerLauncher,
                )

                Spacer(modifier = Modifier.height(16.dp))

                MemoEditor(
                    selectedMarker = selectedMarker,
                    permanentMarkers = permanentMarkers,
                    onMarkerUpdate = onMarkerUpdate,
                    memoEmbedding = memoEmbedding,
                    marker = marker,
                )

                Spacer(modifier = Modifier.height(24.dp))

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    OutlinedButton(
                        onClick = {
                            onMarkerDelete(marker)
                        },
                        modifier =
                            Modifier
                                .fillMaxWidth(0.4f),
                    ) {
                        Text(
                            stringResource(id = R.string.edit_deleteMarker),
                            fontWeight = FontWeight.Bold,
                            color = colorResource(id = R.color.alert_red),
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(
                        colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.primary),
                        modifier =
                            Modifier
                                .fillMaxWidth(0.4f),
                        onClick = {
                            onPanelClose()
                        },
                    ) {
                        Text(
                            stringResource(id = R.string.edit_name_update),
                            fontWeight = FontWeight.Bold,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun MemoEditor(
    selectedMarker: NamedMarker,
    permanentMarkers: List<NamedMarker>,
    onMarkerUpdate: (NamedMarker) -> Unit,
    memoEmbedding: (NamedMarker, String) -> Unit,
    marker: NamedMarker,
) {
    var memoText by remember(marker) { mutableStateOf(marker.memo ?: "") }
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
        value = memoText,
        onValueChange = { newText ->
            memoText = newText
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
                .onFocusChanged {
                    if (!it.isFocused) {
                        memoEmbedding(selectedMarker, memoText)
                    }
                }.fillMaxWidth()
                .height(150.dp),
        placeholder = { Text(stringResource(id = R.string.edit_memo_hint), color = Color.Gray) },
        singleLine = false,
        maxLines = 10,
        keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Default),
    )
}

@Composable
private fun MediaSelector(
    selectedMarker: NamedMarker,
    permanentMarkers: List<NamedMarker>,
    onMarkerUpdate: (NamedMarker) -> Unit,
    marker: NamedMarker,
    mediaPickerLauncher: ActivityResultLauncher<Array<String>>,
) {
    Text(
        stringResource(id = R.string.edit_media_title),
        style = MaterialTheme.typography.bodyMedium,
        color = Color.Gray,
        fontWeight = FontWeight.Bold,
    )
    Column(
        modifier =
            Modifier
                .fillMaxWidth()
                .border(
                    width = 1.dp,
                    color = Color.Gray,
                    shape = RoundedCornerShape(4.dp),
                ).padding(12.dp),
    ) {
        OutlinedButton(
            modifier = Modifier.align(Alignment.CenterHorizontally),
            border = BorderStroke(3.dp, MaterialTheme.colorScheme.primary),
            onClick = {
                mediaPickerLauncher.launch(arrayOf("image/*", "video/*"))
            },
            shape = RoundedCornerShape(8.dp),
        ) {
            Text(
                stringResource(id = R.string.edit_media_Button),
                color = Color.White,
                fontWeight = FontWeight.Bold,
            )
        }

        marker.imageUri?.let { uri ->
            Spacer(modifier = Modifier.height(16.dp))
            AsyncImage(
                model = uri,
                contentDescription = stringResource(id = R.string.edit_media_description),
                modifier =
                    Modifier
                        .size(200.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .align(Alignment.CenterHorizontally),
            )
        }

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
                        .clip(RoundedCornerShape(8.dp))
                        .align(Alignment.CenterHorizontally),
            )
        }

        if (selectedMarker.imageUri != null || selectedMarker.videoUri != null) {
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedButton(
                modifier = Modifier.align(Alignment.CenterHorizontally),
                border = BorderStroke(3.dp, MaterialTheme.colorScheme.primary),
                onClick = {
                    selectedMarker.let { marker ->
                        val index =
                            permanentMarkers.indexOfFirst { it.id == marker.id }
                        if (index != -1) {
                            val updatedMarker =
                                marker.copy(
                                    imageUri = null,
                                    videoUri = null,
                                )
                            onMarkerUpdate(updatedMarker)
                        }
                    }
                },
            ) {
                Text(stringResource(id = R.string.edit_media_delete), color = Color.White)
            }
        }
    }
}

@Composable
private fun MarkerColorSelector(
    selectedMarker: NamedMarker,
    permanentMarkers: List<NamedMarker>,
    onMarkerUpdate: (NamedMarker) -> Unit,
    marker: NamedMarker,
) {
    var selectedColorHue by remember(marker) { mutableFloatStateOf(marker.colorHue) }

    Text(
        stringResource(id = R.string.edit_color_title),
        style = MaterialTheme.typography.bodyMedium,
        color = Color.Gray,
        fontWeight = FontWeight.Bold,
    )

    val colorOptions =
        listOf(
            BitmapDescriptorFactory.HUE_RED to stringResource(id = R.string.color_red),
            BitmapDescriptorFactory.HUE_BLUE to stringResource(id = R.string.color_blue),
            BitmapDescriptorFactory.HUE_GREEN to stringResource(id = R.string.color_green),
            BitmapDescriptorFactory.HUE_YELLOW to stringResource(id = R.string.color_yellow),
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

                        selectedMarker.let { marker ->
                            val index =
                                permanentMarkers.indexOfFirst { it.id == marker.id }
                            if (index != -1) {
                                val updatedMarker =
                                    marker.copy(colorHue = hue)
                                onMarkerUpdate(updatedMarker)
                            }
                        }
                    },
                )
                Text(label, color = Color.White, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun MarkerNameEditor(
    selectedMarker: NamedMarker,
    permanentMarkers: List<NamedMarker>,
    focusManager: FocusManager,
    onMarkerUpdate: (NamedMarker) -> Unit,
    onPanelClose: () -> Unit,
    mapsSaveMarker: () -> Unit,
    marker: NamedMarker,
) {
    var editedName by remember(marker) { mutableStateOf(marker.title) }
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier =
            Modifier
                .fillMaxWidth()
                .height(IntrinsicSize.Min),
    ) {
        OutlinedTextField(
            colors =
                OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                ),
            value = editedName,
            onValueChange = { newName ->
                editedName = newName
                selectedMarker.let { marker ->
                    val index =
                        permanentMarkers.indexOfFirst { it.id == marker.id }
                    if (index != -1) {
                        val updatedMarker = marker.copy(title = editedName)
                        onMarkerUpdate(updatedMarker)
                        mapsSaveMarker()
                    }
                }
            },
            label = { Text(stringResource(id = R.string.edit_name_title), color = Color.Gray) },
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
            keyboardActions =
                KeyboardActions(
                    onDone = {
                        focusManager.clearFocus()
                    },
                ),
            modifier =
                Modifier
                    .weight(1f)
                    .fillMaxHeight(),
        )

        Spacer(modifier = Modifier.width(8.dp))
    }
}

@Composable
private fun MarkerInfoSection(
    marker: NamedMarker,
    address: String,
) {
    Text(
        text = stringResource(id = R.string.edit_time) + marker.createdAt,
        color = Color.White,
        modifier = Modifier.padding(vertical = 8.dp),
        style = MaterialTheme.typography.bodyMedium,
    )

    Text(
        text = stringResource(id = R.string.edit_address) + address.ifBlank { stringResource(id = R.string.edit_address_noting) },
        color = Color.White,
    )
}

@Preview(showBackground = true)
@Composable
fun EditPanelPreview() {
    val dummyMarker =
        NamedMarker(
            id = "1",
            title = "",
            // memo = "メモの例",
            colorHue = BitmapDescriptorFactory.HUE_RED,
            position = LatLngSerializable(35.0, 139.0),
            createdAt = "2025-04-21",
        )

    val dummyAddress: StateFlow<String> = MutableStateFlow("東京都千代田区永田町")

    EditPanel(
        selectedMarker = dummyMarker,
        onMarkerUpdate = {},
        onMarkerDelete = {},
        onPanelClose = {},
        permanentMarkers = listOf(dummyMarker),
        focusManager = LocalFocusManager.current,
        context = LocalContext.current,
        selectedAddress = dummyAddress,
        mapsSaveMarker = {},
        memoEmbedding = { _, _ -> },
        changeShowConfirmDialog = {},
    )
}
