package com.alyak.detector.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.alyak.detector.R

@Composable
fun FloatingActionButton(
    modifier: Modifier = Modifier,
    icon: ImageVector,
    contentDescription: String,
    backgroundColor: Color = colorResource(R.color.primaryBlue),
    iconTint: Color = Color.White,
    onClick: () -> Unit
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .size(64.dp)
            .background(backgroundColor, CircleShape)
            .clickable { onClick() }
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            tint = iconTint,
            modifier = Modifier.size(35.dp)
        )
    }
}

@Composable
fun MultiFloatingActionButton(
    navController: NavController
) {
    var expanded by remember { mutableStateOf(false) }

    Box(
        contentAlignment = Alignment.BottomEnd,
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.End,
        ) {
            if (expanded) {
                FloatingActionButton(
                    modifier = Modifier.size(50.dp),
                    icon = Icons.Filled.CameraAlt,
                    contentDescription = "수정",
                    onClick = {
                        navController.navigate("CameraScreen")
                    }
                )
                FloatingActionButton(
                    modifier = Modifier.size(50.dp),
                    icon = Icons.Filled.Map,
                    contentDescription = "수정",
                    onClick = {
                        navController.navigate("MapScreen")
                    }
                )
                FloatingActionButton(
                    modifier = Modifier.size(50.dp),
                    icon = Icons.Filled.Search,
                    contentDescription = "검색",
                    onClick = {
                        navController.navigate("PillSearchScreen")
                    }
                )
                FloatingActionButton(
                    modifier = Modifier.size(50.dp),
                    icon = Icons.Outlined.Person,
                    contentDescription = "수정",
                    onClick = { /* 동작2: 수정 */ }
                )
            }
            FloatingActionButton(
                icon = Icons.Filled.Add,
                contentDescription = "카메라",
                onClick = { expanded = !expanded }
            )
        }
    }
}
