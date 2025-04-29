package com.mKanta.archivemaps.ui.stateholder

import android.util.Log
import androidx.lifecycle.ViewModel
import com.mKanta.archivemaps.network.NominatimApiService
import com.mKanta.archivemaps.network.NominatimResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import retrofit2.Call
import javax.inject.Inject

@HiltViewModel
class MarkerViewModel @Inject constructor(
    private val apiService: NominatimApiService,
) : ViewModel() {
    private val _selectedAddress = MutableStateFlow("読み込み中…")

    val selectedAddress: StateFlow<String> = _selectedAddress

    fun fetchAddressForLatLng(
        lat: Double,
        lon: Double,
    ) {
        _selectedAddress.value = "読み込み中…"

        Log.d("MarkerViewModel", "住所取得リクエスト: lat=$lat, lon=$lon")

        apiService.reverseGeocode(lat, lon).enqueue(
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
