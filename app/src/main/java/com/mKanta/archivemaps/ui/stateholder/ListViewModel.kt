package com.mKanta.archivemaps.ui.stateholder

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mKanta.archivemaps.data.repository.MemoRepository
import com.mKanta.archivemaps.network.fetchEmbedding
import com.mKanta.archivemaps.ui.state.ListState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ListViewModel
    @Inject
    constructor(
        private val memoRepository: MemoRepository,
    ) : ViewModel() {
        private val _listState = MutableStateFlow(ListState())
        val listState: StateFlow<ListState> = _listState

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

        fun searchSimilarMarkers() {
            viewModelScope.launch {
                val query = listState.value.embeddingMemo
                if (query != null) {
                    if (query.isBlank()) return@launch
                }

                val embedding =
                    query?.let { fetchEmbedding(memoRepository.openAiApi, it) } ?: return@launch
                val similarMemos = memoRepository.getSimilarMemos(embedding) ?: return@launch

                val matchedIds = similarMemos.map { it.marker_id }

            _listState.update {
                it.copy(similarMarkerIds = matchedIds)
            }
        }
    }
}
