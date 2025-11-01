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
import com.alyak.detector.data.family.repository.FamilyRepository
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
import com.alyak.detector.ui.main.components.StatusRow

@Composable
fun MainScreen(
    navController: NavController,
    modifier: Modifier = Modifier.background(Color.White),
    viewModel: MainViewModel = hiltViewModel()
) {
    val familyMembers = viewModel.familyMembers
    var selectedIndex by remember { mutableIntStateOf(viewModel.selectedIndex) }

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
                            DonutSegment(0.55f, colorResource(R.color.primaryBlue)),   // 파랑
                            DonutSegment(0.15f, Color(0xFFD6D9DE)),   // 회색(연함)
                            DonutSegment(0.13f, colorResource(R.color.Orange)),   // 주황
                            DonutSegment(0.17f, colorResource(R.color.RealRed))    // 빨강
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
                    //TODO : 최근 7일 복약 패턴 그래프 데이터 입력 받아야함
                    val barDataList = listOf(
                        listOf(BarSegment(1f, colorResource(R.color.primaryBlue))), // 5/30: 성공
                        listOf(BarSegment(1f, colorResource(R.color.primaryBlue))), // 5/31: 성공
                        listOf(
                            BarSegment(0.4f, colorResource(R.color.RealRed)),
                            BarSegment(0.6f, colorResource(R.color.primaryBlue))
                        ), // 6/1: 미복용+성공
                        listOf(BarSegment(1f, colorResource(R.color.primaryBlue))), // 6/2: 성공
                        listOf(BarSegment(1f, colorResource(R.color.primaryBlue))), // 6/3: 성공
                        listOf(
                            BarSegment(0.2f, colorResource(R.color.RealRed)),
                            BarSegment(0.8f, colorResource(R.color.primaryBlue))
                        ), // 6/4: 미복용+완료
                        listOf(
                            BarSegment(0.4f, colorResource(R.color.Orange)),
                            BarSegment(0.6f, colorResource(R.color.primaryBlue))
                        ) // 6/5: 지연+완료
                    )

                    val barDataWithDates = listOf(
                        Pair(listOf(BarSegment(1f, colorResource(R.color.primaryBlue))), "5/30"),
                        Pair(listOf(BarSegment(1f, colorResource(R.color.primaryBlue))), "5/31"),
                        Pair(
                            listOf(
                                BarSegment(0.4f, colorResource(R.color.RealRed)),
                                BarSegment(0.6f, colorResource(R.color.primaryBlue))
                            ), "6/1"
                        ),
                        Pair(listOf(BarSegment(1f, colorResource(R.color.primaryBlue))), "6/2"),
                        Pair(listOf(BarSegment(1f, colorResource(R.color.primaryBlue))), "6/3"),
                        Pair(
                            listOf(
                                BarSegment(0.2f, colorResource(R.color.RealRed)),
                                BarSegment(0.8f, colorResource(R.color.primaryBlue))
                            ), "6/4"
                        ),
                        Pair(
                            listOf(
                                BarSegment(0.4f, colorResource(R.color.Orange)),
                                BarSegment(0.6f, colorResource(R.color.primaryBlue))
                            ), "6/5"
                        )
                    )

                    Row(
                        Modifier
                            .fillMaxWidth()
                            .padding(8.dp, vertical = 4.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.Bottom
                    ) {
                        barDataWithDates.forEach { (segments, date) ->
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
@Preview(showBackground = true, heightDp = 2000)
fun HistoryScreenPrev() {
    MainScreen(
        navController = rememberNavController()
    )
}