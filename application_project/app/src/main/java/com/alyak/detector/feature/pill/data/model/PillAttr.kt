package com.alyak.detector.feature.pill.data.model

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Circle
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import com.alyak.detector.ui.theme.PillBlack
import com.alyak.detector.ui.theme.PillBlue
import com.alyak.detector.ui.theme.PillBrown
import com.alyak.detector.ui.theme.PillGreen
import com.alyak.detector.ui.theme.PillGrey
import com.alyak.detector.ui.theme.PillLightGreen
import com.alyak.detector.ui.theme.PillNavy
import com.alyak.detector.ui.theme.PillOrange
import com.alyak.detector.ui.theme.PillPink
import com.alyak.detector.ui.theme.PillPurple
import com.alyak.detector.ui.theme.PillRed
import com.alyak.detector.ui.theme.PillTeal
import com.alyak.detector.ui.theme.PillWhite
import com.alyak.detector.ui.theme.PillWine
import com.alyak.detector.ui.theme.PillYellow

enum class PillShapeType(val label: String) {
    ALL("전체"),
    ROUND("원형"),
    OVAL("타원형"),
    CAPSULE("장방형"),
    HALF_ROUND("반원형"),
    TRIANGLE("삼각형"),
    SQUARE("사각형"),
    DIAMOND("마름모형"),
    PENTAGON("오각형"),
    HEXAGON("육각형"),
    OCTAGON("팔각형"),
    ETC("기타")
}


enum class PillColor(val label: String, val color: Color) {
    ALL("전체", Color.Unspecified),
    WHITE("하양", PillWhite),
    YELLOW("노랑", PillYellow),
    ORANGE("주황", PillOrange),
    PINK("분홍", PillPink),
    RED("빨강", PillRed),
    BROWN("갈색", PillBrown),
    LIGHT_GREEN("연두", PillLightGreen),
    GREEN("초록", PillGreen),
    TEAL("청록", PillTeal),
    BLUE("파랑", PillBlue),
    NAVY("남색", PillNavy),
    WINE("자주", PillWine),
    PURPLE("보라", PillPurple),
    GREY("회색", PillGrey),
    BLACK("검정", PillBlack),
    TRANSPARENT("투명", Color.Transparent);
}

enum class PillLineType(val label: String, val query: String) {
    ALL("전체",""),
    NONE("없음",""),
    PLUS(" + 형","+"),
    MINUS(" - 형","-"),
    ETC("기타","")
}