package com.mKanta.archivemaps.ui.stateholder

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mKanta.archivemaps.data.repository.MemoRepository
import com.mKanta.archivemaps.data.repository.UserPreferencesRepository
import com.mKanta.archivemaps.domain.model.NamedMarker
import com.mKanta.archivemaps.ui.state.EmbeddingUiState
import com.mKanta.archivemaps.ui.state.ListState
import com.mKanta.archivemaps.ui.state.MarkerListUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ListViewModel
    @Inject
    constructor(
        private val memoRepository: MemoRepository,
        private val preferencesRepository: UserPreferencesRepository,
    ) : ViewModel() {
        private val _listState = MutableStateFlow(ListState())
        val listState: StateFlow<ListState> = _listState

        private val _listUIState = MutableStateFlow<MarkerListUiState>(MarkerListUiState.Loading)
        val listUIState: StateFlow<MarkerListUiState> = _listUIState.asStateFlow()

        private val _embeddingUiState = MutableStateFlow<EmbeddingUiState>(EmbeddingUiState.Loading)
        val embeddingUiState: StateFlow<EmbeddingUiState> = _embeddingUiState.asStateFlow()

        init {
            viewModelScope.launch {
                preferencesRepository.showListIntroFlow.collect { savedValue ->
                    _listState.update { it.copy(showListIntro = savedValue) }
                }
            }

            viewModelScope.launch {
                preferencesRepository.showDetailIntroFlow.collect { savedValue ->
                    _listState.update { it.copy(showDetailIntro = savedValue) }
                }
            }
        }

        fun checkListUIState(filteredMarkerList: List<NamedMarker>) {
//            _listUIState.value = MarkerListUiState.Loading
//
//            if (filteredMarkerList.isNotEmpty()) {
            _listUIState.value = MarkerListUiState.Success(filteredMarkerList)
//            }
        }

        fun changeShowDetailIntro() {
            val newValue = !_listState.value.showDetailIntro
            _listState.update { it.copy(showDetailIntro = newValue) }

            viewModelScope.launch {
                preferencesRepository.setShowDetailIntro(newValue)
            }
        }

        fun changeShowListIntro() {
            val newValue = !_listState.value.showListIntro
            _listState.update { it.copy(showListIntro = newValue) }

            viewModelScope.launch {
                preferencesRepository.setShowListIntro(newValue)
            }
        }

        fun chengeStartDatePicker() {
            _listState.update { it.copy(openStartDatePicker = !it.openStartDatePicker) }
        }

        fun chengeEndDatePicker() {
            _listState.update { it.copy(openEndDatePicker = !it.openEndDatePicker) }
        }

        fun changeMarkerName(change: String) {
            _listState.value = _listState.value.copy(markerName = change)
        }

        fun changeMemo(change: String) {
            _listState.value = _listState.value.copy(memo = change)
        }

        fun changeEmbeddingMemo(change: String) {
            _listState.value = _listState.value.copy(embeddingMemo = change)
        }

        fun changeStartDate(change: String) {
            _listState.value = _listState.value.copy(startDate = change)
        }

        fun changeEndDate(change: String) {
            _listState.value = _listState.value.copy(endDate = change)
        }

//        fun searchSimilarMarkers() {
//            viewModelScope.launch {
//                val query = listState.value.embeddingMemo
//                if (query.isNullOrBlank()) return@launch
//
//                val matchedIds = memoRepository.getSimilarMarkerIds(query)
//                if (matchedIds == null) {
//                    _listUIState.value = MarkerListUiState.Error("検索結果が取得できませんでした。")
//                    return@launch
//                }
//
//                _listState.update {
//                    it.copy(similarMarkerIds = matchedIds)
//                }
//            }
//        }

        fun searchSimilarMarkers() {
            viewModelScope.launch {
                val query = listState.value.embeddingMemo
                if (query.isNullOrBlank()) return@launch

                _embeddingUiState.value = EmbeddingUiState.Loading

                try {
                    val matchedIds = memoRepository.getSimilarMarkerIds(query)
                    if (matchedIds == null) {
                        _embeddingUiState.value =
                            EmbeddingUiState.Error("検索結果が取得できませんでした。")
                        return@launch
                    }

                    _listState.update {
                        it.copy(similarMarkerIds = matchedIds)
                    }

                    _embeddingUiState.value =
                        EmbeddingUiState.Success(
                            similarIds = matchedIds,
                        )
                } catch (e: Exception) {
                    _embeddingUiState.value =
                        EmbeddingUiState.Error(e.localizedMessage ?: "不明なエラー")
                }
            }
        }
    }
