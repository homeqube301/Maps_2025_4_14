package com.mKanta.archivemaps.ui.stateholder

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.Priority
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.maps.android.compose.CameraPositionState
import com.mKanta.archivemaps.data.repository.GeocodingRepository
import com.mKanta.archivemaps.data.repository.MarkerRepository
import com.mKanta.archivemaps.data.repository.MemoRepository
import com.mKanta.archivemaps.data.repository.UserPreferencesRepository
import com.mKanta.archivemaps.domain.model.NamedMarker
import com.mKanta.archivemaps.ui.state.MapState
import com.mKanta.archivemaps.ui.state.MapsUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class MapViewModel
    @Inject
    constructor(
        private val markerRepository: MarkerRepository,
        private val memoRepository: MemoRepository,
        private val preferencesRepository: UserPreferencesRepository,
        private val fusedLocationClient: FusedLocationProviderClient,
        private val geocodingRepository: GeocodingRepository,
    ) : ViewModel() {
        private val _uiState = MutableStateFlow(MapsUiState())
        private var currentBounds: LatLngBounds? = null
        private lateinit var locationCallback: LocationCallback
        private val _selectedAddress = MutableStateFlow("読み込み中…")

        private val _isFollowing = MutableStateFlow(false)

        val uiState: StateFlow<MapsUiState> = _uiState
        val selectedAddress: StateFlow<String> = _selectedAddress

        init {
            loadMarkers()
            viewModelScope.launch {
                preferencesRepository.showMapIntroFlow.collect { savedValue ->
                    _uiState.update { it.copy(showMapIntro = savedValue) }
                }
            }
        }

        private fun updateMarkersVisibility() {
            currentBounds?.let { bounds ->
                val visibleMarkers =
                    _uiState.value.permanentMarkers.filter {
                        bounds.contains(it.position.toLatLng())
                    }
                _uiState.update { it.copy(visibleMarkers = visibleMarkers) }
            }
        }

        fun updateVisibleMarkers(
            cameraPositionState: CameraPositionState,
            permanentMarkers: List<NamedMarker>,
        ) {
            val bounds = cameraPositionState.projection?.visibleRegion?.latLngBounds
            if (bounds != null) {
                _uiState.value.visibleMarkers
                    .map { it.id }
                    .toSet()

                val filtered =
                    permanentMarkers
                        .filter { marker ->
                            bounds.contains(marker.position.toLatLng()) &&
                                _uiState.value.permanentMarkers.any { it.id == marker.id }
                        }

                _uiState.update { it.copy(visibleMarkers = filtered) }
            }
        }

        fun addMarker(marker: NamedMarker) {
            viewModelScope.launch {
                _uiState.update { currentState ->
                    val updatedList = currentState.permanentMarkers + marker
                    currentState.copy(permanentMarkers = updatedList)
                }
                saveMarkers()
                updateMarkersVisibility()
            }
        }

        fun removeMarker(markerId: String) {
            viewModelScope.launch {
                _uiState.update { currentState ->
                    val updatedList = currentState.permanentMarkers.filter { it.id != markerId }
                    val updatedVisibleList = currentState.visibleMarkers.filter { it.id != markerId }
                    currentState.copy(
                        permanentMarkers = updatedList,
                        visibleMarkers = updatedVisibleList,
                    )
                }
                saveMarkers()

                currentBounds?.let { bounds ->
                    val filtered =
                        _uiState.value.permanentMarkers.filter {
                            bounds.contains(it.position.toLatLng())
                        }
                    _uiState.update { it.copy(visibleMarkers = filtered) }
                }
            }
        }

        fun updateMarker(updatedMarker: NamedMarker) {
            viewModelScope.launch {
                _uiState.update { currentState ->
                    val updatedList =
                        currentState.permanentMarkers.map {
                            if (it.id == updatedMarker.id) updatedMarker else it
                        }
                    currentState.copy(permanentMarkers = updatedList)
                }
                saveMarkers()
                updateMarkersVisibility()
            }
        }

        fun loadMarkers() {
            viewModelScope.launch {
                val loaded = markerRepository.loadMarkers()
                _uiState.update { it.copy(permanentMarkers = loaded) }

                updateMarkersVisibility()
            }
        }

        fun saveMarkers() {
            viewModelScope.launch {
                markerRepository.saveMarkers(_uiState.value.permanentMarkers)
            }
        }

        fun updateSearchList(
            titleQuery: String?,
            memoQuery: String?,
            visibleMarkers: List<NamedMarker>,
        ) {
            viewModelScope.launch {
                val lowerTitle = titleQuery?.lowercase()
                val lowerMemo = memoQuery?.lowercase()

                val titleFiltered =
                    if (!lowerTitle.isNullOrBlank()) {
                        visibleMarkers.filter { it.title.lowercase().contains(lowerTitle) }
                    } else {
                        emptyList()
                    }

                val memoFiltered =
                    if (!lowerMemo.isNullOrBlank()) {
                        visibleMarkers.filter { it.memo?.lowercase()?.contains(lowerMemo) == true }
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
        }

        fun changeLastCameraPosition(position: CameraPositionState) {
            _uiState.update { it.copy(lastCameraPosition = position.position) }
        }

        fun changeGoogleMapState(ready: Boolean) {
            if (ready) {
                _uiState.update { it.copy(googleMapState = MapState.Success(true)) }
            } else {
                _uiState.update { it.copy(googleMapState = MapState.Loading) }
            }
        }

        fun changeShowConfirmDialog() {
            _uiState.update { it.copy(showConfirmDialog = !it.showConfirmDialog) }
        }

        fun changeShowMapIntro() {
            val newValue = !_uiState.value.showMapIntro
            _uiState.update { it.copy(showMapIntro = newValue) }

            viewModelScope.launch {
                preferencesRepository.setShowMapIntro(newValue)
            }
        }

        fun changeIsFollowing() {
            _uiState.update { it.copy(isFollowing = !it.isFollowing) }
        }

        fun changeIsEditPanelOpen() {
            _uiState.update { it.copy(isEditPanelOpen = !it.isEditPanelOpen) }
        }

        fun changeIsPanelOpen() {
            _uiState.update { currentState ->
                val newPanelState = !currentState.isPanelOpen
                currentState.copy(
                    isPanelOpen = newPanelState,
                    tempMarkerPosition = if (!newPanelState) null else currentState.tempMarkerPosition,
                )
            }
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

        fun removeVisibleMarkers(marker: NamedMarker) {
            _uiState.update {
                it.copy(
                    visibleMarkers = it.visibleMarkers.filter { m -> m != marker },
                )
            }
        }

        fun setVisibleMarkers(marker: List<NamedMarker>) {
            _uiState.update {
                it.copy(visibleMarkers = marker)
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

        fun updateMarkerMemoEmbedding(
            marker: NamedMarker,
            newMemo: String,
        ) {
            val updatedMarker = marker.copy(memo = newMemo)

            _uiState.update { currentState ->
                val updatedList =
                    currentState.permanentMarkers.map {
                        if (it.id == updatedMarker.id) updatedMarker else it
                    }
                currentState.copy(permanentMarkers = updatedList)
            }

            viewModelScope.launch {
                memoRepository.saveMemoEmbedding(marker.id, newMemo)
            }
        }

        fun toggleFollowing() {
            _isFollowing.value = !_isFollowing.value
        }

        fun startLocationUpdates(
            context: Context,
            cameraPositionState: CameraPositionState,
        ) {
            val hasPermission =
                ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                ) == PackageManager.PERMISSION_GRANTED

            if (!hasPermission) return

            if (::locationCallback.isInitialized) {
                stopLocationUpdates()
            }

            locationCallback =
                object : LocationCallback() {
                    override fun onLocationResult(result: LocationResult) {
                        val location = result.lastLocation
                        location?.let {
                            val latLng = LatLng(it.latitude, it.longitude)

                            if (_isFollowing.value) {
                                cameraPositionState.move(
                                    CameraUpdateFactory.newLatLngZoom(
                                        latLng,
                                        17f,
                                    ),
                                )
                            }
                        }
                    }
                }

            val locationRequest =
                LocationRequest
                    .Builder(
                        Priority.PRIORITY_HIGH_ACCURACY,
                        3000L,
                    ).setMinUpdateIntervalMillis(2000L)
                    .build()

            fusedLocationClient.requestLocationUpdates(
                locationRequest,
                locationCallback,
                context.mainLooper,
            )
        }

        private fun stopLocationUpdates() {
            if (::locationCallback.isInitialized) {
                fusedLocationClient.removeLocationUpdates(locationCallback)
            }
        }

        override fun onCleared() {
            super.onCleared()
            stopLocationUpdates()
        }

        fun fetchAddressForLatLng(
            lat: Double,
            lon: Double,
        ) {
            _selectedAddress.value = "読み込み中…"

            Log.d("MarkerViewModel", "住所取得リクエスト: lat=$lat, lon=$lon")

            viewModelScope.launch {
                geocodingRepository
                    .reverseGeocode(lat, lon)
                    .onSuccess { response ->
                        _selectedAddress.value = response.displayName ?: "住所が見つかりません"
                    }.onFailure { e ->
                        Log.e("API", "住所取得失敗: ${e.message}")
                        _selectedAddress.value =
                            when (e) {
                                is IOException -> "ネットワークエラー: ${e.message}"
                                is HttpException -> "サーバーエラー: ${e.code()}"
                                else -> "予期しないエラー: ${e.message}"
                            }
                    }
            }
        }

        fun changeTempMarkerMemo(memo: String?) {
            _uiState.update { it.copy(tempMarkerMemo = memo) }
        }
    }
