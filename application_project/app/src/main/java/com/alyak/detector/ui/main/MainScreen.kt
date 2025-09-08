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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import com.alyak.detector.ui.other.BottomForm
import com.alyak.detector.ui.other.DashboardCard
import com.alyak.detector.ui.other.FloatingActionButton
import com.alyak.detector.ui.other.HeaderForm

@Composable
fun MainScreen(
    navController: NavController
) {
    val icons = listOf(
        Icons.Filled.Home,
        Icons.Filled.DateRange,
        Icons.Filled.FavoriteBorder,
        Icons.Filled.Settings
    )
    var selectedIndex by remember { mutableStateOf(0) }

    Scaffold(
        topBar = {
            HeaderForm()
        },
        bottomBar = {
            BottomForm(
                modifier = Modifier.fillMaxWidth(),
                icons = icons,
                selectedIndex = selectedIndex,
                onItemSelected = { selectedIndex = it }
            )
        },
        floatingActionButton = {
            FloatingActionButton()
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Color.White
                )
                .padding(paddingValues)  // Scaffold가 바텀바 높이만큼 패딩 자동 적용
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp)
        ) {
            NextMedicationCard()
            Spacer(modifier = Modifier.height(24.dp))

            DashboardButtonGrid(
                // Dashboard
                {
                    DashboardCard(
                        icon = Icons.Default.Map,
                        iconBackgroundColor = Color(0xFFD6D8FB),
                        title = "지도",
                        subtitle = "주변 의료기관",
                        onClick = { navController.navigate("MapScreen") }
                    )
                },
                {
                    DashboardCard(
                        icon = Icons.Default.Description,
                        iconBackgroundColor = Color(0xFFF3E8FE),
                        title = "복용 이력",
                        subtitle = "90%~",
                        onClick = {}
                    )
                },
                {
                    DashboardCard(
                        icon = Icons.Default.Search,
                        iconBackgroundColor = Color(0xFFFFF2D8),
                        title = "약 정보",
                        subtitle = "검색 및 등록",
                        onClick = { navController.navigate("pillSearch") }
                    )
                },
                {
                    DashboardCard(
                        icon = Icons.Default.Person,
                        iconBackgroundColor = Color(0xFFE6F3E7),
                        title = "가족 캐스팅",
                        subtitle = "가족 이력 관리",
                        onClick = {}
                    )
                }
            )
            Spacer(modifier = Modifier.height(24.dp))

            MedicationHistory()
            Spacer(modifier = Modifier.height(24.dp))
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
        colors = CardDefaults.cardColors(containerColor = colorResource(R.color.primaryBlue))
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
fun DashboardButtonGrid(
    button1: @Composable () -> Unit,
    button2: @Composable () -> Unit,
    button3: @Composable () -> Unit,
    button4: @Composable () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            button1()
            button2()
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            button3()
            button4()
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

@Preview(showBackground = true)
@Composable
fun MainScreenPreview() {
    MainScreen(
        navController = rememberNavController()
    )
}