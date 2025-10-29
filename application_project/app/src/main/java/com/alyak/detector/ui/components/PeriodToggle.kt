package com.alyak.detector.ui.components

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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.alyak.detector.R

@Composable
fun PeriodToggle(
    selected: String,
    onSelectedChange: (String) -> Unit,
    list: List<String>,
    modifier: Modifier
) {
    val options = list
    Row(
        modifier = Modifier
            .shadow(1.dp, shape = RoundedCornerShape(16.dp))
            .background(colorResource(R.color.white), shape = RoundedCornerShape(16.dp))
            .height(24.dp)
            .padding(1.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        options.forEach { label ->
            val isSelected = selected == label
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = if (isSelected) Color(0xFF6F5CF1) else Color.Transparent,
                modifier = Modifier
                    .padding(horizontal = 2.dp)
                    .height(20.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .clickable { onSelectedChange(label) }
            ) {
                Box(
                    modifier = Modifier
                        .padding(horizontal = 6.dp, vertical = 0.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = label,
                        fontSize = 13.sp,
                        color = if (isSelected) Color.White else Color.Black,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        maxLines = 1
                    )
                }
            }
        }
    }
}


@Composable
@Preview(showBackground = true)
fun PeriodTogglePrev() {
    PeriodToggle(
        selected = "주간",
        onSelectedChange = {},
        list = listOf("주간", "월간", "연간"),
        modifier = Modifier
    )
}
