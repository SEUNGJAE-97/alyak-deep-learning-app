package com.alyak.detector.feature.map.ui

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.rememberBottomSheetScaffoldState
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
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.rememberAsyncImagePainter
import com.alyak.detector.R
import com.alyak.detector.feature.map.data.model.KakaoPlaceDto
import com.alyak.detector.feature.map.data.model.LocationDto
import com.alyak.detector.feature.map.ui.components.FilterButton
import com.alyak.detector.feature.map.ui.components.HospitalInfo
import com.alyak.detector.feature.map.ui.components.MapSearchBar
import com.alyak.detector.feature.map.ui.model.MapPlaceFilter
import com.alyak.detector.ui.components.CustomButton
import com.alyak.detector.ui.components.HeaderForm

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapScreen(
    navController: NavController,
    viewModel: MapViewModel = hiltViewModel()
) {
    val name by viewModel.userName.collectAsState()
    val displayedPlaces by viewModel.displayedPlaces.collectAsState()
    val placeFilter by viewModel.placeFilter.collectAsState()
    val showSearchFromCurrentLocation by viewModel.showSearchFromCurrentLocationButton.collectAsState()
    var mapSearchQuery by remember { mutableStateOf("") }
    val scaffoldState = rememberBottomSheetScaffoldState()
    BottomSheetScaffold(
        scaffoldState = scaffoldState,
        sheetContainerColor = Color.White,
        sheetContentColor = Color.Black,
        sheetShape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        sheetShadowElevation = 10.dp,
        sheetPeekHeight = 200.dp,
        sheetContent = {
            HospitalListContent(
                hospitals = displayedPlaces,
                placeFilter = placeFilter,
                viewModel = viewModel,
            )
        },
        topBar = { HeaderForm(name) },
        sheetDragHandle = { DragHandler() }
    ) { paddingValues ->
        Column {
            Box(
                modifier = Modifier
                    .padding(paddingValues)
                    .fillMaxSize()
            ) {
                KakaoMapView(viewModel = viewModel)
                Column(
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.Top
                ) {
                    MapSearchBar(
                        query = mapSearchQuery,
                        onQueryChange = { mapSearchQuery = it },
                        onSearch = { /* 장소 키워드 검색 연동 시 구현 */ },
                    )
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 16.dp, start = 16.dp, end = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        MapPlaceFilter.entries.forEach { filter ->
                            FilterButton(
                                text = filter.chipLabel,
                                isSelected = placeFilter == filter,
                                onClick = { viewModel.setPlaceFilter(filter) },
                            )
                        }
                    }
                }
                if (showSearchFromCurrentLocation) {
                    SearchFromCurrentLocationButton(
                        onClick = { viewModel.fetchPlacesAroundCurrentLocation() },
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(bottom = 20.dp),
                    )
                }
                CustomButton(
                    text = "",
                    onClick = {
                        viewModel.fetchLocation()
                    },
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
private fun SearchFromCurrentLocationButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val shape = RoundedCornerShape(24.dp)
    Button(
        onClick = onClick,
        modifier = modifier
            .shadow(5.dp, shape)
            .clip(shape),
        shape = shape,
        colors = ButtonDefaults.buttonColors(
            containerColor = colorResource(R.color.white),
            contentColor = Color.Black,
        ),
        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 3.dp),
        elevation = ButtonDefaults.buttonElevation(
            defaultElevation = 0.dp,
            pressedElevation = 0.dp,
            focusedElevation = 0.dp,
            hoveredElevation = 0.dp,
        ),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = null,
                modifier = Modifier.size(24.dp),
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "현재 위치에서 검색",
                color = Color.Black,
            )
        }
    }
}

@Composable
fun HospitalListContent(
    hospitals: List<KakaoPlaceDto>,
    placeFilter: MapPlaceFilter,
    viewModel: MapViewModel,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(0.9f)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "총 ${hospitals.size}개의 ${placeFilter.sheetCountNoun}",
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
        LazyColumn(
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(bottom = 20.dp)
        ) {
            items(hospitals.size) { index ->
                HospitalInfo(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                    hospitalName = hospitals[index].place_name,
                    hospitalAddress = hospitals[index].address_name,
//                    hospitalDepartment = hospitals[index].category_name.
                    hospitalDepartment = arrayListOf("안과", "정형외과"),
                    hospitalDistance = hospitals[index].distance,
                    onClick = {
                        Log.d(
                            "HospitalInfo",
                            "길찾기 버튼 클릭됨 - 좌표: ${hospitals[index].y}, ${hospitals[index].x}"
                        )
                        val destination = LocationDto(
                            hospitals[index].y.toDouble(),
                            hospitals[index].x.toDouble()
                        )
                        viewModel.findPath(destination, hospitals[index].id)
                    }
                )
            }
        }
    }
}

@Composable
fun DragHandler() {
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
}


@Composable
@Preview(showBackground = true)
fun MapScreenPrev() {
    MapScreen(navController = rememberNavController())
}