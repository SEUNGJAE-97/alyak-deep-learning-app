package com.alyak.detector.feature.notification.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alyak.detector.core.network.ApiResult
import com.alyak.detector.feature.notification.data.model.MealTime
import com.alyak.detector.feature.notification.data.model.MedicationTimeEntry
import com.alyak.detector.feature.notification.alarm.MedicationAlarmScheduler
import com.alyak.detector.feature.notification.data.local.ScheduleBackupLocalRepository
import com.alyak.detector.feature.notification.data.model.ScheduleBackupRequest
import com.alyak.detector.feature.notification.data.repository.ScheduleRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

sealed interface SaveScheduleUiState {
    data object Idle : SaveScheduleUiState
    data object Loading : SaveScheduleUiState
    data object Success : SaveScheduleUiState
    data class Error(val message: String) : SaveScheduleUiState
}

@HiltViewModel
class MedicineStatisticsViewModel @Inject constructor(
    private val scheduleRepository: ScheduleRepository,
    private val scheduleBackupLocalRepository: ScheduleBackupLocalRepository,
    private val medicationAlarmScheduler: MedicationAlarmScheduler,
) : ViewModel() {

    private val _timeEntries = MutableStateFlow(
        listOf(
            MedicationTimeEntry(id = 0, mealTime = MealTime.MORNING, hour = 8, minute = 0),
            MedicationTimeEntry(id = 1, mealTime = MealTime.LUNCH, hour = 13, minute = 0),
            MedicationTimeEntry(id = 2, mealTime = MealTime.DINNER, hour = 19, minute = 0),
        ),
    )
    val timeEntries: StateFlow<List<MedicationTimeEntry>> = _timeEntries.asStateFlow()

    private val _selectedPills = MutableStateFlow<List<String>>(emptyList())
    val selectedPills: StateFlow<List<String>> = _selectedPills.asStateFlow()

    private val _saveUiState = MutableStateFlow<SaveScheduleUiState>(SaveScheduleUiState.Idle)
    val saveUiState: StateFlow<SaveScheduleUiState> = _saveUiState.asStateFlow()

    fun resetSaveUiState() {
        _saveUiState.value = SaveScheduleUiState.Idle
    }

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

    fun addPill(pillName: String) {
        if (pillName.isBlank()) return
        val current = _selectedPills.value
        if (!current.contains(pillName)) {
            _selectedPills.value = current + pillName
        }
    }

    fun removePill(pillName: String) {
        _selectedPills.value = _selectedPills.value.filter { it != pillName }
    }

    /**
     * [POST /api/schedule/backup] — 선택한 약 × 복약 시간마다 한 건씩 저장합니다.
     */
    fun saveScheduleToServer() {
        val pills = _selectedPills.value
        val times = _timeEntries.value
        if (pills.isEmpty()) {
            _saveUiState.value = SaveScheduleUiState.Error("약을 한 가지 이상 선택해 주세요.")
            return
        }
        if (times.isEmpty()) {
            _saveUiState.value = SaveScheduleUiState.Error("복약 시간을 추가해 주세요.")
            return
        }

        val startDate = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE)
        val endDate = LocalDate.now().plusYears(1).format(DateTimeFormatter.ISO_LOCAL_DATE)

        val requests = buildList {
            for (pillName in pills) {
                for (entry in times) {
                    val scheduledTime = String.format(
                        Locale.US,
                        "%02d:%02d:00",
                        entry.hour,
                        entry.minute,
                    )
                    add(
                        ScheduleBackupRequest(
                            pillId = null,
                            pillName = pillName,
                            dosage = 1,
                            scheduledTime = scheduledTime,
                            startDate = startDate,
                            endDate = endDate,
                        ),
                    )
                }
            }
        }

        viewModelScope.launch {
            _saveUiState.value = SaveScheduleUiState.Loading
            when (val result = scheduleRepository.backupSchedules(requests)) {
                is ApiResult.Success -> {
                    val body = result.data
                    if (body.isNotEmpty()) {
                        withContext(Dispatchers.IO) {
                            scheduleBackupLocalRepository.insertOrReplaceFromServer(body)
                            val all = scheduleBackupLocalRepository.getAllForAlarms()
                            medicationAlarmScheduler.rescheduleAllFromLocal(all)
                        }
                    }
                    _saveUiState.value = SaveScheduleUiState.Success
                }

                is ApiResult.Error -> {
                    _saveUiState.value = SaveScheduleUiState.Error(
                        result.message ?: "저장에 실패했습니다. (${result.code})",
                    )
                }

                is ApiResult.Exception -> {
                    _saveUiState.value = SaveScheduleUiState.Error(
                        result.throwable.message ?: "네트워크 오류가 발생했습니다.",
                    )
                }
            }
        }
    }
}
