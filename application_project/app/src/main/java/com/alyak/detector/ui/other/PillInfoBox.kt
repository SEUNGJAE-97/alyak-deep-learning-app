package com.alyak.detector.ui.other

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.alyak.detector.R
import com.alyak.detector.data.dto.pill.Pill
import com.alyak.detector.ui.components.StatusBadge

@Composable
fun PillInfoBox(
    PillInfo: Pill
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(2.dp, shape = RoundedCornerShape(24.dp))
            .clip(RoundedCornerShape(24.dp))
            .background(colorResource(R.color.white))

    ) {
        Row(
            modifier = Modifier.padding(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            //image
            Image(
                painter = painterResource(R.drawable.pill),
                contentDescription = null,
                modifier = Modifier
                    .clip(shape = RoundedCornerShape(24.dp))
                    .weight(0.3f)
                    .size(30.dp)
            )
            //info
            Column(modifier = Modifier.weight(0.5f)) {
                Text(PillInfo.name, fontWeight = FontWeight.Bold, fontSize = 20.sp)
                Text(PillInfo.ingredient, fontWeight = FontWeight.Thin)
                Row {
                    Text("제조사 : " + PillInfo.manufacturer, fontWeight = FontWeight.Thin, fontSize = 10.sp)
                    Spacer(modifier = Modifier.width(5.dp))
                    Text("식별 코드 : " + PillInfo.pid, fontWeight = FontWeight.Thin, fontSize = 10.sp)
                }

            }

            //type
            Column(
                modifier = Modifier
                    .weight(0.2f)
                    .align(Alignment.Top),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                StatusBadge(
                    text = PillInfo.category,
                    backgroundColor = colorResource(R.color.primaryBlue), // 연한 초록
                    textColor = colorResource(R.color.white)
                )
            }
        }
    }

}

@Composable
@Preview(showBackground = true)
fun PillInfoPrev() {
    val pill = Pill("타이레놀 500mg", "아세트아미노펜", "한국얀센", "일반약", "TYLENOL")
    PillInfoBox(pill)
}