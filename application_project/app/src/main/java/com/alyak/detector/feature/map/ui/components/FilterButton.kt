package com.alyak.detector.feature.map.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.alyak.detector.R
import com.alyak.detector.ui.components.StatusBadge

@Composable
fun FilterButton(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    selectedBackgroundColor: Color = colorResource(R.color.primaryBlue),
    unselectedBackgroundColor: Color = colorResource(R.color.white),
    selectedTextColor: Color = colorResource(R.color.white),
    unselectedTextColor: Color = colorResource(R.color.primaryBlue),
    borderColor: Color = unselectedTextColor,
    icon: Painter? = null
) {
    val currentBackgroundColor = if (isSelected) selectedBackgroundColor else unselectedBackgroundColor
    val currentTextColor = if (isSelected) selectedTextColor else unselectedTextColor
    val borderStroke = if (!isSelected) {
        BorderStroke(width = 1.dp, color = borderColor)
    } else {
        null
    }
    Box(
        modifier = Modifier
            .border(border = borderStroke ?: BorderStroke(0.dp, Color.Transparent), shape = CircleShape)
            .clip(CircleShape)
            .clickable(onClick = onClick)
    ) {
        StatusBadge(
            text = text,
            icon = icon,
            backgroundColor = currentBackgroundColor,
            textColor = currentTextColor
        )
    }
}


@Preview(showBackground = true, name = "클릭 테스트 (인터랙티브)")
@Composable
fun FilterButtonInteractivePreview() {
    var isSelected1 by remember { mutableStateOf(false) }
    var isSelected2 by remember { mutableStateOf(true) }

    Row(
        modifier = Modifier.padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        FilterButton(
            text = "클릭해보세요",
            isSelected = isSelected1,
            onClick = { isSelected1 = !isSelected1 }
        )

        FilterButton(
            text = "체크박스",
            isSelected = isSelected2,
            icon = rememberVectorPainter(Icons.Default.Check),
            onClick = { isSelected2 = !isSelected2 }
        )
    }
}