package com.alyak.detector.feature.pill.ui.search.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.asAndroidPath
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.dp
import com.alyak.detector.feature.pill.data.model.PillLineType
import com.alyak.detector.feature.pill.data.model.PillShapeType
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun ShapeIcon(shapeType: PillShapeType, modifier: Modifier = Modifier) {
    Canvas(modifier = modifier.size(24.dp)) {
        val minDimension = size.minDimension
        val finalSize = minDimension * 0.8f
        val topLeftOffset = Offset(
            (size.width - finalSize) / 2,
            (size.height - finalSize) / 2
        )
        val centerPoint = Offset(size.width / 2, size.height / 2)

        // 그림자 설정
        val shadowColor = Color.Gray.copy(alpha = 0.5f).toArgb()
        val shadowRadius = 3.dp.toPx()
        val shadowOffset = 1.dp.toPx()

        val paint = Paint().asFrameworkPaint().apply {
            color = android.graphics.Color.WHITE
            isAntiAlias = true
            setShadowLayer(shadowRadius, shadowOffset, shadowOffset, shadowColor)
        }

        val path = Path()

        when (shapeType) {
            PillShapeType.ROUND -> {
                path.addOval(Rect(topLeftOffset, Size(finalSize, finalSize)))
            }

            PillShapeType.OVAL -> {
                val ovalHeight = finalSize * 0.65f
                val ovalTop = centerPoint.y - (ovalHeight / 2)

                path.addOval(
                    Rect(
                        left = topLeftOffset.x,
                        top = ovalTop,
                        right = topLeftOffset.x + finalSize,
                        bottom = ovalTop + ovalHeight
                    )
                )
            }

            PillShapeType.SQUARE -> {
                path.addRect(Rect(topLeftOffset, Size(finalSize, finalSize)))
            }

            PillShapeType.CAPSULE -> {
                val capsuleHeight = finalSize * 0.6f
                val capsuleTop = centerPoint.y - (capsuleHeight / 2)

                path.addRoundRect(
                    RoundRect(
                        rect = Rect(
                            left = topLeftOffset.x,
                            top = capsuleTop,
                            right = topLeftOffset.x + finalSize,
                            bottom = capsuleTop + capsuleHeight
                        ),
                        cornerRadius = CornerRadius(capsuleHeight / 2, capsuleHeight / 2)
                    )
                )
            }

            PillShapeType.HALF_ROUND -> {
                val halfHeight = finalSize / 2
                val verticalStart = centerPoint.y - (halfHeight / 2)

                path.moveTo(topLeftOffset.x, centerPoint.y + halfHeight / 2)
                path.arcTo(
                    rect = Rect(
                        left = topLeftOffset.x,
                        top = verticalStart,
                        right = topLeftOffset.x + finalSize,
                        bottom = verticalStart + finalSize
                    ),
                    startAngleDegrees = 180f,
                    sweepAngleDegrees = 180f,
                    forceMoveTo = false
                )
                path.close()
            }

            PillShapeType.TRIANGLE -> path.addPolygonPath(3, finalSize / 2, centerPoint)
            PillShapeType.DIAMOND -> path.addPolygonPath(4, finalSize / 2, centerPoint, rotate = 0f)
            PillShapeType.PENTAGON -> path.addPolygonPath(5, finalSize / 2, centerPoint)
            PillShapeType.HEXAGON -> path.addPolygonPath(6, finalSize / 2, centerPoint)
            PillShapeType.OCTAGON -> path.addPolygonPath(8, finalSize / 2, centerPoint)

            else -> {
                path.addOval(Rect(topLeftOffset, Size(finalSize, finalSize)))
            }
        }

        drawIntoCanvas { canvas ->
            canvas.nativeCanvas.drawPath(path.asAndroidPath(), paint)
        }
    }
}

fun Path.addPolygonPath(
    sides: Int,
    radius: Float,
    center: Offset,
    rotate: Float = -90f
): Path {
    val angleStep = 2 * Math.PI / sides
    val startAngle = Math.toRadians(rotate.toDouble())

    for (i in 0 until sides) {
        val angle = startAngle + i * angleStep
        val x = center.x + radius * cos(angle).toFloat()
        val y = center.y + radius * sin(angle).toFloat()
        if (i == 0) moveTo(x, y) else lineTo(x, y)
    }
    close()
    return this
}

fun RoundRect(rect: Rect, cornerRadius: CornerRadius): androidx.compose.ui.geometry.RoundRect {
    return androidx.compose.ui.geometry.RoundRect(
        left = rect.left, top = rect.top, right = rect.right, bottom = rect.bottom,
        cornerRadius = cornerRadius
    )
}

@Composable
fun MarkingIcon(type: PillLineType, modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val shadowColor = android.graphics.Color.LTGRAY
        val paint = Paint().asFrameworkPaint().apply {
            color = android.graphics.Color.WHITE
            setShadowLayer(3.dp.toPx(), 1.dp.toPx(), 1.dp.toPx(), shadowColor)
        }

        val drawSize = size.minDimension * 0.8f
        val centerOffset = center

        drawIntoCanvas {
            it.nativeCanvas.drawCircle(
                centerOffset.x,
                centerOffset.y,
                drawSize / 2,
                paint
            )
        }
        val strokeWidth = 1.5.dp.toPx()
        val lineLength = drawSize * 0.6f
        val lineColor = Color.LightGray

        when (type) {
            PillLineType.PLUS -> {
                drawLine(
                    color = lineColor,
                    start = center.copy(x = center.x - lineLength / 2),
                    end = center.copy(x = center.x + lineLength / 2),
                    strokeWidth = strokeWidth,
                    cap = StrokeCap.Round
                )
                drawLine(
                    color = lineColor,
                    start = center.copy(y = center.y - lineLength / 2),
                    end = center.copy(y = center.y + lineLength / 2),
                    strokeWidth = strokeWidth,
                    cap = StrokeCap.Round
                )
            }

            PillLineType.MINUS -> {
                drawLine(
                    color = lineColor,
                    start = center.copy(x = center.x - lineLength / 2),
                    end = center.copy(x = center.x + lineLength / 2),
                    strokeWidth = strokeWidth,
                    cap = StrokeCap.Round
                )
            }

            PillLineType.NONE,
            PillLineType.ALL,
            PillLineType.ETC -> {
                /* 아무것도 그리지 않음 */
            }
        }
    }
}