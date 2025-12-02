package com.alyak.detector.core.model

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.NightsStay
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material.icons.filled.WbTwilight
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.colorResource
import com.alyak.detector.R

/**
 * 요일 정보를 포함 하는 클래스
 * */
enum class Week(val dayName: String) {
    MONDAY("월요일"),
    TUESDAY("화요일"),
    WEDNESDAY("수요일"),
    THURSDAY("목요일"),
    FRIDAY("금요일"),
    SATURDAY("토요일"),
    SUNDAY("일요일"),
}

/**
 * 시간대 정보를 포함하는 클래스 (아침, 점심, 저녁)
 * */
enum class MealTime(val icon: ImageVector, val backgroundColor: Int, val tint: Int) {
    MORNING(Icons.Default.WbTwilight, R.color.Orange, R.color.kakaoYellow),
    LUNCH(Icons.Default.WbSunny, R.color.kakaoYellow, R.color.Orange),
    DINNER(Icons.Default.NightsStay, R.color.main_blue, R.color.point_blue)
}

