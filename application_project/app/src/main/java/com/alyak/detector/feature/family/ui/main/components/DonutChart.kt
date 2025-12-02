package com.alyak.detector.feature.family.ui.main.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import com.alyak.detector.R

data class DonutSegment(val ratio: Float, val color: Color)

@Composable
fun DonutChart(
    segments: List<DonutSegment>,
    modifier: Modifier = Modifier,
    strokeWidth: Float = 70f
) {
    Canvas(modifier = modifier) {
        var startAngle = -90f
        segments.forEach { segment ->
            val sweepAngle = 360f * segment.ratio
            drawArc(
                color = segment.color,
                startAngle = startAngle,
                sweepAngle = sweepAngle,
                useCenter = false,
                style = androidx.compose.ui.graphics.drawscope.Stroke(
                    width = strokeWidth,
                    cap = StrokeCap.Butt
                )
            )
            startAngle += sweepAngle
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DonutChartPreview() {
    Row(
        Modifier
            .fillMaxWidth()
            .padding(24.dp),
        horizontalArrangement = Arrangement.Center
    ) {
        DonutChart(
            segments = listOf(
                DonutSegment(0.55f, colorResource(R.color.primaryBlue)),   // 파랑
                DonutSegment(0.15f, Color(0xFFD6D9DE)),   // 회색(연함)
                DonutSegment(0.13f, colorResource(R.color.Orange)),   // 주황
                DonutSegment(0.17f, colorResource(R.color.RealRed))    // 빨강
            ),
            modifier = Modifier.size(180.dp)
        )
    }
}
