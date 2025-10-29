package com.alyak.detector.ui.PillDetail

import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Column
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.alyak.detector.ui.other.BottomForm
import com.alyak.detector.ui.other.FloatingActionButton
import com.alyak.detector.ui.other.HeaderForm
import com.alyak.detector.ui.other.MultiFloatingActionButton

@Composable
fun PillDetailScreen() {
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
            MultiFloatingActionButton()
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
        ) {
            //
        }
    }
}

@Composable
@Preview(showBackground = true)
fun PillDetailPrev() {
    PillDetailScreen()
}