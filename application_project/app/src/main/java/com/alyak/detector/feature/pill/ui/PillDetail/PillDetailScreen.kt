package com.alyak.detector.feature.pill.ui.PillDetail

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.alyak.detector.feature.pill.ui.PillDetail.components.CardBox
import com.alyak.detector.feature.pill.ui.PillDetail.components.PillDetailContent
import com.alyak.detector.ui.components.BottomForm
import com.alyak.detector.ui.components.HeaderForm
import com.alyak.detector.ui.components.MultiFloatingActionButton

@Composable
fun PillDetailScreen(
    pillId: Long,
    navController: NavController,
    viewModel: PillDetailViewModel = hiltViewModel()

) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            HeaderForm("김민수")
        },
        floatingActionButton = {
            MultiFloatingActionButton(navController)
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (uiState) {
                is PillDetailUiState.Loading -> {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("알약 정보를 불러오는 중...")
                    }
                }
                is PillDetailUiState.Error -> {
                    Box(Modifier.fillMaxSize().padding(16.dp), contentAlignment = Alignment.Center) {
                        Text("알약 정보를 불러오지 못했습니다. 다시 시도해 주세요.")
                    }
                }
                is PillDetailUiState.Success -> {
                    val medicineDetail = (uiState as PillDetailUiState.Success).detail
                    PillDetailContent(medicineDetail = medicineDetail)
                }
            }
        }
    }
}