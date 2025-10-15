package com.alyak.detector.ui.search

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.alyak.detector.R

@Composable
fun SearchBar(
    modifier: Modifier = Modifier,
    query: String = "",
    onQueryChange: (String) -> Unit = {},
    onMicClick: () -> Unit = {},
    onCameraClick: () -> Unit = {}
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp)
            .background(colorResource(R.color.white), shape = RoundedCornerShape(24.dp))
            .shadow(2.dp, shape = RoundedCornerShape(24.dp))
            .padding(horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Default.Search,
            contentDescription = null,
            tint = Color.Gray.copy(alpha = 0.3f), // 흐린 아이콘
            modifier = Modifier.size(28.dp)
        )

        Spacer(modifier = Modifier.width(8.dp))

        Box(
            modifier = Modifier.weight(1f),
            contentAlignment = Alignment.CenterStart
        ) {
            TextField(
                value = query,
                onValueChange = onQueryChange,
                placeholder = {
                    Text(
                        "약 이름, 성분, 식별 문자 검색",
                        color = Color.Gray.copy(alpha = 0.5f),
                    )
                },
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = colorResource(R.color.white),
                    unfocusedContainerColor = colorResource(R.color.white),
                    disabledContainerColor = colorResource(R.color.white),
                    cursorColor = colorResource(R.color.white),
                    focusedLabelColor = colorResource(R.color.white).copy(alpha = 0.6f),
                    unfocusedLabelColor = colorResource(R.color.white).copy(alpha = 0.4f),
                    focusedPlaceholderColor = colorResource(R.color.white).copy(alpha = 0.5f),
                    unfocusedPlaceholderColor = colorResource(R.color.white).copy(alpha = 0.5f),
                    unfocusedIndicatorColor = Color.Transparent
                ),
                textStyle = TextStyle(
                    fontSize = 12.sp
                ),
                singleLine = true,
                modifier = Modifier
                    .height(45.dp)
                    .fillMaxWidth(),
                //contentPadding = PaddingValues(vertical = 0.dp)
            )
        }

        Spacer(modifier = Modifier.width(4.dp))

        IconButton(onClick = onMicClick) {
            Icon(
                imageVector = Icons.Default.Mic,
                contentDescription = "음성 검색",
                tint = Color.Gray.copy(alpha = 0.3f),
                modifier = Modifier.size(28.dp)
            )
        }

        IconButton(onClick = onCameraClick) {
            Icon(
                imageVector = Icons.Default.CameraAlt,
                contentDescription = "사진 검색",
                tint = Color.Gray.copy(alpha = 0.3f),
                modifier = Modifier.size(28.dp)
            )
        }
    }

}

@Composable
@Preview(showBackground = true)
fun SearchBarPrev() {
    SearchBar()
}