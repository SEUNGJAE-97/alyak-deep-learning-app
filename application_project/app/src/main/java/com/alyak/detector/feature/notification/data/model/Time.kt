package com.alyak.detector.feature.notification.data.model

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.NightsStay
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material.icons.filled.WbTwilight
import androidx.compose.ui.graphics.vector.ImageVector
import com.alyak.detector.R

/**
 * 시간대 정보를 포함하는 클래스 (아침, 점심, 저녁)
 * */
enum class MealTime(val icon: ImageVector, val backgroundColor: Int, val tint: Int) {
    MORNING(Icons.Default.WbTwilight, R.color.Orange, R.color.kakaoYellow),
    LUNCH(Icons.Default.WbSunny, R.color.kakaoYellow, R.color.Orange),
    DINNER(Icons.Default.NightsStay, R.color.main_blue, R.color.point_blue)
}

data class MedicationTimeEntry(
    val id: Int,
    val mealTime: MealTime,
    val hour: Int,
    val minute: Int,
) {
    val displayTime: String
        get() {
            val label = when (mealTime) {
                MealTime.MORNING -> "아침"
                MealTime.LUNCH -> "점심"
                MealTime.DINNER -> "저녁"
            }
            return "$label %02d:%02d".format(hour, minute)
        }
}