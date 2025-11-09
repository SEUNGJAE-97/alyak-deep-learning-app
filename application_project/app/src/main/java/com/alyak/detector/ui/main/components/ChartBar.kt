    package com.alyak.detector.ui.main.components

    import androidx.compose.foundation.Canvas
    import androidx.compose.foundation.layout.*
    import androidx.compose.runtime.Composable
    import androidx.compose.ui.Modifier
    import androidx.compose.ui.graphics.Color
    import androidx.compose.ui.res.colorResource
    import androidx.compose.ui.unit.dp
    import androidx.compose.ui.tooling.preview.Preview
    import com.alyak.detector.R
    import com.alyak.detector.data.family.model.DailyMedicationStat

    data class BarSegment(
        val ratio: Float,
        val color: Color
    )

    @Composable
    fun ChartBar(
        modifier: Modifier = Modifier,
        segments: List<BarSegment>
    ) {
        Box(
            modifier = modifier
                .width(30.dp)
                .height(120.dp)
        ) {
            Canvas(modifier = Modifier.matchParentSize()) {
                var topY = 0f
                val totalHeightPx = size.height
                segments.forEach { segment ->
                    val segmentHeight = totalHeightPx * segment.ratio
                    drawRect(
                        color = segment.color,
                        topLeft = androidx.compose.ui.geometry.Offset(0f, topY),
                        size = androidx.compose.ui.geometry.Size(size.width, segmentHeight)
                    )
                    topY += segmentHeight
                }
            }
        }
    }

    /**
     * DailyMedicationStat을 BarSegments로 변환
     */
    @Composable
    fun dailyStatToBarSegments(stat : DailyMedicationStat): List<BarSegment>{
        val segments = mutableListOf<BarSegment>()
        if (stat.missedRatio > 0f)
            segments.add(BarSegment(stat.missedRatio, colorResource(R.color.RealRed)))
        if (stat.delayedRatio > 0f)
            segments.add(BarSegment(stat.delayedRatio, colorResource(R.color.Orange)))
        if (stat.successRatio > 0f)
            segments.add(BarSegment(stat.successRatio, colorResource(R.color.primaryBlue)))
        return segments
    }

    @Composable
    @Preview(showBackground = true)
    fun ChartBarPrev() {
        ChartBar(
            segments = listOf(
                BarSegment(0.3f, colorResource(R.color.RealRed)),
                BarSegment(0.7f, colorResource(R.color.primaryBlue))
            )
        )

        ChartBar(
            segments = listOf(
                BarSegment(0.1f, colorResource(R.color.Orange)),
                BarSegment(0.2f, colorResource(R.color.RealRed)),
                BarSegment(0.7f, colorResource(R.color.primaryBlue))
            )
        )
    }
