package com.alyak.detector.feature.pill.ui.PillDetail

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.alyak.detector.feature.pill.ui.PillDetail.components.PillDetailContent
import com.alyak.detector.ui.components.HeaderForm
import com.alyak.detector.ui.components.MultiFloatingActionButton

@Composable
fun PillDetailScreen(
    pillId: Long,
    navController: NavController,
    viewModel: PillDetailViewModel = hiltViewModel()

) {
    val uiState by viewModel.uiState.collectAsState()
    var isExpanded by remember { mutableStateOf(false) }
    val name by viewModel.userName.collectAsState()
    Scaffold(
        topBar = {
            HeaderForm(name)
        },
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
                    Box(
                        Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
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
    MultiFloatingActionButton(navController)
}