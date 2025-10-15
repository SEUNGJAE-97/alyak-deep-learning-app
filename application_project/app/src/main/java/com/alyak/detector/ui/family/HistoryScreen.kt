package com.alyak.detector.ui.family

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
import com.alyak.detector.R
import com.alyak.detector.ui.components.ContentBox
import com.alyak.detector.ui.other.BottomForm
import com.alyak.detector.ui.other.HeaderForm

@Composable
fun HistoryScreen(
    modifier: Modifier = Modifier.background(Color.White)
) {
    val icons = listOf(
        Icons.Filled.Home,
        Icons.Filled.DateRange,
        Icons.Filled.FavoriteBorder,
        Icons.Filled.Settings
    )
    var selectedIndex by remember { mutableStateOf(0) }

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
                FamilyMemberBtn(role = "할머니", name = "김싸피", isSelected = true)
                FamilyMemberBtn(role = "할아버지", name = "하싸피", isSelected = false)
                FamilyMemberBtn(role = "아버지", name = "하하하", isSelected = false)
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
                            "78%",
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
                                "복용 완료: 18",
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
                                "미복용: 3",
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
                                "지연 복용: 2",
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
                                "예정: 5",
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
                ScheduleBox()

                // 복약 기록 박스
                Spacer(modifier = Modifier.height(10.dp))

                HistoryBox()
            }
        }
    }
}


@Composable
@Preview(showBackground = true)
fun HistoryScreenPrev() {
    HistoryScreen()
}