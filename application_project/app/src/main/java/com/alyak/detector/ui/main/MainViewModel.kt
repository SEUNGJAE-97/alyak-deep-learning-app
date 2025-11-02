package com.alyak.detector.ui.main

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alyak.detector.data.family.model.DailyMedicationStat
import com.alyak.detector.data.family.model.FamilyMember
import com.alyak.detector.data.family.repository.FamilyRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale

@HiltViewModel
class MainViewModel @Inject constructor(
    private val familyRepository: FamilyRepository
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
    val _totalCount : Int get() = totalCount
    val dateFormatter = SimpleDateFormat("M/d", Locale.getDefault())
    
    /**
     * 선택된 멤버의 주간 통계 데이터
     * UI에서 dailyStatToBarSegments 함수로 BarSegments로 변환
     */
    val selectedMemberStats: List<DailyMedicationStat> 
        get() = if (_familyMembers.isNotEmpty()) _familyMembers[_selectedIndex].weeklyMedicationStats else emptyList()

    init {
        fetchFamilyMembers()
    }

    fun onItemSelected(index: Int) {
        _selectedIndex = index
        loadUserChartData()
    }

    private fun fetchFamilyMembers() {
        //TODO : API 호출
        viewModelScope.launch {
            try {
                val fetchedMembers = familyRepository.fetchMembers()
                _familyMembers.clear()
                _familyMembers.addAll(fetchedMembers)
                loadUserChartData()
            } catch (e: Exception) {
                e.stackTrace
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




}
