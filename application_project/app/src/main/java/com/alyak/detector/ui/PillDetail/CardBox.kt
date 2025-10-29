package com.alyak.detector.ui.PillDetail

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ReportProblem
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.alyak.detector.R
import com.alyak.detector.data.dto.MealTime
import com.alyak.detector.data.dto.common.SpecialCautionType
import com.alyak.detector.data.dto.pill.PillDetail.AdditionalInfoDTO
import com.alyak.detector.data.dto.pill.PillDetail.AlertInfoDTO
import com.alyak.detector.data.dto.pill.PillDetail.DosageInfoDTO
import com.alyak.detector.data.dto.pill.PillDetail.EffectsInfoDTO
import com.alyak.detector.data.dto.pill.PillDetail.MedicineDetailDTO
import com.alyak.detector.data.dto.pill.PillDetail.MedicineInfoDTO
import com.alyak.detector.data.dto.pill.PillDetail.MemoDTO
import com.alyak.detector.data.dto.pill.PillDetail.SideEffectsDTO
import com.alyak.detector.data.dto.pill.PillDetail.SpecialCautionDTO
import com.alyak.detector.ui.components.StatusBadge
import com.alyak.detector.ui.other.AdditionalInfoSection
import com.alyak.detector.ui.theme.CardBackground

@Composable
fun CardBox(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .background(colorResource(R.color.white))
            .padding(vertical = 8.dp),
        shape = RoundedCornerShape(26.dp),
        colors = CardDefaults.cardColors(containerColor = CardBackground),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Box(Modifier.padding(20.dp)) {
            content()
        }
    }
}

/**
 * @param icon : 아이콘
 * @param title : 제목
 * @param content : 내용
 * */
@Composable
fun TitledSection(
    icon: ImageVector,
    title: String,
    content: @Composable () -> Unit
) {
    Column {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, contentDescription = null, tint = colorResource(R.color.primaryBlue))
            Spacer(Modifier.width(8.dp))
            Text(title, fontWeight = FontWeight.Bold, fontSize = 18.sp)
        }
        Spacer(Modifier.height(8.dp))
        content()
    }
}

/**
 *@param tags : StatusBadge에 tags를 담아 출력
 * */
@Composable
fun <T> TagList(
    tags: List<T>,
    labelMapper: @Composable (T) -> Pair<String, Painter?>
) {
    Row(Modifier.horizontalScroll(rememberScrollState())) {
        tags.forEach { tag ->
            val (text, icon) = labelMapper(tag)
            StatusBadge(
                text,
                icon,
                colorResource(R.color.primaryBlue).copy(alpha = 0.5f),
                colorResource(R.color.primaryBlue)
            )
            Spacer(Modifier.width(6.dp))
        }
    }
}

/**
 * 복용 알림 등록, 이력 확인 버튼
 * */
@Composable
fun FunctionButtonRow() {
    Column(Modifier.fillMaxWidth()) {
        Button(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = colorResource(R.color.primaryBlue)),
            onClick = { }
        ) {
            Row(
                Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Default.Notifications,
                    contentDescription = null,
                    Modifier.size(20.dp),
                    tint = colorResource(R.color.white)
                )
                Spacer(Modifier.width(12.dp))
                Text("복약 알림 등록", color = Color.White, modifier = Modifier.weight(1f))
                Icon(
                    Icons.Default.ChevronRight,
                    contentDescription = null,
                    modifier = Modifier.size(30.dp),
                    tint = colorResource(R.color.white)
                )
            }

        }
        Spacer(Modifier.height(6.dp))
        OutlinedButton(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            onClick = { },
        ) {
            Row(
                Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ){
                Icon(
                    painterResource(R.drawable.history),
                    contentDescription = null,
                    Modifier.size(20.dp),
                    tint = colorResource(R.color.primaryBlue)
                )
                Spacer(Modifier.width(12.dp))
                Text("복용 이력 확인", color = colorResource(R.color.primaryBlue), modifier = Modifier.weight(1f))
                Icon(
                    Icons.Default.ChevronRight,
                    contentDescription = null,
                    modifier = Modifier.size(30.dp),
                    tint = colorResource(R.color.primaryBlue)
                )
            }
        }
    }
}

