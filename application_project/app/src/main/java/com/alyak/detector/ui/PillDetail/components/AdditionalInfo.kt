package com.alyak.detector.ui.PillDetail.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.alyak.detector.R
import com.alyak.detector.data.dto.pill.PillDetail.AdditionalInfoDTO

@Composable
fun AdditionalInfoBox(
    icon: Painter,
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .padding(end = 24.dp) // 항목 간 좌우 여백 조정 (Row에서 마지막은 제외)
    ) {
        Icon(
            painter = icon,
            contentDescription = label,
            tint = Color(0xFF979797), // 이미지와 비슷하게 회색 지정
            modifier = Modifier
                .size(36.dp)
                .padding(end = 8.dp) // 아이콘 오른쪽 간격
        )
        Column {
            Text(
                text = label,
                color = Color(0xFF979797),
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = value,
                color = Color.Black,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}


@Composable
fun AdditionalInfoSection(info: AdditionalInfoDTO) {
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            AdditionalInfoBox(
                modifier = Modifier.weight(1f),
                icon = painterResource(R.drawable.ic_storage),
                label = "보관 방법",
                value = info.storageMethod
            )
            AdditionalInfoBox(
                modifier = Modifier.weight(1f),
                icon = painterResource(R.drawable.ic_expiration),
                label = "유효기간",
                value = info.expiration
            )
        }
        Row(
            modifier = Modifier.padding(vertical = 8.dp)
        ) {
            AdditionalInfoBox(
                modifier = Modifier.weight(1f),
                icon = painterResource(R.drawable.ic_formulation),
                label = "제형",
                value = info.formulation
            )
            AdditionalInfoBox(
                modifier = Modifier.weight(1f),
                icon = painterResource(R.drawable.ic_packaging),
                label = "포장 단위",
                value = info.packaging
            )
        }

    }
}

@Composable
@Preview(showBackground = true)
fun AdditionalInfoPrev() {
    AdditionalInfoSection(
        AdditionalInfoDTO(
            storageMethod = "실온 보관 1~30도",
            expiration = "2026-12-31",
            formulation = "타원형 정제",
            packaging = "10정"
        )
    )
}