package com.example.maps_2025_4_14.model

import android.content.Context
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.maps_2025_4_14.strage.saveMarkers
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton


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

    fun addMarker(marker: NamedMarker) {
        _permanentMarkers.add(marker)
        saveMarkers()
    }

    fun removeMarker(markerId: String) {
        _permanentMarkers.removeAll { it.id == markerId }
        saveMarkers()
    }

}

interface MarkerRepository {
    suspend fun loadMarkers(): List<NamedMarker>
    suspend fun saveMarkers(markers: List<NamedMarker>)
}

class MarkerRepositoryImpl @Inject constructor(
    private val context: Context
) : MarkerRepository {
    // SharedPreferencesやファイルなどでマーカーを保存・取得する実装を行います
    override suspend fun loadMarkers(): List<NamedMarker> {
        // 例: SharedPreferencesからロード
        return listOf()
    }

    override suspend fun saveMarkers(markers: List<NamedMarker>) {
        // 例: SharedPreferencesに保存
    }
}

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideMarkerRepository(@ApplicationContext context: Context): MarkerRepository {
        return MarkerRepositoryImpl(context)
    }
}
