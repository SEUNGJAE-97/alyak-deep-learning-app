package com.alyak.detector.ui.other

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.alyak.detector.R
import com.alyak.detector.data.dto.pill.PillDetail.AdditionalInfoDTO

@Composable
fun AdditionalInfoBox(icon: Painter, label: String, value: String) {
    Box(
        modifier = Modifier.size(25.dp, 15.dp)
    ) {

    }

}

@Composable
fun AdditionalInfoSection(info: AdditionalInfoDTO) {
    Column {
        Row {
            AdditionalInfoBox(
                icon = painterResource(R.drawable.ic_storage),
                label = "보관 방법",
                value = info.storageMethod
            )
            InfoItem(
                icon = painterResource(R.drawable.ic_expiration),
                label = "유효기간",
                value = info.expiration
            )
        }

        InfoItem(
            icon = painterResource(R.drawable.ic_formulation),
            label = "제형",
            value = info.formulation
        )
        InfoItem(
            icon = painterResource(R.drawable.ic_packaging),
            label = "포장 단위",
            value = info.packaging
        )
    }
}

@Composable
fun AdditionalInfoPrev() {

}