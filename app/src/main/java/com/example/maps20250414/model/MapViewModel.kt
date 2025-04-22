package com.example.maps20250414.model

import androidx.lifecycle.ViewModel
import com.example.maps20250414.data.MapsUiState
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.CameraPositionState
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
        _uiState.update { it.copy(isPanelOpen = !it.isPanelOpen) }
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
            val TileFiltered = permanentMarkers.filter {
                it.title.lowercase().contains(lowerTitle)
            }
            _uiState.update { it.copy(titleResults = TileFiltered) }
        }
        if (!lowerMemo.isNullOrBlank()) {
            val MemoFiltered = permanentMarkers.filter {
                it.title.lowercase().contains(lowerMemo)
            }
            _uiState.update { it.copy(memoResults = MemoFiltered) }
        }
    }

    fun updateVisibleMarkers(
        cameraPositionState: CameraPositionState,
        permanentMarkers: List<NamedMarker>
    ) {
        val bounds = cameraPositionState.projection?.visibleRegion?.latLngBounds
        if (bounds != null) {
            val filtered = permanentMarkers.filter { bounds.contains(it.position.toLatLng()) }
            //uiState.visibleMarkers.clear()
            //uiState.visibleMarkers.addAll(filtered)
            //uiState.copy(visibleMarkers = filtered)
            _uiState.update { it.copy(visibleMarkers = filtered) }
        }
    }


    fun removeVisibleMarkers(
        marker: NamedMarker
    ) {
        _uiState.update {
            it.copy(
                visibleMarkers = it.visibleMarkers.filter { m -> m != marker }
            )
        }
    }

    fun addVisibleMarkers(newMarker: NamedMarker) {
        _uiState.update {
            it.copy(visibleMarkers = it.visibleMarkers + newMarker)
        }
    }


    fun addAllVisibleMarkers(marker: List<NamedMarker>) {
        _uiState.update {
            it.copy(visibleMarkers = it.visibleMarkers + marker)
        }
    }


    fun changePanelOpen(isOpen: Boolean) {
        _uiState.update { it.copy(isPanelOpen = isOpen) }
    }
}