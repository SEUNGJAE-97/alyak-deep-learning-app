package com.alyak.detector.feature.pill.ui.search

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBar
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.alyak.detector.R
import com.alyak.detector.feature.pill.data.model.PillCategory
import com.alyak.detector.feature.pill.data.model.PillColor
import com.alyak.detector.feature.pill.data.model.PillLineType
import com.alyak.detector.feature.pill.data.model.PillShapeType
import com.alyak.detector.feature.pill.ui.search.components.FilterBar
import com.alyak.detector.feature.pill.ui.search.components.MarkingIcon
import com.alyak.detector.feature.pill.ui.search.components.RecentSearch
import com.alyak.detector.feature.pill.ui.search.components.SearchBar
import com.alyak.detector.feature.pill.ui.search.components.ShapeIcon
import com.alyak.detector.ui.components.BottomForm
import com.alyak.detector.ui.components.HeaderForm
import com.alyak.detector.ui.components.MultiFloatingActionButton

@Composable
fun PillSearchScreen(navController: NavController) {
    var selectedShape by remember { mutableStateOf(PillShapeType.entries.first()) }
    var selectedColor by remember { mutableStateOf(PillColor.entries.first()) }
    var selectedLine by remember { mutableStateOf(PillLineType.ALL) }
    var selectedCategory by remember { mutableStateOf(PillCategory.entries.first()) }
    val icons = listOf(
        Icons.Filled.Home,
        Icons.Filled.DateRange,
        Icons.Filled.FavoriteBorder,
        Icons.Filled.Settings
    )
    var selectedIndex by remember { mutableStateOf(0) }

    Scaffold(
        topBar = {
            HeaderForm("No name")
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

            Spacer(Modifier.height(30.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("빠른 검색", fontWeight = FontWeight.Bold, fontSize = 20.sp)
                Text(
                    "더보기",
                    fontWeight = FontWeight.Thin,
                    color = colorResource(R.color.primaryBlue)
                )
            }

            FilterBar(
                entries = PillCategory.entries,
                labelSelector = { it.name },
                iconSelector = { category ->
                    Image(
                        painter = painterResource(id = category.image),
                        contentDescription = null,
                        modifier = Modifier.size(24.dp)
                    )
                },
                selectedItem = selectedCategory,
                onItemClick = { clickedItem ->
                    selectedCategory = clickedItem
                },

                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 10.dp)
            )
            RecentSearch()
        }
    }
}