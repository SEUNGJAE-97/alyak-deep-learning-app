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

    private val _familyMembers: SnapshotStateList<FamilyMember> = mutableStateListOf()
    val familyMembers: List<FamilyMember> get() = _familyMembers
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
        //TODO : API 호출
        viewModelScope.launch {
            // 1. familyRepo.fetchMembers()를 호출하고 그 결과를 apiResult 변수에 저장합니다.
            val apiResult = familyRepo.fetchMembers()

            // 2. when 표현식을 사용해 ApiResult의 상태를 확인합니다.
            when (apiResult) {
                // 3. API 호출이 성공한 경우 (ApiResult.Success)
                is ApiResult.Success -> {
                    // '포장지'에서 '선물(데이터)'을 꺼냅니다 (apiResult.data).
                    val fetchedMembers = apiResult.data
                    _familyMembers.clear()
                    // 이제 올바른 타입의 List를 addAll에 전달합니다.
                    _familyMembers.addAll(fetchedMembers)

                    if (_familyMembers.isNotEmpty()) {
                        loadUserChartData()
                    }
                }

                // 4. API 호출이 실패한 경우 (ApiResult.Error)
                is ApiResult.Error -> {
                    // 에러 상황을 처리합니다. (예: 로그 출력, 사용자에게 메시지 표시)
                    Log.e("MainViewModel", "API Error: ${apiResult.code} - ${apiResult.message}")
                }

                // 5. 네트워크 예외 등 다른 예외가 발생한 경우 (ApiResult.Exception)
                is ApiResult.Exception -> {
                    // 예외 상황을 처리합니다.
                    Log.e("MainViewModel", "Network Exception: ${apiResult.throwable.message}")
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
