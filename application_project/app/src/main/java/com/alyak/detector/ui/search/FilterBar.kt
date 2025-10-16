package com.alyak.detector.ui.search

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.alyak.detector.R
import com.alyak.detector.ui.components.CustomButton

@Composable
fun <E : Enum<E>> FilterBar(
    entries: List<E>,
    labelSelector: (E) -> String,
    iconSelector: @Composable (E) -> Painter,
    onItemClick: (E) -> Unit = {},
    modifier: Modifier = Modifier.height(80.dp)
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(
                rememberScrollState()
            )
    ) {
        for (item in entries) {
            CustomButton(
                labelSelector(item),
                onClick = { onItemClick(item) },
                image = iconSelector(item),
                contentDescription = null,
                containerColor = colorResource(R.color.white),
                contentColor = colorResource(R.color.primaryBlue),
                textColor = colorResource(R.color.black),
                modifier = Modifier
                    .height(70.dp)
                    .clip(RoundedCornerShape(24))
            )
        }
    }
}

@Composable
@Preview(showBackground = true)
fun FilterBarPrev() {
    FilterBar(
        entries = PillShapeType.entries,
        labelSelector = { it.label },
        iconSelector = { rememberVectorPainter(it.icon) },
        onItemClick = {},
        modifier = Modifier
    )
}
