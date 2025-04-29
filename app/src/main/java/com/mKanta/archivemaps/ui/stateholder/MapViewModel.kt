package com.mKanta.archivemaps.ui.stateholder

import androidx.lifecycle.ViewModel
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.CameraPositionState
import com.mKanta.archivemaps.domain.model.NamedMarker
import com.mKanta.archivemaps.ui.state.MapsUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class MapViewModel
@Inject
constructor() : ViewModel() {
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

    fun changeTitleQuery(answer: String) {
        _uiState.update { it.copy(titleQuery = answer) }
    }

    fun changeMemoQuery(answer: String) {
        _uiState.update { it.copy(memoQuery = answer) }
    }

    fun changeSelectedMarker(updateMarker: NamedMarker?) {
        _uiState.update { it.copy(selectedMarker = updateMarker) }
    }

    fun changeTempMarkerName(answer: String?) {
        _uiState.update { it.copy(tempMarkerName = answer) }
    }

    fun changeTempMarkerPosition(answer: LatLng?) {
        _uiState.update { it.copy(tempMarkerPosition = answer) }
    }

    fun updateSearchList(
        titleQuery: String?,
        memoQuery: String?,
        visibleMarkers: List<NamedMarker>,
    ) {
        val lowerTitle = titleQuery?.lowercase()
        val lowerMemo = memoQuery?.lowercase()

        _uiState.update { it.copy(titleResults = emptyList()) }
        _uiState.update { it.copy(memoResults = emptyList()) }

        val titleFiltered =
            if (!lowerTitle.isNullOrBlank()) {
                visibleMarkers.filter {
                    it.title.lowercase().contains(lowerTitle)
                }
            } else {
                emptyList()
            }

        val memoFiltered =
            if (!lowerMemo.isNullOrBlank()) {
                visibleMarkers.filter {
                    it.memo?.lowercase()?.contains(lowerMemo) == true
                }
            } else {
                emptyList()
            }

        _uiState.update {
            it.copy(
                titleResults = titleFiltered,
                memoResults = memoFiltered,
            )
        }
    }

    fun updateVisibleMarkers(
        cameraPositionState: CameraPositionState,
        permanentMarkers: List<NamedMarker>,
    ) {
        val bounds = cameraPositionState.projection?.visibleRegion?.latLngBounds
        if (bounds != null) {
            val filtered = permanentMarkers.filter { bounds.contains(it.position.toLatLng()) }
            // uiState.visibleMarkers.clear()
            // uiState.visibleMarkers.addAll(filtered)
            // uiState.copy(visibleMarkers = filtered)
            _uiState.update { it.copy(visibleMarkers = filtered) }
        }
    }

    fun removeVisibleMarkers(marker: NamedMarker) {
        _uiState.update {
            it.copy(
                visibleMarkers = it.visibleMarkers.filter { m -> m != marker },
            )
        }
    }

    fun addAllVisibleMarkers(marker: List<NamedMarker>) {
        _uiState.update {
            it.copy(visibleMarkers = (it.visibleMarkers + marker).distinct())
        }
    }

    fun changeUserLocation(location: LatLng) {
        _uiState.update {
            it.copy(userLocation = location)
        }
    }

    fun changePanelOpen(isOpen: Boolean) {
        _uiState.update { it.copy(isPanelOpen = isOpen) }
    }
}
