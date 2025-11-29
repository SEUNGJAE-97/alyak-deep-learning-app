package com.alyak.detector.feature.pill.ui.search.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.alyak.detector.R
import com.alyak.detector.feature.pill.data.model.PillShapeType

@Composable
fun <E : Enum<E>> FilterBar(
    entries: List<E>,
    selectedItem: E,
    labelSelector: (E) -> String,
    iconSelector: @Composable (E) -> Unit,
    onItemClick: (E) -> Unit = {},
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp)
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        for (item in entries) {
            val isSelected = (item == selectedItem)
            val backgroundColor = if (isSelected) colorResource(R.color.primaryBlue) else colorResource(R.color.white) // 파란색/흰색
            val contentColor = if (isSelected) colorResource(R.color.white) else colorResource(R.color.black)
            val shadowModifier = if (isSelected) Modifier else Modifier.shadow(2.dp, shape = RoundedCornerShape(24.dp))

            Box(
                modifier = Modifier
                    .then(shadowModifier)
                    .shadow(2.dp, shape = RoundedCornerShape(24.dp))
                    .clip(RoundedCornerShape(24.dp))
                    .background(backgroundColor)
                    .clickable { onItemClick(item) }
                    .padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    iconSelector(item)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = labelSelector(item),
                        color = contentColor,
                        fontSize = 14.sp
                    )
                }
            }
        }
    }
}

@Composable
@Preview(showBackground = true)
fun FilterBarPrev() {
    FilterBar(
        entries = PillShapeType.entries,
        selectedItem = PillShapeType.entries.first(), // 첫 번째(전체) 선택된 상태
        labelSelector = { it.label },
        iconSelector = { item ->
            if (item != PillShapeType.ALL && item != PillShapeType.ETC) {
                ShapeIcon(
                    shapeType = item,
                    modifier = Modifier.size(20.dp)
                )
            }
        },
        onItemClick = {},
        modifier = Modifier
    )
}
