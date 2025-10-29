package com.alyak.detector.ui.other

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddLocation
import androidx.compose.material.icons.filled.Call
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.alyak.detector.R
import com.alyak.detector.ui.components.StatusBadge

@Composable
fun HospitalInfo(
    modifier: Modifier,
    hospitalName: String,
    hospitalAddress: String,
    hospitalDepartment: ArrayList<String>,
) {
    Card(
        Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp)),

        colors = CardDefaults.cardColors(Color.White),
        elevation = CardDefaults.cardElevation(1.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Row(
                Modifier.fillMaxWidth(),
                Arrangement.SpaceBetween,
                Alignment.Top
            ) {
                Text(
                    hospitalName,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
                StatusBadge("영업중", null,Color(0xFFD1FAE5), Color(0xFF10B981))
            }

            Spacer(Modifier.height(6.dp))
            Text("300m", fontSize = 14.sp, color = Color.Gray)
            Spacer(Modifier.height(10.dp))
            Text(hospitalAddress, fontSize = 15.sp)
            Spacer(Modifier.height(10.dp))

            Row {
                hospitalDepartment.forEach { department ->
                    Text(
                        hospitalDepartment.joinToString(", "),
                        fontSize = 15.sp,
                        color = Color.DarkGray,
                        modifier = Modifier.padding(top = 10.dp, bottom = 18.dp)
                    )
                    Spacer(Modifier.size(10.dp))
                }
            }

            Row {
                SimpleButton(
                    icon = Icons.Default.AddLocation,
                    description = "길찾기",
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp),
                    color = R.color.primaryBlue,
                    lineColor = R.color.primaryBlue,
                    textColor = R.color.white
                )
                Spacer(Modifier.size(10.dp))
                SimpleButton(
                    icon = Icons.Default.Call,
                    description = "전화하기",
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp),
                    color = R.color.white,
                    lineColor = R.color.primaryBlue,
                    textColor = R.color.primaryBlue
                )
            }
        }
    }

}

@Composable
fun SimpleButton(
    modifier: Modifier,
    icon: ImageVector,
    description: String,
    color: Int,
    lineColor: Int,
    textColor: Int,
) {
    Box(
        modifier
            .clip(RoundedCornerShape(8.dp))
            .border(2.dp, colorResource(lineColor), RoundedCornerShape(8.dp))
            .background(colorResource(color)),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                icon,
                contentDescription = null,
                modifier = Modifier
                    .size(24.dp),
                tint = colorResource(textColor),
            )

            Spacer(Modifier.size(10.dp))

            Text(description, color = colorResource(textColor))
        }
    }
}

@Composable
@Preview(showBackground = true)
fun HospitalInfoPrev() {
    HospitalInfo(
        modifier = Modifier,
        hospitalName = "연세 세브란스 병원",
        hospitalAddress = "서울특별시 서대문구 신촌동",
        hospitalDepartment = arrayListOf("안과", "정형외과")
    )
}