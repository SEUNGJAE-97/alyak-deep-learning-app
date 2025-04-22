package com.alyak.detector.ui.main


import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.alyak.detector.R
import com.alyak.detector.ui.theme.BackgroundGradientEnd
import com.alyak.detector.ui.theme.BackgroundGradientStart
import com.alyak.detector.ui.theme.CardBackground
import com.alyak.detector.ui.theme.PrimaryGreen
import androidx.compose.foundation.Image

@Composable
fun MainScreen(
    navController: NavController
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(BackgroundGradientStart, BackgroundGradientEnd)
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Spacer(modifier = Modifier.height(24.dp))

            // Logo
            Text(
                text = "Alyak",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Next Medication Card
            NextMedicationCard()

            Spacer(modifier = Modifier.height(24.dp))

            // Quick Action Buttons
            QuickActionButtons(
                onQrScanClick = {
                    navController.navigate("CameraScreen")
                },
                onPillSearchClick = {
                    navController.navigate("pillSearch")
                },
                onFamilyManageClick = {
                    navController.navigate("family")
                }
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Medication History
            MedicationHistory()

            Spacer(modifier = Modifier.height(24.dp))

            // Health Checklist
            HealthChecklist()
        }
    }
}

@Composable
fun NextMedicationCard() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = PrimaryGreen)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
        ) {
            Text(
                text = "다음 복용 알림",
                color = CardBackground,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "타이레놀 500mg",
                color = CardBackground,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "오후 1:00",
                color = CardBackground,
                fontSize = 14.sp
            )
        }
    }
}

@Composable
fun QuickActionButtons(
    onQrScanClick: () -> Unit,
    onPillSearchClick: () -> Unit,
    onFamilyManageClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        QuickActionButton(
            icon = painterResource(id = R.drawable.camera),
            text = "알약 스캔",
            modifier = Modifier.weight(1f),
            onClick = onQrScanClick
        )
        Spacer(modifier = Modifier.width(8.dp))
        QuickActionButton(
            icon = painterResource(id = R.drawable.pill),
            text = "알약 검색",
            modifier = Modifier.weight(1f),
            onClick = onPillSearchClick
        )
        Spacer(modifier = Modifier.width(8.dp))
        QuickActionButton(
            icon = painterResource(id = R.drawable.map),
            text = "주변 약국",
            modifier = Modifier.weight(1f),
            onClick = onFamilyManageClick
        )
    }
}

@Composable
fun QuickActionButton(
    icon: Painter,
    text: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = modifier
            .height(100.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CardBackground)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Image(
                painter = icon,
                contentDescription = text,
                modifier = Modifier.size(32.dp)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = text,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
fun MedicationHistory() {
    Column {
        Text(
            text = "오늘의 복용 기록",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(8.dp))
        Card(
            modifier = Modifier
                .fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = CardBackground)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "✅ 타이레놀 500mg  오전 9:00",
                    fontSize = 14.sp
                )
            }
        }
    }
}

@Composable
fun HealthChecklist() {
    Column {
        Text(
            text = "건강 체크리스트",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(8.dp))
        Card(
            modifier = Modifier
                .fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = CardBackground)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                ChecklistItem("두통")
                Spacer(modifier = Modifier.height(8.dp))
                ChecklistItem("피로감")
                Spacer(modifier = Modifier.height(8.dp))
                ChecklistItem("소화불량")
            }
        }
    }
}

@Composable
fun ChecklistItem(text: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "☐",
            fontSize = 20.sp
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = text,
            fontSize = 14.sp
        )
    }
}

@Preview(showBackground = true)
@Composable
fun MainScreenPreview() {
    MainScreen(
        navController = rememberNavController()
    )
}