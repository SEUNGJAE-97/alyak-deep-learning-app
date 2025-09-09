package com.alyak.detector.ui.family

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun FamilyMemberBtn(
    modifier: Modifier = Modifier,
    role: String,
    name: String,
    isSelected: Boolean
){
    val selectedColor = Color(0xFF5864D9)
    val unselectedBorder = Color(0xFFDDDDDD)
    val unselectedText = Color(0xFF9E9E9E)

    Column(
        modifier = modifier.width(100.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(75.dp)
                .border(
                    width = 2.dp,
                    color = if (isSelected) selectedColor else unselectedBorder,
                    shape = CircleShape
                )
                .background(
                    color = if (isSelected) selectedColor.copy(alpha = 0.09f) else Color(0xFFF8F8F8),
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            androidx.compose.material3.Icon(
                imageVector = androidx.compose.material.icons.Icons.Default.Person,
                contentDescription = null,
                tint = if (isSelected) selectedColor else Color(0xFF757575),
                modifier = Modifier.size(44.dp)
            )
        }
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = role,
            fontWeight = FontWeight.Bold,
            color = if (isSelected) selectedColor else unselectedText,
            fontSize = 20.sp
        )
        Text(
            text = name,
            color = if (isSelected) Color.Black else unselectedText,
            fontSize = 16.sp
        )
    }

}

@Composable
@Preview(showBackground =true)
fun FamilyMemberPrev(){
    Row(
        horizontalArrangement = Arrangement.spacedBy(18.dp),
        modifier = Modifier.padding(16.dp)
    ) {
        FamilyMemberBtn(role = "할머니", name = "김순자", isSelected = true)
        FamilyMemberBtn(role = "할아버지", name = "하이고", isSelected = false)
        FamilyMemberBtn(role = "아버지", name = "하태영", isSelected = false)
    }
}
