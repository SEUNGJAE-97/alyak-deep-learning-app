package com.alyak.detector.feature.map.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBar
import androidx.compose.material3.Text
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.alyak.detector.R
import com.alyak.detector.feature.map.ui.components.FilterButton
import com.alyak.detector.ui.components.HeaderForm
import com.alyak.detector.feature.pill.ui.search.components.SearchBar
import com.alyak.detector.ui.components.CustomButton
import coil.compose.rememberAsyncImagePainter
import coil.decode.SvgDecoder
import com.alyak.detector.feature.map.ui.components.HospitalInfo

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapScreen(
    navController: NavController,
) {
    val scaffoldState = rememberBottomSheetScaffoldState()
    BottomSheetScaffold(
        scaffoldState = scaffoldState,
        // --- [핵심 1] 시트 디자인 (흰색 배경, 둥근 모서리) ---
        sheetContainerColor = Color.White,
        sheetContentColor = Color.Black,
        sheetShape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        sheetShadowElevation = 10.dp,
        // --- [핵심 2] 시트가 처음 보일 높이 (Peek Height) ---
        sheetPeekHeight = 200.dp, // 처음에 지도 밑에 얼마나 깔려있을지 설정 (적절히 조절)
        // --- [핵심 3] 시트 안에 들어갈 내용 (리스트) ---
        sheetContent = {
            HospitalListContent() // 아래에서 만들 함수 호출
        },
        topBar = { HeaderForm("No Name") }
    ) { paddingValues ->
        Column {
            Box(
                modifier = Modifier
                    .padding(paddingValues)
                    .fillMaxSize()
            ) {
                KakaoMapView()
                Column(
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.Top
                ) {
                    SearchBar()
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 16.dp, start = 16.dp, end = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        FilterButton("전체", isSelected = true, onClick = {})
                        FilterButton("병원", isSelected = false, onClick = {})
                        FilterButton("약국", isSelected = false, onClick = {})
                        FilterButton("영업중", isSelected = false, onClick = {})
                    }
                }
                CustomButton(
                    text = "",
                    onClick = { /** TODO : 현재 위치를 갱신하고 줌인 해줄때 사용 */ },
                    image = rememberAsyncImagePainter(model = R.drawable.my_location),
                    containerColor = colorResource(R.color.white),
                    shape = CircleShape,
                    modifier = Modifier
                        .padding(20.dp)
                        .shadow(5.dp, CircleShape)
                        .clip(CircleShape)
                        .background(color = Color.White, shape = CircleShape)
                        .align(Alignment.BottomEnd)
                )
            }
        }
    }

}
@Composable
fun HospitalListContent() {
    // 임시 데이터 (실제로는 ViewModel 등에서 받아오겠죠?)
    val hospitalList = List(8) { index ->
        "병원 ${index + 1}"
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(0.9f) // 시트를 최대로 올렸을 때 화면의 90%까지만 차지
    ) {
        // 1. 드래그 핸들 (회색 작은 막대기)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 10.dp),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .width(40.dp)
                    .height(4.dp)
                    .background(Color.LightGray, RoundedCornerShape(2.dp))
            )
        }

        // 2. 헤더 (총 8개의 의료기관 | 거리순)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "총 ${hospitalList.size}개의 의료기관",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("거리순", fontSize = 12.sp, color = Color.Gray)
                Icon(
                    imageVector = Icons.Default.KeyboardArrowDown,
                    contentDescription = null,
                    tint = Color.Gray,
                    modifier = Modifier.size(16.dp)
                )
            }
        }

        // 3. 스크롤 가능한 병원 리스트
        LazyColumn(
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(bottom = 20.dp) // 맨 아래 여백
        ) {
            items(hospitalList.size) { index ->
                // 만드신 컴포넌트 사용
                HospitalInfo(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                    hospitalName = "연세세브란스병원",
                    hospitalAddress = "서울특별시 서대문구 신촌동 134",
                    hospitalDepartment = arrayListOf("내과", "외과", "정형외과")
                )
            }
        }
    }
}
@Composable
@Preview(showBackground = true)
fun MapScreenPrev() {
    MapScreen(navController = rememberNavController())
}