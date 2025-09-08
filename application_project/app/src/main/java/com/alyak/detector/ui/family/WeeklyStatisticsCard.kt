package com.alyak.detector.ui.family

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// 데이터 클래스
data class WeeklyStatsData(
    val percent: Int,
    val complete: Int,
    val missed: Int,
    val natural: Int,
    val planned: Int,
    val weeklyPattern: List<DayPattern>
)

data class DayPattern(
    val dateLabel: String,
    val complete: Int,
    val missed: Int,
    val natural: Int
)

// 전용 컴포저블
@Composable
fun WeeklyStatisticsCard(
    data: WeeklyStatsData,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("주간", "월간")

    Card(
        shape = RoundedCornerShape(24.dp),
        modifier = modifier.padding(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(24.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "이번 주 복약 현황",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                Row {
                    tabs.forEachIndexed { index, title ->
                        val selected = selectedTab == index
                        Text(
                            text = title,
                            modifier = Modifier
                                .clickable { selectedTab = index }
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                                .background(
                                    color = if (selected) Color(0xFF5453DD) else Color(0xFFE5E5E5),
                                    shape = RoundedCornerShape(50)
                                ),
                            color = if (selected) Color.White else Color.Black,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                DonutChart(
                    modifier = Modifier.size(120.dp),
                    data = mapOf(
                        Color(0xFF5453DD) to data.complete,
                        Color(0xFFFF7242) to data.missed,
                        Color(0xFFE2A726) to data.natural,
                        Color(0xFFD7D7D7) to data.planned
                    )
                )
                Spacer(modifier = Modifier.width(24.dp))
                Column {
                    Text(
                        text = "${data.percent}%",
                        fontSize = 36.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF5453DD)
                    )
                    Text(text = "복약 성공률", fontSize = 14.sp, color = Color.Gray)
                    Spacer(modifier = Modifier.height(8.dp))
                    LegendItem(Color(0xFF5453DD), "복용 완료: ${data.complete}")
                    LegendItem(Color(0xFFFF7242), "미복용: ${data.missed}")
                    LegendItem(Color(0xFFE2A726), "지연 복용: ${data.natural}")
                    LegendItem(Color(0xFFD7D7D7), "예정: ${data.planned}")
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(text = "최근 7일 복약 패턴", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            WeeklyPatternBarChart(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
                data = data.weeklyPattern
            )
        }
    }
}

@Composable
fun LegendItem(color: Color, text: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(vertical = 2.dp)
    ) {
        Box(
            modifier = Modifier
                .size(12.dp)
                .background(color = color, shape = RoundedCornerShape(3.dp))
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(text = text, fontSize = 14.sp)
    }
}

@Composable
fun DonutChart(modifier: Modifier = Modifier, data: Map<Color, Int>) {
    val total = data.values.sum()
    val proportions = data.values.map { it.toFloat() / total }
    val colors = data.keys.toList()

    Canvas(modifier = modifier) {
        val strokeWidth = size.minDimension / 8
        var startAngle = -90f
        for (i in proportions.indices) {
            val sweepAngle = 360 * proportions[i]
            drawArc(
                color = colors[i],
                startAngle = startAngle,
                sweepAngle = sweepAngle,
                useCenter = false,
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
            )
            startAngle += sweepAngle
        }
    }
}

@Composable
fun WeeklyPatternBarChart(modifier: Modifier = Modifier, data: List<DayPattern>) {
    val maxCount = (data.maxOfOrNull { it.complete + it.missed + it.natural } ?: 1).toFloat()
    val blue = Color(0xFF5453DD)
    val red = Color(0xFFFF7242)
    val yellow = Color(0xFFE2A726)

    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Bottom
    ) {
        data.forEach { day ->
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Bottom,
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .height(((day.complete / maxCount) * 80f).dp)
                        .fillMaxWidth()
                        .background(blue)
                )
                Box(
                    modifier = Modifier
                        .height(((day.missed / maxCount) * 80f).dp)
                        .fillMaxWidth()
                        .background(red)
                )
                Box(
                    modifier = Modifier
                        .height(((day.natural / maxCount) * 80f).dp)
                        .fillMaxWidth()
                        .background(yellow)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = day.dateLabel, fontSize = 12.sp)
            }
        }
    }
}

@Composable
@Preview(showBackground = true)
fun WeeklyPatternBarPrev() {
    val sampleData = WeeklyStatsData(
        percent = 78,
        complete = 18,
        missed = 3,
        natural = 2,
        planned = 5,
        weeklyPattern = listOf(
            DayPattern("5/30", 5, 0, 0),
            DayPattern("5/31", 5, 0, 0),
            DayPattern("6/1", 3, 2, 0),
            DayPattern("6/2", 5, 0, 0),
            DayPattern("6/3", 5, 0, 0),
            DayPattern("6/4", 3, 2, 0),
            DayPattern("6/5", 3, 0, 2)
        )
    )
    WeeklyPatternBarChart(
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp),
        data = sampleData.weeklyPattern
    )
}
