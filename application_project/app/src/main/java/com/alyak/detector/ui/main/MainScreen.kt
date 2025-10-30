package com.alyak.detector.ui.main

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.runtime.mutableStateOf
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
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.alyak.detector.R
import com.alyak.detector.ui.components.BottomForm
import com.alyak.detector.ui.components.ContentBox
import com.alyak.detector.ui.components.HeaderForm
import com.alyak.detector.ui.components.MultiFloatingActionButton
import com.alyak.detector.ui.main.components.FamilyMemberButton
import com.alyak.detector.ui.main.components.DonutChart
import com.alyak.detector.ui.main.components.DonutSegment
import com.alyak.detector.ui.main.components.ChartBar
import com.alyak.detector.ui.main.components.BarSegment
import com.alyak.detector.ui.main.components.HistoryCard

@Composable
fun MainScreen(
    navController: NavController,
    modifier: Modifier = Modifier.background(Color.White),
    mainViewModel: MainViewModel = viewModel()
) {
    val icons = listOf(
        Icons.Filled.Home,
        Icons.Filled.DateRange,
        Icons.Filled.FavoriteBorder,
        Icons.Filled.Settings
    )
    var selectedIndex = mainViewModel.selectedIndex
    val familyMembers = mainViewModel.familyMembers

    Scaffold(
        topBar = {
            HeaderForm()
        },
        bottomBar = {
            BottomForm(
                modifier = Modifier.fillMaxWidth(),
                icons = icons,
                selectedIndex = selectedIndex,
                onItemSelected = { selectedIndex = it }
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
            Row(
                horizontalArrangement = Arrangement.spacedBy(18.dp),
                modifier = Modifier.padding(16.dp)
            ) {
                familyMembers.forEach{ member->
                    FamilyMemberButton(
                        role = member.role,
                        name = member.name,
                        isSelected = member.isSelected,
                        onClick = { mainViewModel.selectMember(member.role)}
                    )
                }
            }
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
                        segments = mainViewModel.donutSegmentData.map { DonutSegment(it.ratio, Color(it.color)) },
                        modifier = Modifier
                            .size(180.dp)
                            .padding(20.dp)
                    )

                    Column(
                        verticalArrangement = Arrangement.Center,
                        modifier = Modifier.padding(start = 8.dp)
                    ) {
                        Text(
                            "${mainViewModel.successRate}%",
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Bold,
                            color = colorResource(R.color.primaryBlue)
                        )
                        Text("복약 성공률", fontSize = 12.sp, color = Color.Gray)
                        Spacer(Modifier.height(8.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                Modifier
                                    .size(10.dp)
                                    .background(colorResource(R.color.primaryBlue), CircleShape)
                            )
                            Text(
                                "복용 완료: ${mainViewModel.completeCount}",
                                fontSize = 13.sp,
                                modifier = Modifier.padding(start = 6.dp)
                            )
                        }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                Modifier
                                    .size(10.dp)
                                    .background(colorResource(R.color.RealRed), CircleShape)
                            )
                            Text(
                                "미복용: ${mainViewModel.missedCount}",
                                fontSize = 13.sp,
                                modifier = Modifier.padding(start = 6.dp)
                            )
                        }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                Modifier
                                    .size(10.dp)
                                    .background(colorResource(R.color.Orange), CircleShape)
                            )
                            Text(
                                "지연 복용: ${mainViewModel.delayedCount}",
                                fontSize = 13.sp,
                                modifier = Modifier.padding(start = 6.dp)
                            )
                        }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                Modifier
                                    .size(10.dp)
                                    .background(Color(0xFFD6D9DE), CircleShape)
                            )
                            Text(
                                "예정: ${mainViewModel.scheduledCount}",
                                fontSize = 13.sp,
                                modifier = Modifier.padding(start = 6.dp)
                            )
                        }
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
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .padding(8.dp, vertical = 4.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.Bottom
                    ) {
                        mainViewModel.barDataWithDates.forEach { (segments, date) ->
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.padding(horizontal = 2.dp)
                            ) {
                                ChartBar(
                                    segments = segments.map { BarSegment(it.ratio, Color(it.color)) },
                                    modifier = Modifier
                                        .height(100.dp)
                                        .width(20.dp)
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    date,
                                    fontSize = 12.sp,
                                    color = Color.Gray
                                )
                            }
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
@Preview(showBackground = true)
fun HistoryScreenPrev() {
    MainScreen(navController = rememberNavController())
}