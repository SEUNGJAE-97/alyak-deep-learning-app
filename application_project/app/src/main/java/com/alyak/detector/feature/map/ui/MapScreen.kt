package com.alyak.detector.feature.map.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.alyak.detector.ui.components.HeaderForm

@Composable
fun MapScreen(
    navController: NavController,
) {
    Scaffold(
        topBar = { HeaderForm("No Name") }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
        ) {

            KakaoMapView()
        }
    }

}

@Composable
@Preview(showBackground = true)
fun MapScreenPrev() {
    MapScreen(navController = rememberNavController())
}