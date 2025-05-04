package com.mKanta.archivemaps.ui.state

import com.mKanta.archivemaps.domain.model.NamedMarker

data class ListState(
    val markerName: String? = null,
    val startDate: String? = null,
    val endDate: String? = null,
    val memo: String? = null,
    val embeddingMemo: String? = null,
    val openStartDatePicker: Boolean = false,
    val openEndDatePicker: Boolean = false,
    val similarMarkerIds: List<String> = emptyList(),
    val showListIntro: Boolean = true,
    val showDetailIntro: Boolean = true,
)

sealed interface MarkerListUiState {
    data object Loading : MarkerListUiState

    data class Success(
        val filteredMarkerList: List<NamedMarker>,
    ) : MarkerListUiState

    data class Error(
        val message: String? = null,
    ) : MarkerListUiState
}

sealed interface EmbeddingUiState {
    data object Loading : EmbeddingUiState

    data class Success(
        val similarIds: List<String>,
    ) : EmbeddingUiState

    data class Error(
        val message: String? = null,
    ) : EmbeddingUiState
}
