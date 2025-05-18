package com.mKanta.archivemaps.ui.screen.map

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.CameraPositionState
import com.mKanta.archivemaps.domain.model.NamedMarker
import com.mKanta.archivemaps.ui.theme.ArchivemapsTheme
import kotlinx.coroutines.flow.StateFlow

@Composable
fun PanelDismissOverlay(
    isEditPanelOpen: Boolean,
    isPanelOpen: Boolean,
    isSearchOpen: Boolean,
    isAccountSheetOpen: Boolean,
    changeShowConfirmDialog: () -> Unit,
    showConfirmDialog: Boolean,
    changeIsEditPanelOpen: () -> Unit,
    changeIsPanelOpen: () -> Unit,
    changeIsSearchOpen: () -> Unit,
    changeSelectedMarker: (NamedMarker?) -> Unit,
) {
    if (isEditPanelOpen || isPanelOpen || isSearchOpen || isAccountSheetOpen) {
        DismissOverlay(
            changeShowConfirmDialog = { changeShowConfirmDialog() },
            showConfirmDialog = showConfirmDialog,
            onClosePanel = {
                when {
                    isPanelOpen -> changeIsPanelOpen()
                    isSearchOpen -> changeIsSearchOpen()
                    else -> changeIsEditPanelOpen()
                }

                changeSelectedMarker(null)
            },
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapPanel(
    modifier: Modifier = Modifier,
    isSearchOpen: Boolean,
    changeIsSearchOpen: () -> Unit,
    titleResults: List<NamedMarker>,
    memoResults: List<NamedMarker>,
    titleQuery: String?,
    memoQuery: String?,
    changeTitleQuery: (String) -> Unit,
    changeMemoQuery: (String) -> Unit,
    changeSelectedMarker: (NamedMarker?) -> Unit,
    changeIsEditPanelOpen: () -> Unit,
    cameraPositionState: CameraPositionState,
    tempMarkerPosition: LatLng?,
    tempMarkerName: String?,
    changeTempMarkerPosition: (LatLng?) -> Unit,
    changeTempMarkerName: (String?) -> Unit,
    changeIsPanelOpen: () -> Unit,
    changePanelOpen: (Boolean) -> Unit,
    permanentMarkers: List<NamedMarker>,
    addAllVisibleMarkers: (List<NamedMarker>) -> Unit,
    addMarker: (NamedMarker) -> Unit,
    removeMarker: (String) -> Unit,
    updateMarker: (NamedMarker) -> Unit,
    updateMarkerMemoEmbedding: (NamedMarker, String) -> Unit,
    changeShowConfirmDialog: () -> Unit,
    context: Context,
    selectedAddress: StateFlow<String>,
    isPanelOpen: Boolean,
    isEditPanelOpen: Boolean,
    removeVisibleMarkers: (NamedMarker) -> Unit,
    selectedMarker: NamedMarker?,
    updateVisibleMarkers: (CameraPositionState, List<NamedMarker>) -> Unit,
    saveMarkers: () -> Unit,
    changeTempMarkerMemo: (String?) -> Unit,
    tempMarkerMemo: String?,
    isAccountSheetOpen: Boolean,
    onAccountSheetOpenChange: (Boolean) -> Unit,
    onSignOut: () -> Unit,
    onDeleteAccount: () -> Unit,
    accountName: String,
    accountId: String,
    onAccountNameChange: (String) -> Unit,
) {
    ArchivemapsTheme {
        val focusManager = LocalFocusManager.current
        val setSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
        val editSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
        val searchSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = false)

        if (isAccountSheetOpen) {
            ModalBottomSheet(
                onDismissRequest = { onAccountSheetOpenChange(false) },
                sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
                containerColor = MaterialTheme.colorScheme.background,
                dragHandle = {
                    Box(
                        modifier =
                            Modifier
                                .padding(top = 8.dp, bottom = 16.dp)
                                .height(4.dp)
                                .width(36.dp)
                                .background(
                                    color = Color.White,
                                    shape = RoundedCornerShape(2.dp),
                                ),
                    )
                },
            ) {
                AccountEditSheet(
                    onDismiss = { onAccountSheetOpenChange(false) },
                    onSignOut = {
                        onSignOut()
                        onAccountSheetOpenChange(false)
                    },
                    onDeleteAccount = {
                        onDeleteAccount()
                        onAccountSheetOpenChange(false)
                    },
                    accountName = accountName,
                    accountId = accountId,
                    onAccountNameChange = onAccountNameChange,
                )
            }
        }

        if (isSearchOpen) {
            ModalBottomSheet(
                onDismissRequest = { changeIsSearchOpen() },
                sheetState = searchSheetState,
                modifier = modifier,
                containerColor = MaterialTheme.colorScheme.background,
                dragHandle = {
                    Box(
                        modifier =
                            Modifier.Companion
                                .padding(top = 8.dp, bottom = 16.dp)
                                .height(4.dp)
                                .width(36.dp)
                                .background(
                                    color = Color.Companion.White,
                                    shape = RoundedCornerShape(2.dp),
                                ),
                    )
                },
            ) {
                SearchMaker(
                    titleResults = titleResults,
                    memoResults = memoResults,
                    titleQuery = titleQuery,
                    memoQuery = memoQuery,
                    onTitleQueryChanged = { changeTitleQuery(it) },
                    onMemoQueryChanged = { changeMemoQuery(it) },
                    onMarkerTapped = { marker ->
                        changeSelectedMarker(marker)
                        changeIsEditPanelOpen()
                        cameraPositionState.move(
                            CameraUpdateFactory.newLatLngZoom(
                                marker.position.toLatLng(),
                                17f,
                            ),
                        )
                        changeIsSearchOpen()
                        changeTitleQuery("")
                        changeMemoQuery("")
                    },
                    onMemoTapped = { marker ->
                        changeSelectedMarker(marker)
                        changeIsEditPanelOpen()
                        cameraPositionState.move(
                            CameraUpdateFactory.newLatLngZoom(
                                marker.position.toLatLng(),
                                17f,
                            ),
                        )
                        changeIsSearchOpen()
                        changeTitleQuery("")
                        changeMemoQuery("")
                    },
                )
            }
        }

        if (isPanelOpen) {
            ModalBottomSheet(
                onDismissRequest = {
                    changePanelOpen(false)
                    changeTempMarkerPosition(null)
                    changeTempMarkerName(null)
                    changeTempMarkerMemo(null)
                },
                sheetState = setSheetState,
                containerColor = MaterialTheme.colorScheme.background,
                dragHandle = {
                    Box(
                        modifier =
                            Modifier.Companion
                                .padding(top = 8.dp, bottom = 16.dp)
                                .height(4.dp)
                                .width(36.dp)
                                .background(
                                    color = Color.Companion.White,
                                    shape =
                                        androidx.compose.foundation.shape
                                            .RoundedCornerShape(2.dp),
                                ),
                    )
                },
            ) {
                SetMarkerPanel(
                    changeShowConfirmDialog = { changeShowConfirmDialog() },
                    cameraPositionState = cameraPositionState,
                    focusManager = focusManager,
                    tempMarkerPosition = tempMarkerPosition,
                    tempMarkerName = tempMarkerName,
                    resetTempMarkers = {
                        changeTempMarkerPosition(null)
                        changeTempMarkerName(null)
                        changeIsPanelOpen()
                    },
                    changeTempMarkerName = { changeTempMarkerName(it) },
                    changeTempMarkerMemo = { changeTempMarkerMemo(it) },
                    addVisibleMarker = { addAllVisibleMarkers(listOf(it)) },
                    addMarker = { addMarker(it) },
                    tempMarkerMemo = tempMarkerMemo,
                )
            }
        }

        if (isEditPanelOpen && selectedMarker != null) {
            ModalBottomSheet(
                onDismissRequest = {
                    changeIsEditPanelOpen()
                    changeSelectedMarker(null)
                },
                sheetState = editSheetState,
                containerColor = MaterialTheme.colorScheme.background,
                dragHandle = {
                    Box(
                        modifier =
                            Modifier.Companion
                                .padding(top = 8.dp, bottom = 16.dp)
                                .height(4.dp)
                                .width(36.dp)
                                .background(
                                    color = Color.Companion.White,
                                    shape =
                                        androidx.compose.foundation.shape
                                            .RoundedCornerShape(2.dp),
                                ),
                    )
                },
            ) {
                EditPanel(
                    selectedMarker = selectedMarker,
                    selectedAddress = selectedAddress,
                    permanentMarkers = permanentMarkers,
                    mapsSaveMarker = { saveMarkers() },
                    focusManager = focusManager,
                    context = context,
                    onMarkerUpdate = { updatedMarker ->
                        updateMarker(updatedMarker)
                        changeSelectedMarker(updatedMarker)
                        updateVisibleMarkers(cameraPositionState, permanentMarkers)
                    },
                    onMarkerDelete = { marker ->
                        removeMarker(marker.id)
                        removeVisibleMarkers(marker)
                        changeSelectedMarker(null)
                        changeIsEditPanelOpen()
                    },
                    onPanelClose = {
                        changeIsEditPanelOpen()
                        changeSelectedMarker(null)
                    },
                    memoEmbedding = updateMarkerMemoEmbedding,
                    changeShowConfirmDialog = { changeShowConfirmDialog() },
                )
            }
        }
    }
}
