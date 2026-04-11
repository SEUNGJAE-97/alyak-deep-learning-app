package com.alyak.detector.feature.pill.ui.PillDetail.components

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ReportProblem
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.alyak.detector.R
import com.alyak.detector.feature.pill.data.model.MedicineDetailDto

@Composable
fun PillDetailContent(
    medicineDetail: MedicineDetailDto
) {
    var isSideEffectExpanded by remember { mutableStateOf(false) }

    Column(
        Modifier
            .verticalScroll(rememberScrollState())
            .background(Color.White)
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        medicineDetail.medicineInfo.let { info ->
            CardBox {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        Modifier
                            .size(60.dp)
                            .background(colorResource(R.color.white))
                            .border(2.dp, colorResource(R.color.white), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        AsyncImage(
                            model = info.img,
                            contentDescription = info.name,
                            modifier = Modifier.fillMaxWidth(),
                            contentScale = ContentScale.Fit,
                            error = painterResource(R.drawable.pill),
                            placeholder = painterResource(R.drawable.pill),
                        )
                    }

                    Spacer(Modifier.width(20.dp))

                    Column {
                        Text(info.name, fontWeight = FontWeight.Bold, fontSize = 20.sp)
                        Text("(${info.classification})", fontSize = 14.sp, color = Color.DarkGray)
                        Spacer(Modifier.height(2.dp))
                        Text("제조사: ${info.manufacturer}", fontSize = 13.sp, color = Color.Gray)
                        Text("식별코드: ${info.pillId}", fontSize = 13.sp, color = Color.Gray)
                        Text("분류: ${info.category}", fontSize = 13.sp, color = Color.Gray)
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
        }

        medicineDetail.dosageInfo.dosageText.takeIf { it.isNotBlank() }?.let { dosageText ->
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
                            Text(dosageText, fontSize = 15.sp)
                            Spacer(Modifier.height(10.dp))
                        }
                    }
                }
            }
        }

        val effects = medicineDetail.effectsInfo
        if (effects.tags.isNotEmpty() || effects.description.isNotBlank()) {
            CardBox {
                TitledSection(
                    icon = Icons.Default.Favorite,
                    title = "효능 및 효과"
                ) {
                    if (effects.tags.isNotEmpty()) {
                        TagList(effects.tags) { tag -> Pair(tag, null) }
                        Spacer(Modifier.height(8.dp))
                    }
                    if (effects.description.isNotBlank()) {
                        Text(
                            effects.description,
                            fontSize = 14.sp,
                            color = Color(0xFF4A4A4A)
                        )
                    }
                }
            }
        }
        medicineDetail.alertInfo.takeIf { it.items.isNotEmpty() }?.let { alert ->
            CardBox {
                AlertBox(
                    title = alert.title,
                    items = alert.items
                )
            }
        }

        medicineDetail.specialCaution.takeIf { it.tags.isNotEmpty() }?.let { caution ->
            CardBox {
                TitledSection(Icons.Default.Person, caution.title) {
                    TagList(
                        caution.tags,
                        { tag ->
                            Pair(
                                stringResource(tag.labelResId),
                                painterResource(tag.iconResId)
                            )
                        }
                    )
                    if (!caution.extraText.isNullOrBlank()) {
                        Spacer(Modifier.height(8.dp))
                        Text(caution.extraText, fontSize = 14.sp)
                    }
                }
            }
        }

        medicineDetail.sideEffects.description.takeIf { it.isNotBlank() }?.let { description ->
            CardBox {
                TitledSection(Icons.Default.ReportProblem, medicineDetail.sideEffects.title) {
                    Column(
                        modifier = Modifier.animateContentSize()
                    ) {
                        Text(
                            description,
                            fontSize = 14.sp,
                            maxLines = if (isSideEffectExpanded) Int.MAX_VALUE else 3,
                        )
                        Spacer(Modifier.height(6.dp))
                        Button(
                            onClick = { isSideEffectExpanded = !isSideEffectExpanded },
                            modifier = Modifier
                                .align(Alignment.End)
                                .fillMaxWidth(),
                            shape = RoundedCornerShape(24),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF5F5F9))
                        ) {
                            Text(
                                if (isSideEffectExpanded) "접기" else "부작용 정보 더보기",
                                color = Color(0xFF7262FD),
                                fontSize = 13.sp
                            )
                        }
                    }
                }
            }
        }
        CardBox { FunctionButtonRow() }
    }
}