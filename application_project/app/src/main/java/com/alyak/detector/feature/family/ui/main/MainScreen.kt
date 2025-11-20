package com.alyak.detector.feature.family.ui.main

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.alyak.detector.R
import com.alyak.detector.ui.components.BottomForm
import com.alyak.detector.ui.components.ContentBox
import com.alyak.detector.ui.components.HeaderForm
import com.alyak.detector.ui.components.MultiFloatingActionButton
import com.alyak.detector.feature.family.ui.main.components.FamilyMemberButton
import com.alyak.detector.feature.family.ui.main.components.DonutChart
import com.alyak.detector.feature.family.ui.main.components.DonutSegment
import com.alyak.detector.feature.family.ui.main.components.ChartBar
import com.alyak.detector.feature.family.ui.main.components.HistoryCard
import com.alyak.detector.feature.family.ui.main.components.StatusRow
import com.alyak.detector.feature.family.ui.main.components.dailyStatToBarSegments

@Composable
fun MainScreen(
    navController: NavController,
    modifier: Modifier = Modifier.background(Color.White),
    viewModel: MainViewModel = hiltViewModel()
) {
    val familyMembers = viewModel.familyMembers
    var selectedIndex by remember { mutableIntStateOf(viewModel.selectedIndex) }
    val selectedMemberStats = viewModel.selectedMemberStats
    val dateFormatter = viewModel.dateFormatter
    val totalRatio = viewModel._totalCount
    val icons = listOf(
        Icons.Filled.Home,
        Icons.Filled.DateRange,
        Icons.Filled.FavoriteBorder,
        Icons.Filled.Settings
    )

    Scaffold(
        topBar = {
            HeaderForm()
        },
        bottomBar = {
            BottomForm(
                modifier = Modifier.fillMaxWidth(),
                icons = icons,
                selectedIndex = selectedIndex,
                onItemSelected = { index ->
                    selectedIndex = index
                    viewModel.onItemSelected(index)
                }
            )
        },
        floatingActionButton = {
            MultiFloatingActionButton()
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
        ) {
            // 가족 리스트
            Row(
                horizontalArrangement = Arrangement.spacedBy(18.dp),
                modifier = Modifier.padding(16.dp)
            ) {
                familyMembers.forEach { member ->
                    FamilyMemberButton(
                        role = member.role,
                        name = member.name,
                        isSelected = member.isSelected
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
                            DonutSegment(familyMembers[selectedIndex].stats.successRate/totalRatio.toFloat(), colorResource(R.color.primaryBlue)),
                            DonutSegment(familyMembers[selectedIndex].stats.scheduledCount/totalRatio.toFloat(), colorResource(R.color.lightGray)),
                            DonutSegment(familyMembers[selectedIndex].stats.delayedCount/totalRatio.toFloat(), colorResource(R.color.Orange)),
                            DonutSegment(familyMembers[selectedIndex].stats.missedCount/totalRatio.toFloat(), colorResource(R.color.RealRed))
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
                            "${familyMembers[selectedIndex].stats.successRate}",
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
                ScheduleCard()

                // 복약 기록 박스
                Spacer(modifier = Modifier.height(10.dp))

                HistoryCard()
            }
        }
    }
}


@Composable
@Preview(showBackground = true, heightDp = 2000)
fun HistoryScreenPrev() {
    MainScreen(
        navController = rememberNavController()
    )
}