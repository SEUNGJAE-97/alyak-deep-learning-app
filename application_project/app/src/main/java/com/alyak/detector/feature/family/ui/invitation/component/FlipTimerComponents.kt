package com.alyak.detector.feature.family.ui.invitation.component

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

val TextColor = Color(0xFFF5A67C)

/**
 * 초 단위 값을 분:초 포맷의 플립 타이머로 렌더링합니다.
 *
 * 분/초를 각각 두 자리 문자열(`MM` / `SS`) 카드로 렌더링합니다.
 *
 * @param targetNumber 남은 시간을 초 단위로 전달합니다.
 * @param cardWidth 각 숫자 카드의 너비입니다.
 * @param cardHeight 각 숫자 카드의 높이입니다.
 * @param textColor 숫자와 콜론에 적용할 색상입니다.
 */
@Composable
fun FlipCounter(
    targetNumber: Int,
    cardWidth: Dp,
    cardHeight: Dp,
    textColor: Color = TextColor
) {
    val min = (targetNumber / 60).coerceAtLeast(0)
    val sec = (targetNumber % 60).coerceAtLeast(0)
    val minuteText = min.toString().padStart(2, '0')
    val secondText = sec.toString().padStart(2, '0')

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        FlipDigit(
            numberText = minuteText,
            cardWidth = cardWidth,
            cardHeight = cardHeight,
            textColor = textColor
        )

        Text(
            text = ":",
            fontSize = (cardWidth.value * 0.6f).sp,
            fontWeight = FontWeight.Bold,
            color = textColor,
            modifier = Modifier.padding(bottom = 2.dp)
        )

        FlipDigit(
            numberText = secondText,
            cardWidth = cardWidth,
            cardHeight = cardHeight,
            textColor = textColor
        )
    }
}

/**
 * 두 자리 숫자 문자열 카드의 플립 애니메이션을 렌더링합니다.
 *
 * 숫자가 변경되면 0도에서 180도까지 회전 애니메이션을 수행하고,
 * 상/하 반쪽 레이어를 조합해 실제 플립 보드처럼 보이도록 구성합니다.
 *
 * @param numberText 현재 표시할 숫자 문자열(예: `04`)입니다.
 * @param cardWidth 카드의 너비입니다.
 * @param cardHeight 카드의 높이입니다.
 * @param textColor 숫자 색상입니다.
 */
@Composable
fun FlipDigit(
    numberText: String,
    cardWidth: Dp,
    cardHeight: Dp,
    textColor: Color
) {
    var currentText by remember { mutableStateOf(numberText) }
    var nextText by remember { mutableStateOf(numberText) }
    val rotation = remember { Animatable(0f) }

    LaunchedEffect(numberText) {
        if (numberText != currentText) {
            nextText = numberText
            rotation.animateTo(
                targetValue = 180f,
                animationSpec = tween(durationMillis = 600)
            )
            currentText = nextText
            rotation.snapTo(0f)
        }
    }

    val cardShape = RoundedCornerShape(8.dp)
    val cardBgColor = Color(0xFF1A1A1A)

    Box(
        modifier = Modifier
            .width(cardWidth)
            .height(cardHeight)
            .clip(cardShape)
            .background(cardBgColor),
        contentAlignment = Alignment.Center
    ) {
        DigitHalf(
            numberText = nextText,
            isTop = false,
            cardWidth = cardWidth,
            cardHeight = cardHeight,
            textColor = textColor
        )

        DigitHalf(
            numberText = nextText,
            isTop = true,
            cardWidth = cardWidth,
            cardHeight = cardHeight,
            textColor = textColor
        )

        DigitHalf(
            numberText = if (rotation.value <= 90f) currentText else nextText,
            isTop = rotation.value <= 90f,
            cardWidth = cardWidth,
            cardHeight = cardHeight,
            textColor = textColor,
            modifier = Modifier.graphicsLayer {
                rotationX = -rotation.value
                cameraDistance = 20f * density
                transformOrigin = TransformOrigin(0.5f, 1f)

                if (rotation.value > 90f) {
                    rotationX = 180f - rotation.value
                }
            }
        )

        if (rotation.value <= 90f) {
            DigitHalf(
                numberText = currentText,
                isTop = false,
                cardWidth = cardWidth,
                cardHeight = cardHeight,
                textColor = textColor
            )
        }

        Spacer(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(Color.Black.copy(alpha = 0.5f))
                .align(Alignment.Center)
        )
    }
}

/**
 * 플립 카드의 절반(상단 또는 하단)을 그립니다.
 *
 * [isTop] 값에 따라 상단 절반 또는 하단 절반 영역을 만들고,
 * 텍스트 위치를 미세 조정해 카드 중앙 분할선 기준으로 자연스럽게 보이게 합니다.
 *
 * @param numberText 해당 절반 영역에 표시할 두 자리 문자열입니다.
 * @param isTop `true`면 상단 절반, `false`면 하단 절반입니다.
 * @param cardWidth 카드 너비입니다.
 * @param cardHeight 카드 높이입니다.
 * @param textColor 숫자 색상입니다.
 * @param modifier 외부에서 전달하는 추가 Modifier입니다.
 */
@Composable
fun BoxScope.DigitHalf(
    numberText: String,
    isTop: Boolean,
    cardWidth: Dp,
    cardHeight: Dp,
    textColor: Color,
    modifier: Modifier = Modifier
) {
    val fontSize = minOf(cardWidth.value * 0.40f, cardHeight.value * 0.24f).sp
    val textStretchY = 1.22f

    Box(
        modifier = modifier
            .width(cardWidth)
            .height(cardHeight / 2)
            .align(if (isTop) Alignment.TopCenter else Alignment.BottomCenter)
            .clip(
                if (isTop) RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp)
                else RoundedCornerShape(bottomStart = 8.dp, bottomEnd = 8.dp)
            )
            .background(Color(0xFF1A1A1A)),
        contentAlignment = if (isTop) Alignment.BottomCenter else Alignment.TopCenter
    ) {
        Text(
            text = numberText,
            color = textColor,
            fontSize = fontSize,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            style = TextStyle(
                platformStyle = PlatformTextStyle(includeFontPadding = false),
                lineHeight = fontSize
            ),
            modifier = Modifier
                .fillMaxWidth()
                .graphicsLayer {
                    val moveDistance = size.height * 0.48f
                    scaleY = textStretchY
                    translationY = if (isTop) moveDistance else -moveDistance
                }
        )
    }
}