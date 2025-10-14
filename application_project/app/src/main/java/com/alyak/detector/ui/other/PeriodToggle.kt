package com.alyak.detector.ui.other

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun PeriodToggle(
    selected: String,
    onSelectedChange: (String) -> Unit,
    list: List<String>
) {
    val options = list
    Row(
        modifier = Modifier
            .background(Color(0xFFF2F2F5), shape = RoundedCornerShape(20.dp))
            .padding(4.dp) // 바깥 테두리 여백
    ) {
        options.forEach { label ->
            val isSelected = selected == label
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = if (isSelected) Color(0xFF6F5CF1) else Color.Transparent,
                modifier = Modifier
                    .padding(horizontal = 4.dp)
                    .height(36.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .clickable { onSelectedChange(label) }
            ) {
                Box(
                    modifier = Modifier
                        .padding(horizontal = 16.dp, vertical = 4.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = label,
                        color = if (isSelected) Color.White else Color.Black,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                    )
                }
            }
        }
    }
}


@Composable
@Preview(showBackground = true)
fun PeriodTogglePrev() {
    PeriodToggle(selected = "주간", onSelectedChange = {}, list = listOf("주간", "월간", "연간"))
}