/**
 * @param title 주의 사항
 * @param items 주의 사항 세부 정보 (알레르기 반응 주의, 과다 복용 금지)
 * */
@Composable
fun AlertBox(
    title: String,
    items: List<String>
) {
    Column(
        Modifier
            .fillMaxWidth()
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

// TODO : 삭제고려
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
@Preview(showBackground = true, heightDp = 2000)
fun FullPillDetailScreenPreview() {
    val medicineDetail = MedicineDetailDTO(
        medicineInfo = MedicineInfoDTO(
            name = "타이레놀 500mg",
            subName = "아세트 아미노펜",
            manufacturer = "한국제약",
            code = "TYL500",
            category = "일반의약품",
            img = R.drawable.pill
        ),
        dosageInfo = DosageInfoDTO(
            dosageText = "하루 3회, 식후 30분 내 복용",
            dosageTimes = listOf(MealTime.MORNING, MealTime.LUNCH, MealTime.DINNER)
        ),
        effectsInfo = EffectsInfoDTO(
            tags = listOf("해열", "진통"),
            description = "감기 증상 완화 및 통증 완화에 효과가 있음"
        ),
        alertInfo = AlertInfoDTO(
            title = "주의사항",
            items = listOf("알레르기 반응 주의", "과다 복용 금지")
        ),
        specialCaution = SpecialCautionDTO(
            title = "특별 주의 대상",
            tags = listOf(SpecialCautionType.PREGNANT, SpecialCautionType.DRIVER),
            extraText = "운전 시 주의가 필요"
        ),
        sideEffects = SideEffectsDTO(
            title = "주요 부작용",
            description = "두통, 어지러움, 위장 장애 등이 발생할 수 있음"
        ),
        additionalInfo = AdditionalInfoDTO(
            storageMethod = "실온 보관 1~30도",
            expiration = "2026-12-31",
            formulation = "타원형 정제",
            packaging = "10정"
        ),
        memo = MemoDTO(
            content = "복용 시간 꼭 지킬 것"
        )
    )

    Column(
        Modifier
            .verticalScroll(rememberScrollState())
            .background(Color.White)
            .fillMaxWidth()
            .padding(16.dp)
    ) {

        //  약 정보 카드
        CardBox {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    Modifier
                        .size(60.dp)
                        .background(colorResource(R.color.white))
                        .border(2.dp, colorResource(R.color.white), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(medicineDetail.medicineInfo.img),
                        contentDescription = medicineDetail.medicineInfo.name,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                }

                Spacer(Modifier.width(20.dp))

                Column {
                    Text(
                        medicineDetail.medicineInfo.name,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    )
                    Text(
                        "(${medicineDetail.medicineInfo.subName})",
                        fontSize = 14.sp,
                        color = Color.DarkGray
                    )
                    Spacer(Modifier.height(2.dp))
                    Text(
                        "제조사: ${medicineDetail.medicineInfo.manufacturer}",
                        fontSize = 13.sp,
                        color = Color.Gray
                    )
                    Text(
                        "식별코드: ${medicineDetail.medicineInfo.code}",
                        fontSize = 13.sp,
                        color = Color.Gray
                    )
                    Text(
                        "분류: ${medicineDetail.medicineInfo.category}",
                        fontSize = 13.sp,
                        color = Color.Gray
                    )
                }
                Spacer(Modifier.weight(1f))
                Icon(
                    Icons.Default.FavoriteBorder,
                    contentDescription = null,
                    tint = Color(0xFF7262FD),
                    modifier = Modifier.size(24.dp)
                )
            }
        }

        //  용법 및 용량
        CardBox {
            TitledSection(
                icon = Icons.Default.Info,
                title = "용법 및 용량"
            ) {
                Column {
                    Row(
                        modifier = Modifier.padding(4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.Schedule,
                            contentDescription = null,
                            tint = colorResource(R.color.primaryBlue),
                            modifier = Modifier.padding(end = 8.dp)
                        )
                        Text(medicineDetail.dosageInfo.dosageText, fontSize = 15.sp)
                        Spacer(Modifier.height(10.dp))
                    }
                    Row(
                        modifier = Modifier.padding(4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            Icons.Default.CalendarMonth,
                            contentDescription = null,
                            tint = colorResource(R.color.primaryBlue),
                            modifier = Modifier.padding(end = 8.dp)
                        )
                        Spacer(Modifier.width(4.dp))
                        medicineDetail.dosageInfo.dosageTimes.forEach { mealTime ->

                            Box(
                                modifier = Modifier
                                    .size(48.dp)
                                    .background(
                                        colorResource(mealTime.backgroundColor),
                                        CircleShape
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = mealTime.icon,
                                    contentDescription = mealTime.name,
                                    tint = colorResource(mealTime.tint),
                                    modifier = Modifier
                                        .size(20.dp)
                                )
                            }
                        }
                        Spacer(Modifier.width(18.dp))

                    }
                    Row(
                        modifier = Modifier.padding(4.dp)
                    ) {
                        Icon(
                            Icons.Default.WaterDrop,
                            contentDescription = null,
                            tint = Color(0xFF7262FD)
                        )
                        Spacer(Modifier.width(4.dp))
                        Text("충분한 물과 함께 복용")
                    }
                }
            }
        }

        //  효능 및 효과
        CardBox {
            TitledSection(
                icon = Icons.Default.Favorite,
                title = "효능 및 효과"
            ) {
                TagList(
                    medicineDetail.effectsInfo.tags,
                    { tag -> Pair(tag, null) }
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    medicineDetail.effectsInfo.description,
                    fontSize = 14.sp,
                    color = Color(0xFF4A4A4A)
                )
            }
        }

        //  주의사항 경고 박스
        CardBox {
            AlertBox(
                title = medicineDetail.alertInfo.title,
                items = medicineDetail.alertInfo.items
            )
        }

        //  특별 주의 대상
        CardBox {
            TitledSection(Icons.Default.Person, medicineDetail.specialCaution.title) {
                TagList(
                    medicineDetail.specialCaution.tags,
                    { tag -> Pair(stringResource(tag.labelResId), painterResource(tag.iconResId)) })
                Spacer(Modifier.height(8.dp))

            }
        }

        //  주요 부작용
        CardBox {
            TitledSection(Icons.Default.ReportProblem, medicineDetail.sideEffects.title) {
                Column {
                    Text(medicineDetail.sideEffects.description, fontSize = 14.sp)
                    Spacer(Modifier.height(6.dp))
                    Button(
                        onClick = { /* TODO: 더보기 기능 */ },
                        modifier = Modifier
                            .align(Alignment.End)
                            .fillMaxWidth(),
                        shape = RoundedCornerShape(24),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF5F5F9))
                    ) {
                        Text("더 많은 주의사항 보기", color = Color(0xFF7262FD), fontSize = 13.sp)
                    }
                }
            }
        }

        //  추가 정보
        CardBox {
            TitledSection(Icons.Default.Info, "추가 정보") {
                AdditionalInfoSection(
                    medicineDetail.additionalInfo
                )
            }
        }

        //  맞춤 기능(알림 등록/이력)
        CardBox { FunctionButtonRow() }

        //  내 메모 입력
        CardBox {
            TitledSection(Icons.Default.Edit, "내 메모") {
                var memoText by remember { mutableStateOf(medicineDetail.memo.content) }
                OutlinedTextField(
                    value = memoText,
                    onValueChange = { memoText = it },
                    placeholder = { Text("이 약에 대한 메모를 작성하세요...") },
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(92.dp)
                )
            }
        }
    }
}
