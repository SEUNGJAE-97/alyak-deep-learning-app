package com.alyak.detector.feature.family.ui.main

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.alyak.detector.R
import com.alyak.detector.feature.family.ui.main.components.ChartBar
import com.alyak.detector.feature.family.ui.main.components.DonutChart
import com.alyak.detector.feature.family.ui.main.components.DonutSegment
import com.alyak.detector.feature.family.ui.main.components.FamilyMemberButton
import com.alyak.detector.feature.family.ui.main.components.StatusRow
import com.alyak.detector.feature.family.ui.main.components.dailyStatToBarSegments
import com.alyak.detector.ui.components.ContentBox
import com.alyak.detector.ui.components.HeaderForm
import com.alyak.detector.ui.components.MultiFloatingActionButton

@Composable
fun MainScreen(
    navController: NavController,
    viewModel: MainViewModel = hiltViewModel()
) {
    val familyMembers = viewModel.familyMembers
    val schedules = viewModel.familySchedule
    val selectedIndex = viewModel.selectedIndex
    val selectedMemberStats = viewModel.selectedMemberStats
    val dateFormatter = viewModel.dateFormatter
    val totalRatio = viewModel._totalCount
    val nearestSchedule by viewModel.nearestSchedule
    val schedule = viewModel.nearestSchedule.value
    val name by viewModel.name.collectAsState()

    if (familyMembers.isEmpty()) {
        // TODO : 이후에 스피너나 다른 로딩 화면으로 대체해야함
        Text("가족 정보를 불러오는 중 입니다. ")
        return
    }

    val targetRate = familyMembers[selectedIndex].stats.successRate
    val animatedRate by animateIntAsState(
        targetValue = targetRate,
        animationSpec = tween(
            durationMillis = 800,
            easing = FastOutSlowInEasing
        ),
        label = "successRateAnim"
    )

    Scaffold(
        topBar = {
            HeaderForm(name ?: "No Name")
        },
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(paddingValues)
        ) {
            // 가족 리스트
            Row(
                horizontalArrangement = Arrangement.spacedBy(7.dp),
                modifier = Modifier.padding(16.dp)
            ) {
                familyMembers.forEachIndexed { index, member ->
                    FamilyMemberButton(
                        role = member.role,
                        name = member.name,
                        isSelected = (index == viewModel.selectedIndex),
                        onClick = {
                            viewModel.onItemSelected(index)
                        }
                    )
                }
            }
            // 컨텐츠 박스
            ContentBox(
                Modifier
                    .padding(10.dp)
                    .shadow(3.dp, RoundedCornerShape(40.dp))
            ) {

                Text(
                    "이번 주 복약 현황",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(start = 16.dp, top = 12.dp, bottom = 12.dp)
                )

                Row(
                    Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    DonutChart(
                        segments = listOf(
                            DonutSegment(
                                familyMembers[selectedIndex].stats.successRate / totalRatio.toFloat(),
                                colorResource(R.color.primaryBlue)
                            ),
                            DonutSegment(
                                familyMembers[selectedIndex].stats.scheduledCount / totalRatio.toFloat(),
                                colorResource(R.color.lightGray)
                            ),
                            DonutSegment(
                                familyMembers[selectedIndex].stats.delayedCount / totalRatio.toFloat(),
                                colorResource(R.color.Orange)
                            ),
                            DonutSegment(
                                familyMembers[selectedIndex].stats.missedCount / totalRatio.toFloat(),
                                colorResource(R.color.RealRed)
                            )
                        ),
                        modifier = Modifier
                            .size(180.dp)
                            .padding(20.dp)
                    )

                    Column(
                        verticalArrangement = Arrangement.Center,
                        modifier = Modifier.padding(start = 8.dp)
                    ) {
                        Text(
                            "$animatedRate %",
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Bold,
                            color = colorResource(R.color.primaryBlue)
                        )
                        Text("복약 성공률", fontSize = 12.sp, color = Color.Gray)
                        Spacer(Modifier.height(8.dp))

                        StatusRow(
                            color = colorResource(R.color.primaryBlue),
                            label = "복용 완료",
                            count = familyMembers[selectedIndex].stats.completeCount
                        )
                        StatusRow(
                            color = colorResource(R.color.RealRed),
                            label = "미복용",
                            count = familyMembers[selectedIndex].stats.missedCount
                        )
                        StatusRow(
                            color = colorResource(R.color.Orange),
                            label = "지연 복용",
                            count = familyMembers[selectedIndex].stats.delayedCount
                        )
                        StatusRow(
                            color = Color(0xFFD6D9DE),
                            label = "예정",
                            count = familyMembers[selectedIndex].stats.scheduledCount
                        )
                    }
                }

                // 최근 7일 복약 패턴 영역
                Text(
                    "최근 7일 복약 패턴",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp)
                )
                Row(
                    Modifier
                        .fillMaxWidth()
                        .padding(8.dp, vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.Bottom
                ) {
                    selectedMemberStats.forEach { stat ->
                        val segments = dailyStatToBarSegments(stat)
                        val dateString = dateFormatter.format(stat.date)
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.padding(horizontal = 2.dp)
                        ) {
                            ChartBar(
                                segments = segments,
                                modifier = Modifier
                                    .height(100.dp)
                                    .width(20.dp)
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                dateString,
                                fontSize = 12.sp,
                                color = Color.Gray
                            )
                        }
                    }
                }

                if (schedule != null) {
                    ScheduleCard(
                        doseTime = "${schedule.scheduledTime}",
                        medicine = schedule.pillName,
                        detail = schedule.detail,
                        timeLeft = schedule.pillDosage.toString(),
                        onAlarmClick = {
                            viewModel.setAlarmForMedicine(schedule.pillDosage.toString())
                        }
                    )
                } else {
                    ScheduleCard(
                        doseTime = "null",
                        medicine = "null",
                        detail = "null",
                        timeLeft = "null"
                    )
                }

                // 복약 기록 박스
                Spacer(modifier = Modifier.height(10.dp))

                // TODO : 일단 막아
                //HistoryCard()
            }
        }
    }
    MultiFloatingActionButton(navController)
}

