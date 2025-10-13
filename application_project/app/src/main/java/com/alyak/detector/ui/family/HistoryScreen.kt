package com.alyak.detector.ui.family

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
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
import androidx.compose.ui.graphics.Brush
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
                        ), // 6/4: 미복용+성공
                        listOf(
                            BarSegment(0.4f, colorResource(R.color.Orange)),
                            BarSegment(0.6f, colorResource(R.color.primaryBlue))
                        ) // 6/5: 지연+성공
                    )

                    barDataList.forEach { segments ->
                        ChartBar(
                            segments = segments,
                            modifier = Modifier
                                .padding(horizontal = 2.dp)
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .padding(horizontal = 17.dp, 2.dp)
                        .fillMaxWidth()
                ) {
                    Text(
                        "5/30",
                    )
                }


                // 일정알림박스
                ContentBox(
                    Modifier
                        .fillMaxWidth()
                        .height(110.dp)
                        .background(
                            brush = Brush.horizontalGradient(
                                colors = listOf(
                                    Color(0xFFF4F3FA),
                                    Color(0xFFF9F7FB)
                                )
                            ),
                            shape = RoundedCornerShape(40.dp)
                        )
                        .padding(20.dp)
                ) {
                    Row(
                        Modifier.fillMaxSize(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
// 왼쪽: 아이콘 + 약 정보
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            // 체크 동그라미
                            Box(
                                Modifier
                                    .size(56.dp)
                                    .background(Color.White, shape = CircleShape)
                                    .border(2.dp, Color(0xFF7262FD), shape = CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = null,
                                    tint = Color(0xFF7262FD),
                                    modifier = Modifier.size(32.dp)
                                )
                            }
                            Spacer(Modifier.width(16.dp))
                            // 텍스트 정보
                            Column {
                                Text(
                                    "다음 복용 예정",
                                    fontSize = 14.sp,
                                    color = Color.Black.copy(alpha = 0.7f)
                                )
                                Text(
                                    "오늘 저녁 7시",
                                    fontSize = 22.sp,
                                    color = Color.Black,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(bottom = 8.dp)
                                )
                                Text(
                                    "고혈압약 아모잘탄",
                                    fontSize = 16.sp,
                                    color = Color.Black,
                                    fontWeight = FontWeight.Medium
                                )
                                Text(
                                    "1정, 식후 30분",
                                    fontSize = 12.sp,
                                    color = Color.Gray
                                )
                            }
                        }

                        Spacer(modifier = Modifier.weight(1f))
                        // 오른쪽: 남은 시간/버튼
                        Column(
                            horizontalAlignment = Alignment.End,
                            verticalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.padding(start = 12.dp)
                        ) {
                            Text(
                                "남은 시간",
                                fontSize = 14.sp,
                                color = Color.Black.copy(alpha = 0.7f)
                            )
                            Text(
                                "3시간 20분",
                                fontSize = 22.sp,
                                color = Color(0xFF4E4BFB),
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(Modifier.height(12.dp))
                            // 알림 설정 버튼
                            Button(
                                onClick = { /*TODO*/ },
                                colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                                elevation = ButtonDefaults.buttonElevation(0.dp),
                                shape = RoundedCornerShape(20.dp),
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 2.dp),
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Alarm,   // 필요시 custom 아이콘
                                    contentDescription = null,
                                    tint = Color(0xFF4E4BFB),
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(Modifier.width(6.dp))
                                Text("알림 설정", color = Color(0xFF4E4BFB), fontSize = 13.sp)
                            }
                        }
                    }
                }
            }
        }
    }
}


@Composable
@Preview(showBackground = true)
fun HistoryScreenPrev() {
    HistoryScreen()
}