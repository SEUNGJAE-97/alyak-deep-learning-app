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
import com.alyak.detector.data.family.model.MemberStats
import com.alyak.detector.data.family.repository.FamilyRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Date

// 색상값용 데이터 클래스 추가
data class DonutSegmentData(val ratio: Float, val color: Int)
data class BarSegmentData(val ratio: Float, val color: Int)

//val dummyFamilyMembers = listOf(
//    FamilyMember(
//        role = "할머니",
//        name = "김싸피",
//        stats = MemberStats(
//            successRate = 78,
//            completeCount = 18,
//            missedCount = 3,
//            delayedCount = 2,
//            scheduledCount = 5
//        ),
//        isSelected = false
//    ),
//    FamilyMember(
//        role = "할아버지",
//        name = "하싸피",
//        stats = MemberStats(
//            successRate = 65,
//            completeCount = 15,
//            missedCount = 5,
//            delayedCount = 1,
//            scheduledCount = 7
//        ),
//        isSelected = false
//    ),
//    FamilyMember(
//        role = "아버지",
//        name = "하하하",
//        stats = MemberStats(
//            successRate = 85,
//            completeCount = 20,
//            missedCount = 2,
//            delayedCount = 3,
//            scheduledCount = 4
//        ),
//        isSelected = false
//    )
//)

@HiltViewModel
class MainViewModel @Inject constructor(
    private val familyRepository : FamilyRepository
) : ViewModel() {

    private val _familyMembers: SnapshotStateList<FamilyMember> = mutableStateListOf()
    val familyMembers: List<FamilyMember> get() = _familyMembers
    private var _selectedIndex by mutableIntStateOf(0)
    val selectedIndex: Int get() = _selectedIndex
    private var donutSegmentData by mutableStateOf<List<DonutSegmentData>>(emptyList())
    private var barDataWithDates by mutableStateOf<List<Pair<List<BarSegmentData>, Date>>>(emptyList())
    private var successRate by mutableIntStateOf(0)
    private var completeCount by mutableIntStateOf(0)
    private var missedCount by mutableIntStateOf(0)
    private var delayedCount by mutableIntStateOf(0)
    private var scheduledCount by mutableIntStateOf(0)

    init {
        fetchFamilyMembers()
//        _familyMembers.addAll(dummyFamilyMembers)
        loadUserChartData()
    }

    fun onItemSelected(index: Int) {
        _selectedIndex = index
    }

    private fun fetchFamilyMembers() {
        //TODO : API 호출
        viewModelScope.launch {
            try{
                val fetchedMembers = familyRepository.fetchMembers()
                _familyMembers.clear()
                _familyMembers.addAll(fetchedMembers)
            }catch (e: Exception){
                e.stackTrace
            }
        }
    }

    private fun loadUserChartData(role: String = "") {
        // 색상 값(리소스 X 직접 지정)
        donutSegmentData = listOf(
            DonutSegmentData(0.55f, 0xFF5864D9.toInt()), // primaryBlue
            DonutSegmentData(0.15f, 0xFFD6D9DE.toInt()), // 연회색
            DonutSegmentData(0.13f, 0xFFFFA626.toInt()), // Orange
            DonutSegmentData(0.17f, 0xFFFF5656.toInt())  // RealRed
        )
        successRate = familyMembers[selectedIndex].stats.successRate
        completeCount = familyMembers[selectedIndex].stats.completeCount
        missedCount = familyMembers[selectedIndex].stats.missedCount
        delayedCount = familyMembers[selectedIndex].stats.delayedCount
        scheduledCount = familyMembers[selectedIndex].stats.scheduledCount

    }

    private fun generateWeekDates(): List<String> {
        val today = LocalDate.now()
        val formatter = DateTimeFormatter.ofPattern("M/d")
        return (0..6).map { today.minusDays((6 - it).toLong()).format(formatter) }
    }

    private fun updateBarDataWithDates(dailyStats: List<DailyMedicationStat>) {
        barDataWithDates = dailyStats.map { stat ->
            val segments = listOfNotNull(
                BarSegmentData(stat.missedRatio, 0xFFFF5656.toInt()),  // red: missed
                BarSegmentData(stat.delayedRatio, 0xFFFFA626.toInt()), // orange: delayed
                BarSegmentData(stat.successRatio, 0xFF5864D9.toInt())  // blue: success
            )
            Pair(segments, stat.date)
        }
    }

}
