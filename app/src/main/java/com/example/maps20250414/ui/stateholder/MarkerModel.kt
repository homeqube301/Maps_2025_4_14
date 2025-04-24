package com.example.maps20250414.ui.stateholder

//import com.example.maps20250414.strage.MarkerStrage

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.maps20250414.domain.model.NamedMarker
import com.example.maps20250414.domain.repoitory.MarkerRepository
import com.example.maps20250414.network.NominatimApiService
import com.example.maps20250414.network.NominatimResponse
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.Priority
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.CameraPositionState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.Call
import javax.inject.Inject

@HiltViewModel
class LocationViewModel @Inject constructor(
    private val fusedLocationClient: FusedLocationProviderClient,
) : ViewModel() {

    private var locationCallback: LocationCallback? = null

    // StateFlowで追従状態を管理
    private val _isFollowing = MutableStateFlow(false)

    fun toggleFollowing() {
        _isFollowing.value = !_isFollowing.value
    }

    fun startLocationUpdates(
        context: Context,
        cameraPositionState: CameraPositionState,
        onLocationUpdate: (LatLng) -> Unit
    ) {

        val hasPermission = ContextCompat.checkSelfPermission(
            context, Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        if (!hasPermission) {
            // 権限がない場合はログに出して終了（アプリ側で別途対応する）
            println("位置情報のパーミッションが許可されていません")
            return
        }

        stopLocationUpdates()

        val locationRequest = LocationRequest.Builder(
            Priority.PRIORITY_HIGH_ACCURACY, 3000L // 3秒間隔
        )
            .setMinUpdateIntervalMillis(2000L)     // 最短2秒間隔
            .build()

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(result: LocationResult) {
                val location = result.lastLocation
                location?.let {
                    val latLng = LatLng(it.latitude, it.longitude)
                    onLocationUpdate(latLng)

                    // StateFlowの値を使う（現在の追従状態）
                    if (_isFollowing.value) {
                        cameraPositionState.move(CameraUpdateFactory.newLatLngZoom(latLng, 17f))
                    }
                }
            }
        }

        fusedLocationClient.requestLocationUpdates(
            locationRequest, locationCallback!!, context.mainLooper
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

}

@HiltViewModel
class PermanentMarkerViewModel @Inject constructor(
    private val markerRepository: MarkerRepository // 依存関係としてリポジトリを注入

) : ViewModel() {

    // 永続マーカーのリスト
    private val _permanentMarkers = mutableStateListOf<NamedMarker>()
    val permanentMarkers: List<NamedMarker> get() = _permanentMarkers

    init {
        // 初期化時にマーカーを読み込む
        loadMarkers()
    }

    // 永続マーカーをロード
    fun loadMarkers() {
        viewModelScope.launch {
            val loaded = markerRepository.loadMarkers() // リポジトリからマーカーを取得
            _permanentMarkers.clear()
            _permanentMarkers.addAll(loaded)
        }
    }

    // 永続マーカーを保存
    private fun saveMarkers() {
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

    fun addMarker(marker: NamedMarker) {
        _permanentMarkers.add(marker)
        saveMarkers()
    }

    fun removeMarker(markerId: String) {
        _permanentMarkers.removeAll { it.id == markerId }
        saveMarkers()
    }

}

@HiltViewModel
class MarkerViewModel @Inject constructor(
    private val apiService: NominatimApiService
) : ViewModel() {

    private val _selectedAddress = MutableStateFlow("読み込み中…")

    val selectedAddress: StateFlow<String> = _selectedAddress

    fun fetchAddressForLatLng(lat: Double, lon: Double) {
        _selectedAddress.value = "読み込み中…"

        Log.d("MarkerViewModel", "住所取得リクエストだよーん！！: lat=$lat, lon=$lon")

        apiService.reverseGeocode(lat, lon).enqueue(object : retrofit2.Callback<NominatimResponse> {
            override fun onResponse(
                call: Call<NominatimResponse>, response: retrofit2.Response<NominatimResponse>
            ) {
                _selectedAddress.value = if (response.isSuccessful) {
                    Log.e("API", "ステータスコード: ${response.code()}")
                    Log.e("API", "メッセージ: ${response.message()}")
                    Log.e("API", "エラー本文: ${response.errorBody()?.string()}")
                    response.body()?.displayName ?: "住所が見つかりません"
                } else {
                    "住所の取得に失敗"
                }
            }

            override fun onFailure(call: Call<NominatimResponse>, t: Throwable) {
                _selectedAddress.value = "ネットワークエラー: ${t.message}"
            }
        })
    }
}