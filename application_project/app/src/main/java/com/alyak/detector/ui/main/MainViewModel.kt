package com.alyak.detector.ui.main

import androidx.compose.runtime.Composable
import androidx.compose.runtime.SnapshotMutationPolicy
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.alyak.detector.R
import com.alyak.detector.ui.main.components.BarSegment
import com.alyak.detector.ui.main.components.DonutSegment
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.scopes.ViewModelScoped
import jakarta.inject.Inject
import kotlinx.coroutines.launch

data class FamilyMember(val role: String, val name: String, val isSelected: Boolean)

// 색상값용 데이터 클래스 추가
data class DonutSegmentData(val ratio: Float, val color: Int)
data class BarSegmentData(val ratio: Float, val color: Int)

@HiltViewModel
class MainViewModel @Inject constructor() : ViewModel() {
    val familyMembers: SnapshotStateList<FamilyMember> = mutableStateListOf()
    var selectedIndex by mutableIntStateOf(0)
        private set

    // UI에서 변환할 데이터(색상은 Int)
    var donutSegmentData by mutableStateOf<List<DonutSegmentData>>(emptyList())
        private set
    var barDataWithDates by mutableStateOf<List<Pair<List<BarSegmentData>, String>>>(emptyList())
        private set
    var successRate by mutableIntStateOf(0)
        private set
    var completeCount by mutableIntStateOf(0)
        private set
    var missedCount by mutableIntStateOf(0)
        private set
    var delayedCount by mutableIntStateOf(0)
        private set
    var scheduledCount by mutableIntStateOf(0)
        private set

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
            val fetchedMembers = listOf(
                FamilyMember("할머니", "김싸피", true),
                FamilyMember("할아버지", "하싸피", false),
                FamilyMember("아버지", "하하하", false)
            )
            familyMembers.clear()
            familyMembers.addAll(fetchedMembers)
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
        successRate = 78
        completeCount = 18
        missedCount = 3
        delayedCount = 2
        scheduledCount = 5

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

