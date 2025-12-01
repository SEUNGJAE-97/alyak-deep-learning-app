package com.alyak.detector.feature.pill.ui.search

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.alyak.detector.R
import com.alyak.detector.feature.map.ui.DragHandler
import com.alyak.detector.feature.pill.data.model.Pill
import com.alyak.detector.feature.pill.data.model.PillColor
import com.alyak.detector.feature.pill.data.model.PillLineType
import com.alyak.detector.feature.pill.data.model.PillShapeType
import com.alyak.detector.feature.pill.ui.search.components.FilterBar
import com.alyak.detector.feature.pill.ui.search.components.MarkingIcon
import com.alyak.detector.feature.pill.ui.search.components.PillInfoBox
import com.alyak.detector.feature.pill.ui.search.components.RecentSearch
import com.alyak.detector.feature.pill.ui.search.components.SearchActionButtons
import com.alyak.detector.feature.pill.ui.search.components.SearchBar
import com.alyak.detector.feature.pill.ui.search.components.ShapeIcon
import com.alyak.detector.ui.components.HeaderForm
import kotlinx.coroutines.launch

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun PillSearchScreen(
    navController: NavController,
    viewModel: PillSearchViewModel = hiltViewModel()
) {
    var selectedShape by remember { mutableStateOf(PillShapeType.entries.first()) }
    var selectedColor by remember { mutableStateOf(PillColor.entries.first()) }
    var selectedLine by remember { mutableStateOf(PillLineType.ALL) }
    val uiState by viewModel.recentSearchState.collectAsState()
    val scaffoldState = rememberBottomSheetScaffoldState()
    val searchUiState by viewModel.searchUiState.collectAsState()
    val scope = rememberCoroutineScope()
    var selectedIndex by remember { mutableStateOf(0) }

    BottomSheetScaffold(
        scaffoldState = scaffoldState,
        topBar = {
            HeaderForm("No name")
        },
        sheetPeekHeight = 100.dp,
        sheetContainerColor = Color.White,
        sheetShadowElevation = 10.dp,
        sheetShape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
        sheetDragHandle = { DragHandler() },
        sheetContent = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.9f)
                    .padding(16.dp)
            ) {
                when (searchUiState) {
                    is SearchUiState.Idle -> {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(min = 250.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.ic_launcher_foreground),
                                contentDescription = null,
                                modifier = Modifier
                                    .size(120.dp)
                                    .padding(bottom = 16.dp)
                            )
                            Text(
                                text = "검색 조건을 입력 후 검색해 주세요.",
                                fontSize = 16.sp,
                                color = Color.Gray
                            )
                        }
                    }
                    is SearchUiState.Loading -> {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            items(10) {
                                PillInfoBox(
                                    pillInfo = Pill(
                                        name = "",
                                        ingredient = "",
                                        manufacturer = "",
                                        pid = "",
                                        category = ""
                                    ),
                                    isLoading = true
                                )
                            }
                        }
                    }
                    is SearchUiState.Error -> {
                        Text(
                            text = "검색 중 오류가 발생했습니다: ${(searchUiState as SearchUiState.Error).message}",
                            color = Color.Red,
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                    is SearchUiState.Success -> {
                        val pills = (searchUiState as SearchUiState.Success).pills

                        if (pills.isEmpty()) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .heightIn(min = 250.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Image(
                                    painter = painterResource(id = R.drawable.ic_launcher_foreground),
                                    contentDescription = null,
                                    modifier = Modifier
                                        .size(120.dp)
                                        .padding(bottom = 16.dp)
                                )
                                Text(
                                    text = "검색 결과가 없습니다.",
                                    fontSize = 16.sp,
                                    color = Color.Gray
                                )
                            }
                        } else {
                            LazyColumn(
                                verticalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                items(pills) { pill ->
                                    PillInfoBox(pill)
                                }
                            }
                        }
                    }
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(
                    colorResource(R.color.white)
                )
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp)
        ) {
            Spacer(Modifier.height(20.dp))
            SearchBar()
            Spacer(Modifier.height(20.dp))
            FilterBar(
                entries = PillLineType.entries,
                selectedItem = selectedLine,
                labelSelector = { it.label },
                iconSelector = { item ->
                    if (item != PillLineType.ALL && item != PillLineType.ETC) {
                        MarkingIcon(
                            type = item,
                            modifier = Modifier
                                .size(24.dp)
                                .padding(end = 6.dp)
                        )
                    }
                },
                onItemClick = { selectedLine = it }
            )
            FilterBar(
                entries = PillColor.entries,
                selectedItem = selectedColor,
                labelSelector = { it.label },
                iconSelector = { item ->
                    if (item != PillColor.ALL && item != PillColor.TRANSPARENT) {
                        Box(
                            modifier = Modifier
                                .size(16.dp)
                                .clip(CircleShape)
                                .background(item.color)
                                .then(
                                    if (item.color == Color.White) {
                                        Modifier.border(1.dp, Color.LightGray, CircleShape)
                                    } else {
                                        Modifier
                                    }
                                )
                        )
                    }
                },
                onItemClick = { selectedColor = it }
            )
            FilterBar(
                entries = PillShapeType.entries,
                selectedItem = selectedShape,
                labelSelector = { it.label },
                iconSelector = { item ->
                    if (item != PillShapeType.ALL && item != PillShapeType.ETC) {
                        ShapeIcon(
                            shapeType = item,
                            modifier = Modifier
                                .size(24.dp)
                                .padding(end = 6.dp)
                        )
                    }
                },
                onItemClick = { selectedShape = it }
            )

            Spacer(Modifier.height(20.dp))

            SearchActionButtons(
                onResetClick = {
                    // 필터 초기화 로직
                    selectedShape = PillShapeType.entries.first()
                    selectedColor = PillColor.entries.first()
                    selectedLine = PillLineType.ALL
                },
                onSearchClick = {
                    viewModel.searchPills(selectedShape.name, selectedColor.name, selectedLine.name)
                    scope.launch {
                        scaffoldState.bottomSheetState.expand()
                    }
                },
                modifier = Modifier.padding(horizontal = 4.dp)
            )

            Spacer(Modifier.height(50.dp))

            when (val state = uiState) {
                is RecentSearchUiState.Loading -> {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        repeat(5) {
                            PillInfoBox(
                                pillInfo = Pill(
                                    name = "",
                                    ingredient = "",
                                    manufacturer = "",
                                    pid = "",
                                    category = ""
                                ),
                                isLoading = true
                            )
                        }
                    }
                }

                is RecentSearchUiState.Error -> {
                    Text(
                        text = "데이터를 불러오지 못했습니다.",
                        color = Color.Red,
                        modifier = Modifier.padding(16.dp)
                    )
                }

                is RecentSearchUiState.Success -> {
                    RecentSearch(
                        recentPills = state.pills,
                        onItemClick = { /* 이벤트 처리 */ }
                    )
                }
            }
        }
    }
}