import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.RemoveCircle
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.alyak.detector.R
import com.alyak.detector.feature.notification.data.model.MealTime
import com.alyak.detector.feature.notification.data.model.MedicationTimeEntry
import com.alyak.detector.feature.notification.ui.MedicineStatisticsViewModel

// 이미지 기반 커스텀 색상
val SoftBlue = Color(0xFF6371C2) // "건강한 습관의 시작" 타이틀 색상
val BgGray = Color(0xFFF9FAFF)  // 전체 배경색
val CardBg = Color(0xFFFFFFFF)  // 카드 배경
val LightPurpleBg = Color(0xFFF1F3FF) // 알림 설정 배경
val TextMain = Color(0xFF333D79) // "약 이름", "복약 시간" 등 메인 텍스트

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MedicineStatisticsScreen(
    navController: NavController,
    viewModel: MedicineStatisticsViewModel = viewModel()
) {
    var medicineName by remember { mutableStateOf("") }
    var isAlarmEnabled by remember { mutableStateOf(true) }
    val timeEntries by viewModel.timeEntries.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("복약 일정 추가", fontWeight = FontWeight.Bold, fontSize = 18.sp) },
                navigationIcon = {
                    IconButton(onClick = { /* 뒤로가기 */ }) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = null,
                            tint = colorResource(R.color.black)
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = BgGray)
            )
        },
        bottomBar = {
            Button(
                onClick = { /* 저장 로직 */ },
                enabled = timeEntries.isNotEmpty(),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
                    .height(56.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = colorResource(R.color.primaryBlue))
            ) {
                Text(
                    "저장하기",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.White
                )
            }
        },
        containerColor = BgGray
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 24.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(10.dp))

            // 1. 상단 배너 카드
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .shadow(elevation = 2.dp, shape = RoundedCornerShape(24.dp))
                    .clip(RoundedCornerShape(24.dp))
                    .background(Color.White)
            ) {
                // 배경 이미지 (알약 이미지)
                Image(
                    painter = painterResource(id = R.drawable.banner), // 실제 배너 이미지로 교체
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop,
                    alpha = 0.6f
                )

                Column(
                    modifier = Modifier
                        .padding(24.dp)
                        .align(Alignment.CenterStart)
                ) {
                    Text(
                        text = "건강한 습관의 시작",
                        fontFamily = FontFamily(
                            Font(
                                R.font.pretendard_extrabold,
                                FontWeight.ExtraBold
                            )
                        ),
                        fontSize = 26.sp,
                        color = colorResource(R.color.main_blue)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "새로운 복약 일정을 추가해 주세요.",
                        fontFamily = FontFamily(Font(R.font.pretendard_semibold, FontWeight.Thin)),
                        fontSize = 14.sp,
                        color = Color.White,
                        fontWeight = FontWeight.Thin
                    )
                }
            }

            Spacer(modifier = Modifier.height(30.dp))

            OutlinedTextField(
                value = medicineName,
                onValueChange = { medicineName = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp),
                placeholder = {
                    Text(
                        text = "어떤 약을 드시나요?",
                        color = Color.LightGray,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 10.dp),
                        textAlign = TextAlign.Start
                    )
                },
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = colorResource(R.color.primaryBlue),
                    unfocusedBorderColor = colorResource(R.color.black),
                    disabledBorderColor = Color(0xFFDEE2E6),
                ),
                trailingIcon = {
                    Icon(
                        imageVector = Icons.Default.CameraAlt,
                        contentDescription = "사진 검색",
                        tint = Color.Gray.copy(alpha = 0.5f),
                        modifier = Modifier
                            .size(24.dp)
                            .clickable(onClick = { navController.navigate("CameraScreen") }),
                    )
                }
            )

            Spacer(modifier = Modifier.height(30.dp))

            // 3. 복약 시간 섹션
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "복약 시간",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = colorResource(R.color.primaryBlue)
                )

                Surface(
                    modifier = Modifier
                        .size(34.dp)
                        .clickable(enabled = timeEntries.size < MealTime.entries.size) {
                            showAddDialog = true
                        },
                    shape = CircleShape,
                    color = if (timeEntries.size < MealTime.entries.size)
                        Color(0xFFE8EAF6) else Color(0xFFEEEEEE)
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = null,
                        modifier = Modifier.padding(6.dp),
                        tint = if (timeEntries.size < MealTime.entries.size) SoftBlue else Color.Gray
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (timeEntries.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(80.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("복약 시간을 추가해주세요.", color = Color.Gray, fontSize = 14.sp)
                }
            } else {
                timeEntries.forEach { entry ->
                    MedicationTimeItem(
                        entry = entry,
                        onRemove = { viewModel.removeTimeEntry(entry.id) }
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }

            Spacer(modifier = Modifier.height(30.dp))

            // 4. 알림 설정 섹션
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .background(LightPurpleBg)
                    .padding(20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    modifier = Modifier.size(44.dp),
                    shape = CircleShape,
                    color = Color.White
                ) {
                    Icon(
                        Icons.Default.Notifications,
                        contentDescription = null,
                        modifier = Modifier.padding(10.dp),
                        tint = SoftBlue
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text("알림 설정", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = TextMain)
                    Text("복약 시간을 놓치지 않게 알려드려요.", fontSize = 12.sp, color = Color.Gray)
                }

                Switch(
                    checked = isAlarmEnabled,
                    onCheckedChange = { isAlarmEnabled = it },
                    colors = SwitchDefaults.colors(
                        checkedTrackColor = SoftBlue
                    )
                )
            }

            Spacer(modifier = Modifier.height(100.dp)) // 버튼 여유 공간
            if (showAddDialog) {
                val availableMealTimes = MealTime.entries.filter { mealTime ->
                    timeEntries.none { it.mealTime == mealTime }
                }

                AlertDialog(
                    onDismissRequest = { showAddDialog = false },
                    title = { Text("시간대 추가") },
                    text = {
                        Column {
                            availableMealTimes.forEach { mealTime ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            val (hour, minute) = when (mealTime) {
                                                MealTime.MORNING -> 8 to 0
                                                MealTime.LUNCH -> 13 to 0
                                                MealTime.DINNER -> 19 to 0
                                            }
                                            viewModel.addTimeEntry(mealTime, hour, minute)
                                            showAddDialog = false
                                        }
                                        .padding(vertical = 12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = mealTime.icon,
                                        contentDescription = null,
                                        tint = colorResource(mealTime.tint)
                                    )
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Text(mealTime.name)
                                }
                            }
                        }
                    },
                    confirmButton = {}
                )
            }
        }
    }
}

@Composable
fun MedicationTimeItem(
    entry: MedicationTimeEntry,
    onRemove: () -> Unit,
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = CardBg,
        shadowElevation = 1.dp
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier.size(48.dp),
                shape = CircleShape,
                color = colorResource(entry.mealTime.backgroundColor)
            ) {
                Icon(
                    imageVector = entry.mealTime.icon,
                    contentDescription = null,
                    modifier = Modifier.padding(12.dp),
                    tint = colorResource(entry.mealTime.tint)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column {
                Text(
                    entry.mealTime.name,
                    fontSize = 11.sp,
                    color = Color.Gray,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    entry.displayTime,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextMain
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            Icon(
                Icons.Default.RemoveCircle,
                contentDescription = null,
                tint = Color(0xFFD1D1D1),
                modifier = Modifier
                    .size(24.dp)
                    .clickable { onRemove() }
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewAddMedication() {
    MedicineStatisticsScreen(navController = rememberNavController())
}