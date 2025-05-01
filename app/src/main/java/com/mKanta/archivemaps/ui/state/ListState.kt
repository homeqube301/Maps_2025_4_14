package com.mKanta.archivemaps.ui.state

data class ListState(
    val markerName: String? = null,
    val startDate: String? = null,
    val endDate: String? = null,
    val memo: String? = null,
    val embeddingMemo: String? = null,
    val openStartDatePicker: Boolean = false,
    val openEndDatePicker: Boolean = false,
    val similarMarkerIds: List<String> = emptyList(),
)
