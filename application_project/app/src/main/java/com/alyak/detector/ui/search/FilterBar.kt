package com.alyak.detector.ui.search

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.tooling.preview.Preview
import com.alyak.detector.R
import com.alyak.detector.ui.components.CustomButton

@Composable
fun FilterBar() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentSize()
            .horizontalScroll(
                rememberScrollState()
            )
    ) {
        for (shape in PillShapeType.entries) {
            CustomButton(
                shape.label,
                onClick = {},
                image = null,
                contentDescription = null,
                textColor = colorResource(R.color.white),
            )
        }
    }
}

@Composable
@Preview(showBackground = true)
fun FilterBarPrev() {
    FilterBar()
}