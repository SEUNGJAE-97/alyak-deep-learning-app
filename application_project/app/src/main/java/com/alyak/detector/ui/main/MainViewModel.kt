package com.alyak.detector.ui.main

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alyak.detector.data.family.model.FamilyMember
import com.alyak.detector.data.family.repository.FamilyRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.launch

// 색상값용 데이터 클래스 추가
data class DonutSegmentData(val ratio: Float, val color: Int)
data class BarSegmentData(val ratio: Float, val color: Int)

@HiltViewModel
class MainViewModel @Inject constructor(
    private val familyRepository : FamilyRepository
) : ViewModel() {
    private val familyMembers: SnapshotStateList<FamilyMember> = mutableStateListOf()
    private var selectedIndex by mutableIntStateOf(0)
    private var donutSegmentData by mutableStateOf<List<DonutSegmentData>>(emptyList())
    private var barDataWithDates by mutableStateOf<List<Pair<List<BarSegmentData>, String>>>(emptyList())
    private var successRate by mutableIntStateOf(0)
    private var completeCount by mutableIntStateOf(0)
    private var missedCount by mutableIntStateOf(0)
    private var delayedCount by mutableIntStateOf(0)
    private var scheduledCount by mutableIntStateOf(0)

    init {
        fetchFamilyMembers()
        loadUserChartData()
    }

    fun onItemSelected(index: Int) {
        selectedIndex = index
    }

    fun selectMember(selectedRole: String) {
//        familyMembers.replaceAll { member ->
//            member.copy(isSelected = member.role == selectedRole)
//        }
    }

    private fun fetchFamilyMembers() {
        //TODO : API 호출
        viewModelScope.launch {
            try{
                val fetchedMembers = familyRepository.fetchMembers()
                familyMembers.clear()
                familyMembers.addAll(fetchedMembers)
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

        barDataWithDates = listOf(
            Pair(listOf(BarSegmentData(1f, 0xFF5864D9.toInt())), "5/30"),
            Pair(listOf(BarSegmentData(1f, 0xFF5864D9.toInt())), "5/31"),
            Pair(
                listOf(
                    BarSegmentData(0.4f, 0xFFFF5656.toInt()),
                    BarSegmentData(0.6f, 0xFF5864D9.toInt())
                ), "6/1"
            ),
            Pair(listOf(BarSegmentData(1f, 0xFF5864D9.toInt())), "6/2"),
            Pair(listOf(BarSegmentData(1f, 0xFF5864D9.toInt())), "6/3"),
            Pair(
                listOf(
                    BarSegmentData(0.2f, 0xFFFF5656.toInt()),
                    BarSegmentData(0.8f, 0xFF5864D9.toInt())
                ), "6/4"
            ),
            Pair(
                listOf(
                    BarSegmentData(0.4f, 0xFFFFA626.toInt()),
                    BarSegmentData(0.6f, 0xFF5864D9.toInt())
                ), "6/5"
            )
        )
    }

}
