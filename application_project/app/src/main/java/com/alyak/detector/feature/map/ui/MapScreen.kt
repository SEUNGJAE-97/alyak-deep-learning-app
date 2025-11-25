package com.alyak.detector.feature.map.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.alyak.detector.feature.map.ui.components.FilterButton
import com.alyak.detector.ui.components.HeaderForm
import com.alyak.detector.feature.pill.ui.search.components.SearchBar

@Composable
fun MapScreen(
    navController: NavController,
) {
    Scaffold(
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
            }
        }
    }

}

@Composable
@Preview(showBackground = true)
fun MapScreenPrev() {
    MapScreen(navController = rememberNavController())
}