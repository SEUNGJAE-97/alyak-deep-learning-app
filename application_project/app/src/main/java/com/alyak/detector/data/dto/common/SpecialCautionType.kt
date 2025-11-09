package com.alyak.detector.data.dto.common

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.alyak.detector.R

/**
 * 특별 주의 대상을 정의
 *
 * @property labelResId 화면에 표시될 이름의 문자열 리소스 ID (ex : R.String.caution_pregnant)
 * @property iconResId 화면에 표시될 아이콘의 리소스 ID (ex : R.drawable.ic_caution_pregnant)
 * */
enum class SpecialCautionType(@StringRes val labelResId: Int, @DrawableRes val iconResId: Int) {
    PREGNANT(R.string.caution_pregnant, R.drawable.ic_caution_pregnant),
    CHILD(R.string.caution_child, R.drawable.ic_caution_child),
    ELDERLY(R.string.caution_elderly, R.drawable.ic_caution_elderly),
    DRIVER(R.string.caution_driver, R.drawable.ic_caution_driver)
}