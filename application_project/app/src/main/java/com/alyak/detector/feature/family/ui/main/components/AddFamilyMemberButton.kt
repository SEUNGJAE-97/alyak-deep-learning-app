package com.alyak.detector.feature.family.ui.main.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun AddFamilyMemberButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val unselectedBorder = Color(0xFFDDDDDD)
    val unselectedText = Color(0xFF9E9E9E)

    Column(
        modifier = modifier
            .width(80.dp)
            .clickable(onClick = onClick),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(75.dp)
                .border(
                    width = 2.dp,
                    color = unselectedBorder,
                    shape = CircleShape
                )
                .background(
                    color = Color(0xFFF8F8F8),
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "가족 추가",
                tint = Color(0xFF757575),
                modifier = Modifier.size(44.dp)
            )
        }
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = "가족 추가",
            fontWeight = FontWeight.Bold,
            color = unselectedText,
            fontSize = 15.sp
        )
    }
}