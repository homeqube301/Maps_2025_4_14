package com.example.maps20250414.ui.stateholder

import androidx.lifecycle.ViewModel
import com.example.maps20250414.ui.state.ListState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class ListViewModel @Inject constructor() : ViewModel() {
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

    fun changeStartDate(change: String) {
        _listState.value = _listState.value.copy(startDate = change)
    }

    fun changeEndDate(change: String) {
        _listState.value = _listState.value.copy(endDate = change)
    }

}