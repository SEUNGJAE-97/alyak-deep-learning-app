package com.alyak.detector.feature.pill.ui.search.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.alyak.detector.R
import com.alyak.detector.feature.pill.data.model.Pill

@Composable
fun RecentSearch(
    recentPills: List<Pill>,
    onItemClick: (Pill) -> Unit
) {
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
                Text("최근 검색", fontWeight = FontWeight.Bold, fontSize = 20.sp)
                if (recentPills.isNotEmpty()) {
                    Text(
                        "전체보기",
                        fontWeight = FontWeight.Thin,
                        color = colorResource(R.color.primaryBlue),
                        // modifier = Modifier.clickable { /* 전체보기 클릭 동작 */ }
                    )
                }
            }
            if (recentPills.isEmpty()) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 250.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_launcher_foreground),
                        contentDescription = null,
                        modifier = Modifier
                            .size(120.dp)
                            .padding(bottom = 16.dp)
                    )
                    Text(
                        text = "최근 검색 기록이 없어요.",
                        fontSize = 16.sp,
                        color = Color.Gray
                    )
                }
            } else {
                Column(
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    recentPills.forEach { pill ->
                        PillInfoBox(pillInfo = pill)
                    }
                }
            }
        }
    }
}

@Composable
@Preview(showBackground = true)
fun QuickSearchPrev() {
    val samplePills = listOf(
        Pill("타이레놀 500mg", "아세트아미노펜", "한국얀센", "일반약", "TYLENOL"),
        Pill("이지엔6애니", "이부프로펜", "대웅제약", "일반약", "EZN6"),
        Pill("게보린정", "아세트아미노펜", "삼진제약", "일반약", "GEVORIN")
    )
    RecentSearch(
        recentPills = samplePills,
        onItemClick = { }
    )
}