package com.alyak.detector.ui.main

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.alyak.detector.R
import com.alyak.detector.ui.components.PeriodToggle
import com.alyak.detector.ui.components.StatusBadge

@Composable
fun HistoryBox(
    modifier: Modifier = Modifier.fillMaxWidth()
) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "복약기록",
                modifier = Modifier
                    .padding(4.dp)
                    .weight(1f),
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.SansSerif,
                fontSize = 20.sp,
                textAlign = TextAlign.Start
            )


            PeriodToggle(
                selected = "전체",
                onSelectedChange = {},
                list = listOf("전체", "완료", "미복용", "지연"),
                modifier = Modifier.wrapContentWidth()
            )
        }

        // 아침, 점심, 저녁
        Row(
            modifier
                .align(Alignment.Start)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("오늘 (2025. 10. 14)", fontSize = 20.sp, modifier = Modifier.padding(10.dp))
            StatusBadge(
                "목요일",
                null,
                colorResource(R.color.primaryBlue).copy(alpha = 0.6f),
                colorResource(R.color.primaryBlue)
            )
        }

        // 복용 일정 박스
        val doseHistory = listOf(
            Triple("아침", "복용", "08:30"),
            Triple("점심", "미복용", "12:00"),
            Triple("저녁", "복용", "19:00")
        )

        for (item in doseHistory) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
                    .shadow(elevation = 2.dp, shape = RoundedCornerShape(20.dp))
                    .background(
                        colorResource(R.color.white),
                        shape = RoundedCornerShape(20.dp)
                    )
            ) {
                DoseStatusItem(item.first, item.second, item.third)
            }
        }
    }


}

@Composable
@Preview(showBackground = true)
fun HistoryBoxPrev() {
    HistoryBox(Modifier)
}