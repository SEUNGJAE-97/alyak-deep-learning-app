package com.alyak.detector.ui.search

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.alyak.detector.R
import com.alyak.detector.data.dto.pill.Pill
import com.alyak.detector.ui.other.PillInfoBox

@Composable
fun QuickSearch() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("빠른 검색", fontWeight = FontWeight.Bold, fontSize = 20.sp)
                Text(
                    "더보기",
                    fontWeight = FontWeight.Thin,
                    color = colorResource(R.color.primaryBlue)
                )
            }

            FilterBar(
                entries = PillShapeType.entries,
                labelSelector = { it.label },
                iconSelector = { rememberVectorPainter(it.icon) },
                onItemClick = { },
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("최근 검색", fontWeight = FontWeight.Bold, fontSize = 20.sp)
                Text(
                    "전체보기",
                    fontWeight = FontWeight.Thin,
                    color = colorResource(R.color.primaryBlue)
                )
            }

            // 최근 검색 알약 정보
            PillInfoBox(Pill("타이레놀 500mg", "아세트아미노펜", "한국얀센", "일반약", "TYLENOL"))
            PillInfoBox(Pill("타이레놀 500mg", "아세트아미노펜", "한국얀센", "일반약", "TYLENOL"))
            PillInfoBox(Pill("타이레놀 500mg", "아세트아미노펜", "한국얀센", "일반약", "TYLENOL"))


        }
    }

}

@Composable
@Preview(showBackground = true)
fun QuickSearchPrev() {
    QuickSearch()
}