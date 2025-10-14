package com.alyak.detector.ui.family

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.alyak.detector.R
import com.alyak.detector.ui.other.PeriodToggle
import com.alyak.detector.ui.other.StatusBadge

@Composable
fun HistoryBox(
    modifier: Modifier = Modifier.fillMaxWidth()
) {
    Column {
        Row {
            Text(
                "복약기록",
                modifier = Modifier
                    .weight(0.5f)
                    .padding(4.dp),
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.SansSerif,
                fontSize = 20.sp,
                textAlign = TextAlign.Center
            )
            PeriodToggle(
                selected = "전체",
                onSelectedChange = {},
                list = listOf("전체", "완료", "미복용", "지연")
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
                colorResource(R.color.primaryBlue).copy(alpha = 0.6f),
                colorResource(R.color.primaryBlue)
            )
        }

        // 복용 일정 박스


    }


}

@Composable
@Preview(showBackground = true)
fun HistoryBoxPrev() {
    HistoryBox(Modifier)
}