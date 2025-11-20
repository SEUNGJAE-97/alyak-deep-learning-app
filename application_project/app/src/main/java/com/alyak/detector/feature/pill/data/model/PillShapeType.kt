package com.alyak.detector.feature.pill.data.model

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Circle
import androidx.compose.ui.graphics.vector.ImageVector

enum class PillShapeType(val label: String, val icon: ImageVector) {
    ROUND("원형", Icons.Default.Circle),
    OVAL("타원형", Icons.Default.Circle),
    CAPSULE("장방형", Icons.Default.Circle),
    HALF_ROUND("반원형", Icons.Default.Circle),
    TRIANGLE("삼각형", Icons.Default.Circle),
    SQUARE("사각형", Icons.Default.Circle),
    DIAMOND("마름모형", Icons.Default.Circle),
    PENTAGON("오각형", Icons.Default.Circle),
    HEXAGON("육각형", Icons.Default.Circle),
    OCTAGON("팔각형", Icons.Default.Circle),
    ALL("전체", Icons.Default.Circle),
    ETC("기타", Icons.Default.Circle)
}

