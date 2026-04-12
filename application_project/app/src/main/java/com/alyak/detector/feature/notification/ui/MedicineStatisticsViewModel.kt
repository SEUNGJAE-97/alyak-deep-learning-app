package com.alyak.detector.feature.notification.ui

import androidx.lifecycle.ViewModel
import com.alyak.detector.feature.notification.data.model.MealTime
import com.alyak.detector.feature.notification.data.model.MedicationTimeEntry
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

@HiltViewModel
class MedicineStatisticsViewModel @Inject constructor(
) : ViewModel() {
    private val _timeEntries = MutableStateFlow(
        listOf(
            MedicationTimeEntry(id = 0, mealTime = MealTime.MORNING, hour = 8, minute = 0),
            MedicationTimeEntry(id = 1, mealTime = MealTime.LUNCH, hour = 13, minute = 0),
            MedicationTimeEntry(id = 2, mealTime = MealTime.DINNER, hour = 19, minute = 0),
        )
    )
    val timeEntries: StateFlow<List<MedicationTimeEntry>> = _timeEntries
    private val _selectedPills = MutableStateFlow<List<String>>(emptyList())
    val selectedPills: StateFlow<List<String>> = _selectedPills.asStateFlow()

    fun addTimeEntry(mealTime: MealTime, hour: Int, minute: Int) {
        val current = _timeEntries.value
        if (current.any { it.mealTime == mealTime }) return
        val newEntry = MedicationTimeEntry(
            id = (current.maxOfOrNull { it.id } ?: 0) + 1,
            mealTime = mealTime,
            hour = hour,
            minute = minute,
        )
        _timeEntries.value = (current + newEntry)
            .sortedBy { it.mealTime.ordinal }
    }

    fun removeTimeEntry(id: Int) {
        _timeEntries.value = _timeEntries.value.filter { it.id != id }
    }

    /**
     * 약 이름을 리스트에 추
     */
    fun addPill(pillName: String) {
        if (pillName.isBlank()) return

        val current = _selectedPills.value
        if (!current.contains(pillName)) {
            _selectedPills.value = current + pillName
        }
    }

    /**
     * 리스트에서 특정 약 제거
     */
    fun removePill(pillName: String) {
        _selectedPills.value = _selectedPills.value.filter { it != pillName }
    }
}