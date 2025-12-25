package com.alyak.detector.feature.family.ui.main

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alyak.detector.core.auth.TokenManager
import com.alyak.detector.core.network.ApiResult
import com.alyak.detector.core.util.AlarmScheduler
import com.alyak.detector.feature.family.data.model.DailyMedicationStat
import com.alyak.detector.feature.family.data.model.FamilyMember
import com.alyak.detector.feature.family.data.model.MedicineSchedule
import com.alyak.detector.feature.family.data.repository.FamilyRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@HiltViewModel
class MainViewModel @Inject constructor(
    private val familyRepo: FamilyRepo,
    private val tokenManager: TokenManager,
    private val alarmScheduler: AlarmScheduler
) : ViewModel() {

    private var _selectedIndex by mutableIntStateOf(0)
    val selectedIndex: Int get() = _selectedIndex
    private var successRate by mutableIntStateOf(0)
    private var completeCount by mutableIntStateOf(0)
    private var missedCount by mutableIntStateOf(0)
    private var delayedCount by mutableIntStateOf(0)
    private var scheduledCount by mutableIntStateOf(0)
    private var totalCount = completeCount + missedCount + delayedCount + scheduledCount
    val _totalCount: Int get() = totalCount
    val dateFormatter = SimpleDateFormat("M/d", Locale.getDefault())
    val familySchedule: List<MedicineSchedule> get() = _familySchedules
    private val _familySchedules: SnapshotStateList<MedicineSchedule> = mutableStateListOf()
    private val _nearestSchedule = mutableStateOf<MedicineSchedule?>(null)
    val nearestSchedule: State<MedicineSchedule?> = _nearestSchedule
    private val _name = MutableStateFlow<String?>(null)
    val name: StateFlow<String?> = _name
    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading
    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage
    private val _familyMembers = mutableStateListOf<FamilyMember>()
    val familyMembers: List<FamilyMember> get() = _familyMembers

    /**
     * 선택된 멤버의 주간 통계 데이터
     * UI에서 dailyStatToBarSegments 함수로 BarSegments로 변환
     */
    val selectedMemberStats: List<DailyMedicationStat>
        get() = if (_familyMembers.isNotEmpty()) _familyMembers[_selectedIndex].weeklyMedicationStats else emptyList()

    init {
        fetchName()
        fetchFamilyMembers()
        fetchSchedules()
    }

    fun onItemSelected(index: Int) {
        _selectedIndex = index
        loadUserChartData()
    }

    private fun fetchName() {
        viewModelScope.launch {
            _name.value = tokenManager.getUserName()
        }
    }

    private fun fetchFamilyMembers() {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            val apiResult = familyRepo.fetchMembers()

            when (apiResult) {
                // 1. API 호출이 성공한 경우
                is ApiResult.Success -> {
                    _familyMembers.clear()
                    _familyMembers.addAll(apiResult.data)
                    if (_familyMembers.isNotEmpty()) {
                        loadUserChartData()
                    }
                    _isLoading.value = false
                }

                // 2. API 호출이 실패한 경우
                is ApiResult.Error -> {
                    _errorMessage.value = apiResult.message
                    _isLoading.value = false
                }

                // 5. 예외 처리
                is ApiResult.Exception -> {
                    _errorMessage.value = "네트워크 연결을 확인해주세요."
                    _isLoading.value = false
                }
            }
        }
    }

    private fun loadUserChartData() {
        if (_familyMembers.isNotEmpty()) {
            successRate = _familyMembers[_selectedIndex].stats.successRate
            completeCount = _familyMembers[_selectedIndex].stats.completeCount
            missedCount = _familyMembers[_selectedIndex].stats.missedCount
            delayedCount = _familyMembers[_selectedIndex].stats.delayedCount
            scheduledCount = _familyMembers[_selectedIndex].stats.scheduledCount
        }
    }

    private fun fetchSchedules() {
        viewModelScope.launch {
            val apiResult = familyRepo.fetchSchedule()
            when (apiResult) {
                is ApiResult.Success -> {
                    val fetchSchedule = apiResult.data
                    _familySchedules.clear()
                    _familySchedules.addAll(fetchSchedule)
                    _nearestSchedule.value = getNearestSchedule(fetchSchedule)
                }

                is ApiResult.Error -> {
                    Log.e("MainViewModel", "API Error: ${apiResult.code} - ${apiResult.message}")
                }

                is ApiResult.Exception -> {
                    Log.e("MainViewModel", "Network Exception: ${apiResult.throwable.message}")
                }
            }
        }
    }

    private fun getNearestSchedule(
        schedules: List<MedicineSchedule>,
        now: Date = Date()
    ): MedicineSchedule? {
        return schedules
            .filter { it.scheduledTime.after(now) }
            .minByOrNull { it.scheduledTime.time - now.time }
    }


    fun setAlarmForMedicine(timeLeftString: String) {
        val minutes = timeLeftString.toIntOrNull() ?: return
        alarmScheduler.scheduleAlarm(minutes)

    }

}
