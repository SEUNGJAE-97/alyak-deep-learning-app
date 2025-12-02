package com.alyak.detector.feature.pill.ui.search.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.alyak.detector.R
import com.alyak.detector.feature.pill.data.model.Pill
import com.alyak.detector.ui.components.StatusBadge
import com.valentinilk.shimmer.shimmer

@Composable
fun PillInfoBox(
    pillInfo: Pill,
    isLoading: Boolean = false
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(2.dp, shape = RoundedCornerShape(24.dp))
            .clip(RoundedCornerShape(24.dp))
            .background(colorResource(R.color.white))
            .then(if (isLoading) Modifier.shimmer() else Modifier)

    ) {
        Row(
            modifier = Modifier.padding(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            val imageModifier = Modifier
                .clip(shape = RoundedCornerShape(24.dp))
                .weight(0.3f)
                .size(30.dp)

            if (isLoading) {
                Box(modifier = imageModifier.background(Color.LightGray))
            } else {
                AsyncImage(
                    model = pillInfo.pillImg,
                    contentDescription = pillInfo.name,
                    modifier = imageModifier,
                    contentScale = ContentScale.Fit,
                    error = painterResource(R.drawable.pill),
                    placeholder = painterResource(R.drawable.pill),
                )
            }
            //info
            Column(
                modifier = Modifier
                    .weight(0.5f)
                    .padding(horizontal = 8.dp)
            ) {
                if (isLoading) {
                    TextPlaceholder(height = 24.dp, widthFraction = 0.8f)
                } else {
                    Text(pillInfo.name, fontWeight = FontWeight.Bold, fontSize = 20.sp)
                }
                Spacer(modifier = Modifier.height(4.dp))

                if (isLoading) {
                    TextPlaceholder(height = 16.dp, widthFraction = 0.5f)
                } else {
                    Text(pillInfo.classification ?: "정보없음", fontWeight = FontWeight.Thin)
                }
                Spacer(modifier = Modifier.height(8.dp))

                Column {
                    if (isLoading) {
                        Box(
                            modifier = Modifier
                                .width(80.dp)
                                .height(12.dp)
                                .background(Color.LightGray)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Box(
                            modifier = Modifier
                                .width(80.dp)
                                .height(12.dp)
                                .background(Color.LightGray)
                        )
                    } else {
                        Text(
                            "제조사 : ${pillInfo.manufacturer ?: "정보없음"}",
                            fontWeight = FontWeight.Thin,
                            fontSize = 10.sp
                        )
                        Spacer(modifier = Modifier.width(5.dp))
                        Text(
                            "식별 코드 : ${pillInfo.pid}",
                            fontWeight = FontWeight.Thin,
                            fontSize = 10.sp,
                            maxLines = 1
                        )
                    }
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
                if (isLoading) {
                    Box(
                        modifier = Modifier
                            .height(20.dp)
                            .width(50.dp)
                            .background(Color.LightGray)
                    )
                } else {
                    StatusBadge(
                        text = pillInfo.pillType ?: " ",
                        backgroundColor = colorResource(R.color.primaryBlue),
                        textColor = colorResource(R.color.white)
                    )
                }
            }
        }
    }
}

@Composable
fun TextPlaceholder(height: Dp, widthFraction: Float) {
    Box(
        modifier = Modifier
            .fillMaxWidth(widthFraction)
            .height(height)
            .clip(RoundedCornerShape(4.dp))
            .background(Color.LightGray)
    )
}

@Composable
@Preview(showBackground = true)
fun PillInfoPrev() {
    val pill = Pill("타이레놀 500mg", "아세트아미노펜", "한국얀센", "일반약", "TYLENOL", "")
    PillInfoBox(pill)
}