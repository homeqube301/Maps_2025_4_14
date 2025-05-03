package com.mKanta.archivemaps.ui.stateholder

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import androidx.compose.runtime.mutableStateListOf
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
import com.google.maps.android.compose.CameraPositionState
import com.mKanta.archivemaps.data.repository.GeocodingRepository
import com.mKanta.archivemaps.data.repository.MarkerRepository
import com.mKanta.archivemaps.data.repository.MemoRepository
import com.mKanta.archivemaps.data.repository.UserPreferencesRepository
import com.mKanta.archivemaps.domain.model.NamedMarker
import com.mKanta.archivemaps.network.NominatimResponse
import com.mKanta.archivemaps.ui.state.MapsUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import retrofit2.Call
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
        private var locationCallback: LocationCallback? = null

        // StateFlowで追従状態を管理
        private val _isFollowing = MutableStateFlow(false)

        // 永続マーカーのリスト
        private val _permanentMarkers = mutableStateListOf<NamedMarker>()
        val uiState: StateFlow<MapsUiState> = _uiState
        val permanentMarkers: List<NamedMarker>
            get() = _permanentMarkers

        init {
            loadMarkers()

//            viewModelScope.launch {
//                preferencesRepository.showMapIntroFlow.collect { savedValue ->
//                    _uiState.update { it.copy(showMapIntro = savedValue) }
//                }
//            }
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

        // 永続マーカーをロード
        fun loadMarkers() {
            viewModelScope.launch {
                val loaded = markerRepository.loadMarkers()
                _permanentMarkers.clear()
                _permanentMarkers.addAll(loaded)
            }
        }

        // 永続マーカーを保存
        fun saveMarkers() {
            viewModelScope.launch {
                markerRepository.saveMarkers(_permanentMarkers)
            }
        }

        // 永続マーカーの更新
        fun updateMarker(updatedMarker: NamedMarker) {
            val index = _permanentMarkers.indexOfFirst { it.id == updatedMarker.id }
            if (index != -1) {
                _permanentMarkers[index] = updatedMarker
                saveMarkers() // 更新後に保存
            }
        }

        fun updateMarkerMemoEmbedding(
            marker: NamedMarker,
            newMemo: String,
        ) {
            val updatedMarker = marker.copy(memo = newMemo)
            val index = _permanentMarkers.indexOfFirst { it.id == updatedMarker.id }
            if (index != -1) {
                viewModelScope.launch {
                    memoRepository.saveMemoEmbedding(marker.id, newMemo)
                }
            }
        }

        fun addMarker(marker: NamedMarker) {
            _permanentMarkers.add(marker)
            saveMarkers()
        }

        fun removeMarker(markerId: String) {
            _permanentMarkers.removeAll { it.id == markerId }
            saveMarkers()
        }

        fun toggleFollowing() {
            _isFollowing.value = !_isFollowing.value
        }

        fun startLocationUpdates(
            context: Context,
            cameraPositionState: CameraPositionState,
            onLocationUpdate: (LatLng) -> Unit,
        ) {
            val hasPermission =
                ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                ) == PackageManager.PERMISSION_GRANTED

            if (!hasPermission) {
                return
            }

            stopLocationUpdates()

            val locationRequest =
                LocationRequest
                    .Builder(
                        Priority.PRIORITY_HIGH_ACCURACY,
                        3000L, // 3秒間隔
                    ).setMinUpdateIntervalMillis(2000L) // 最短2秒間隔
                    .build()

            locationCallback =
                object : LocationCallback() {
                    override fun onLocationResult(result: LocationResult) {
                        val location = result.lastLocation
                        location?.let {
                            val latLng = LatLng(it.latitude, it.longitude)
                            onLocationUpdate(latLng)

                            // StateFlowの値を使う（現在の追従状態）
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

            fusedLocationClient.requestLocationUpdates(
                locationRequest,
                locationCallback!!,
                context.mainLooper,
            )
        }

        private fun stopLocationUpdates() {
            locationCallback?.let {
                fusedLocationClient.removeLocationUpdates(it)
                locationCallback = null
            }
        }

        override fun onCleared() {
            super.onCleared()
            // ViewModelが破棄されるときにも解除
            stopLocationUpdates()
        }

        private val _selectedAddress = MutableStateFlow("読み込み中…")

        val selectedAddress: StateFlow<String> = _selectedAddress

        fun fetchAddressForLatLng(
            lat: Double,
            lon: Double,
        ) {
            _selectedAddress.value = "読み込み中…"

            Log.d("MarkerViewModel", "住所取得リクエスト: lat=$lat, lon=$lon")

            geocodingRepository.reverseGeocode(lat, lon).enqueue(
                object : retrofit2.Callback<NominatimResponse> {
                    override fun onResponse(
                        call: Call<NominatimResponse>,
                        response: retrofit2.Response<NominatimResponse>,
                    ) {
                        _selectedAddress.value =
                            if (response.isSuccessful) {
                                Log.e("API", "ステータスコード: ${response.code()}")
                                Log.e("API", "メッセージ: ${response.message()}")
                                Log.e("API", "エラー本文: ${response.errorBody()?.string()}")
                                response.body()?.displayName ?: "住所が見つかりません"
                            } else {
                                "住所の取得に失敗"
                            }
                    }

                    override fun onFailure(
                        call: Call<NominatimResponse>,
                        t: Throwable,
                    ) {
                        _selectedAddress.value = "ネットワークエラー: ${t.message}"
                    }
                },
            )
        }
    }
