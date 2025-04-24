package com.example.maps20250414.ui.state

import com.example.maps20250414.domain.model.NamedMarker
import com.google.android.gms.maps.model.LatLng

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