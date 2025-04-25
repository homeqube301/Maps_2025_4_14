package com.example.maps20250414.ui.state

data class ListState(
    val markerName: String? = null,
    val startDate: String? = null,
    val endDate: String? = null,
    val memo: String? = null,
    // DatePicker用の状態
    val openStartDatePicker: Boolean = false,
    val openEndDatePicker: Boolean = false,

    )