package com.alyak.detector.ui.other

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.alyak.detector.R

@Composable
fun FloatingActionButton(modifier: Modifier = Modifier){
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .size(64.dp)
            .background(colorResource(R.color.primaryBlue), CircleShape)
    ) {
        Icon(
            imageVector = Icons.Filled.CameraAlt, // 카메라 아이콘
            contentDescription = "카메라",
            tint = Color.White,
            modifier = Modifier.size(35.dp) // 아이콘 크기
        )
    }
}

@Composable
@Preview(showBackground = true)
fun FloatingActionButtonPrev(){
    FloatingActionButton()
}