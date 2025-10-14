package com.alyak.detector.ui.family;

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.alyak.detector.R


@Composable
fun ScheduleBox(
    doseTime: String = "오늘 저녁 7시",
    medicine: String = "고혈압약 아모잘탄",
    detail: String = "1정, 식후 30분",
    timeLeft: String = "3시간 20분",
    onAlarmClick: () -> Unit = {}
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(150.dp)
            .background(
                brush = Brush.horizontalGradient(
                    listOf(colorResource(R.color.lightPurple), colorResource(R.color.lightPink))
                ),
                shape = RoundedCornerShape(24.dp)
            )
            .padding(20.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column {
                Text("다음 복용 예정", fontSize = 15.sp, color = Color.Black.copy(alpha = 0.75f))
                Text(doseTime, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(top = 8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .padding(4.dp)
                            .background(Color.White, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null,
                            tint = Color(0xFF7262FD),
                            modifier = Modifier.size(40.dp)
                        )

                    }
                    Column {
                        Text(medicine, fontSize = 16.sp, fontWeight = FontWeight.Normal)
                        Text(
                            detail,
                            fontSize = 12.sp,
                            color = colorResource(R.color.black),
                            fontWeight = FontWeight.Thin
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.weight(1f))
            Column(horizontalAlignment = Alignment.End) {
                Text("남은 시간", fontSize = 13.sp, color = Color.Black.copy(alpha = 0.7f))
                Text(
                    timeLeft,
                    fontSize = 25.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF7262FD)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = onAlarmClick,
                    shape = RoundedCornerShape(20.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                    elevation = ButtonDefaults.buttonElevation(4.dp), // 그림자 높이 조정
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 0.dp),
                ) {
                    Icon(
                        imageVector = Icons.Default.Alarm,
                        contentDescription = null,
                        tint = Color(0xFF7262FD),
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(Modifier.width(6.dp))
                    Text("알림 설정", color = Color(0xFF7262FD), fontSize = 13.sp)
                }
            }
        }
    }
}


@Composable
@Preview(showBackground = true)
fun ScheduleBoxPrev() {
    ScheduleBox()
}