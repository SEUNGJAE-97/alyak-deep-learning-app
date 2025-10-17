package com.alyak.detector.ui.other

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.*
import androidx.compose.runtime.Composable

// 공통 카드 박스
@Composable
fun CardBox(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        shape = RoundedCornerShape(26.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Box(Modifier.padding(20.dp)) {
            content()
        }
    }
}

// 헤더+아이콘+서브텍스트+내용 슬롯
@Composable
fun TitledSection(
    icon: ImageVector,
    title: String,
    description: String? = null,
    content: @Composable () -> Unit
) {
    Column {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, contentDescription = null, tint = Color(0xFF7262FD))
            Spacer(Modifier.width(8.dp))
            Text(title, fontWeight = FontWeight.Bold, fontSize = 18.sp)
        }
        if (description != null) {
            Spacer(Modifier.height(4.dp))
            Text(description, fontSize = 13.sp, color = Color.Gray)
        }
        Spacer(Modifier.height(8.dp))
        content()
    }
}

// 태그 스타일 리스트
@Composable
fun TagList(tags: List<String>) {
    Row(Modifier.horizontalScroll(rememberScrollState())) {
        tags.forEach {
            Box(
                modifier = Modifier
                    .background(Color(0xFFE7E8F2), RoundedCornerShape(50))
                    .padding(horizontal = 12.dp, vertical = 6.dp)
                    .border(1.dp, Color(0xFF7262FD), RoundedCornerShape(50))
                    .padding(horizontal = 8.dp, vertical = 2.dp)
            ) {
                Text(it, color = Color(0xFF7262FD), fontSize = 13.sp)
            }
            Spacer(Modifier.width(6.dp))
        }
    }
}

// 경고 박스
@Composable
fun AlertBox(
    title: String,
    items: List<String>
) {
    Column(
        Modifier
            .background(Color(0xFFFDF0F0), RoundedCornerShape(16.dp))
            .padding(16.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.Warning, contentDescription = null, tint = Color(0xFFD65A54))
            Spacer(Modifier.width(8.dp))
            Text(title, fontWeight = FontWeight.Bold, color = Color(0xFFD65A54))
        }
        Spacer(Modifier.height(8.dp))
        Column {
            items.forEach {
                Text("• $it", fontSize = 15.sp, color = Color(0xFFD65A54))
            }
        }
    }
}

// 버튼 Row 스타일 (맞춤 기능)
@Composable
fun FunctionButtonRow() {
    Column(Modifier.fillMaxWidth()) {
        Button(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF7262FD)),
            onClick = { }
        ) {
            Icon(Icons.Default.Notifications, contentDescription = null, Modifier.size(20.dp))
            Spacer(Modifier.width(6.dp))
            Text("복약 알림 등록", color = Color.White)
        }
        Spacer(Modifier.height(6.dp))
        OutlinedButton(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            onClick = { }
        ) {
            Icon(Icons.Default.CheckCircle, contentDescription = null, Modifier.size(20.dp), tint = Color(0xFF7262FD))
            Spacer(Modifier.width(6.dp))
            Text("복용 이력 확인", color = Color(0xFF7262FD))
        }
    }
}

// 메모 입력 박스
@Composable
fun MemoInputBox() {
    var value by remember { mutableStateOf("") }
    OutlinedTextField(
        value = value,
        onValueChange = { value = it },
        placeholder = { Text("이 약에 대한 메모를 작성하세요...") },
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier
            .fillMaxWidth()
            .height(92.dp)
    )
}

