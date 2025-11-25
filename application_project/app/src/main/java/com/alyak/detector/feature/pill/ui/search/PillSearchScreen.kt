package com.alyak.detector.feature.pill.ui.search

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.alyak.detector.R
import com.alyak.detector.feature.pill.data.model.PillShapeType
import com.alyak.detector.feature.pill.ui.search.components.FilterBar
import com.alyak.detector.feature.pill.ui.search.components.QuickSearch
import com.alyak.detector.feature.pill.ui.search.components.SearchBar
import com.alyak.detector.ui.components.BottomForm
import com.alyak.detector.ui.components.MultiFloatingActionButton

@Composable
fun PillSearchScreen(navController: NavController) {
    val icons = listOf(
        Icons.Filled.Home,
        Icons.Filled.DateRange,
        Icons.Filled.FavoriteBorder,
        Icons.Filled.Settings
    )
    var selectedIndex by remember { mutableStateOf(0) }

    Scaffold(
        topBar = {
            // 검색창
            SearchBar()
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
            MultiFloatingActionButton(navController)
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
            //외형 , 색상 등등 버튼
            // 가로로 스크롤 되도록 3줄
            FilterBar(
                PillShapeType.entries,
                { it.label },
                { rememberVectorPainter(it.icon) },
                {/*TODO : 단순 색상 변경 및 상태를 저장 해둬야함*/ }
            )
            FilterBar(
                PillShapeType.entries,
                { it.label },
                { rememberVectorPainter(it.icon) },
                {/*TODO : 단순 색상 변경 및 상태를 저장 해둬야함*/ }
            )
            FilterBar(
                PillShapeType.entries,
                { it.label },
                { rememberVectorPainter(it.icon) },
                {/*TODO : 단순 색상 변경 및 상태를 저장 해둬야함*/ }
            )
            // 빠른 검색 ( 두통약, 소화제, 해열제....)
            QuickSearch()
            //최근 검색
        }
    }
}