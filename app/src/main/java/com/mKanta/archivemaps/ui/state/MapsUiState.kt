package com.mKanta.archivemaps.ui.state

import com.google.android.gms.maps.model.LatLng
import com.mKanta.archivemaps.domain.model.NamedMarker

data class MapsUiState(
    val isPermissionGranted: Boolean = true,
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
    val editname: String? = null,
)