@Composable
@Preview(showBackground = true)
fun FullPillDetailScreenPreview() {
    Column(Modifier
        .verticalScroll(rememberScrollState())
        .background(Color.White)
        .fillMaxWidth()
        .padding(16.dp)) {

        // [1] 약 정보 카드
        CardBox {
            Row(verticalAlignment = Alignment.CenterVertically) {
                // 약 모양 예시
                Box(
                    Modifier
                        .size(60.dp)
                        .background(Color(0xFFF5F5F5), CircleShape)
                        .border(2.dp, Color(0xFFD1D1EA), CircleShape),
                    contentAlignment = Alignment.Center
                ) { Icon(Icons.Default.Circle, contentDescription = null, tint = Color(0xFFC7C7D6), modifier = Modifier.size(38.dp)) }
                Spacer(Modifier.width(20.dp))

                Column {
                    Text("타이레놀 500mg", fontWeight = FontWeight.Bold, fontSize = 20.sp)
                    Text("(아세트 아미노펜)", fontSize = 14.sp, color = Color.DarkGray)
                    Spacer(Modifier.height(2.dp))
                    Text("제조사: 한국 얀센제약", fontSize = 13.sp, color = Color.Gray)
                    Text("식별코드: TY500", fontSize = 13.sp, color = Color.Gray)
                    Text("분류: 일반의약품", fontSize = 13.sp, color = Color.Gray)
                }
                Spacer(Modifier.weight(1f))
                Icon(Icons.Default.FavoriteBorder, contentDescription = null, tint = Color(0xFF7262FD), modifier = Modifier.size(24.dp))
            }
        }

        // [2] 용법 및 용량
        CardBox {
            TitledSection(
                icon = Icons.Default.Info,
                title = "용법 및 용량"
            ) {
                Column {
                    Text("1일 1회, 1회 1~2정, 식후 30분", fontSize = 15.sp)
                    Spacer(Modifier.height(10.dp))
                    Row {
                        Icon(Icons.Default.Schedule, contentDescription = null, tint = Color(0xFF7262FD))
                        Spacer(Modifier.width(4.dp))
                        Text("복용 시간대")
                        Spacer(Modifier.width(18.dp))
                        Icon(Icons.Default.WaterDrop, contentDescription = null, tint = Color(0xFF7262FD))
                        Spacer(Modifier.width(4.dp))
                        Text("충분한 물과 함께 복용")
                    }
                }
            }
        }

        // [3] 효능 및 효과
        CardBox {
            TitledSection(
                icon = Icons.Default.Favorite,
                title = "효능 및 효과"
            ) {
                TagList(listOf("해열", "진통", "두통", "근육통", "치통"))
                Spacer(Modifier.height(8.dp))
                Text("이 약은 감기로 인한 발열 및 통증, 두통, 신경통, 근육통, 월경통, 치통, 관절통, 류마티스통에 사용합니다.",
                    fontSize = 14.sp,
                    color = Color(0xFF4A4A4A))
            }
        }

        // [4] 주의사항 경고 박스
        CardBox {
            AlertBox(
                title = "주의사항 및 부작용",
                items = listOf(
                    "이 약에 과민증이 있는 환자",
                    "소화성 궤양 환자",
                    "심한 혈액 이상 환자",
                    "심한 간장애 환자",
                    "심한 신장장애 환자"
                )
            )
        }

        // [5] 특별 주의 대상
        CardBox {
            TitledSection(Icons.Default.Person, "특별 주의 대상") {
                TagList(listOf("임산부", "어린이", "고령자"))
                Spacer(Modifier.height(8.dp))
                Row {
                    Icon(Icons.Default.DirectionsCar, contentDescription = null, Modifier.size(16.dp), tint = Color(0xFF7262FD))
                    Spacer(Modifier.width(4.dp))
                    Text("운전자")
                }
            }
        }

        // [6] 주요 부작용
        CardBox {
            TitledSection(Icons.Default.ReportProblem, "주요 부작용") {
                Column {
                    Text("구역, 구토, 식욕부진, 위부불쾌감, 소화불량\n두드러기, 발진, 가려움증\n어지러움, 귀울림, 시각장애",
                        fontSize = 14.sp)
                    Spacer(Modifier.height(6.dp))
                    Button(
                        onClick = { /* TODO: 더보기 기능 */ },
                        modifier = Modifier.align(Alignment.End),
                        shape = RoundedCornerShape(50),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF5F5F9))
                    ) {
                        Text("더 많은 주의사항 보기", color = Color(0xFF7262FD), fontSize = 13.sp)
                    }
                }
            }
        }

        // [7] 추가 정보(예시)
        CardBox {
            TitledSection(Icons.Default.Info, "추가 정보") {
                Column {
                    Text("보관방법: 실온보관 (1~30°C)\n유효기간: 2026년 12월 31일\n제형: 타원형 정제\n포장 단위: 10정, 20정, 100정", fontSize = 14.sp)
                }
            }
        }

        // [8] 맞춤 기능(알림 등록/이력)
        CardBox { FunctionButtonRow() }

        // [9] 내 메모 입력
        CardBox {
            TitledSection(Icons.Default.Edit, "내 메모") {
                MemoInputBox()
            }
        }
    }
}
