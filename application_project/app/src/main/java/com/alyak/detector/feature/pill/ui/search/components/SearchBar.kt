package com.alyak.detector.feature.pill.ui.search.components

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
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.SearchBar
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
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
    // BasicTextField는 장식(배경, 테두리 등)이 없는 순수 입력 필드입니다.
    BasicTextField(
        value = query,
        onValueChange = onQueryChange,
        textStyle = TextStyle(
            fontSize = 14.sp,
            color = Color.Black // 텍스트 색상
        ),
        singleLine = true,
        cursorBrush = SolidColor(colorResource(R.color.primaryBlue)), // 커서 색상
        decorationBox = { innerTextField ->
            // decorationBox 안에서 디자인(아이콘, 배경 등)을 모두 구성합니다.
            Row(
                modifier = modifier
                    .fillMaxWidth()
                    .height(50.dp) // 전체 높이 고정
                    .shadow(4.dp, shape = RoundedCornerShape(24.dp)) // 그림자
                    .background(color = Color.White, shape = RoundedCornerShape(24.dp)) // 배경 및 모양
                    .padding(horizontal = 12.dp), // 내부 좌우 여백
                verticalAlignment = Alignment.CenterVertically
            ) {
                // 1. 검색 아이콘
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "검색",
                    tint = Color.Gray.copy(alpha = 0.5f),
                    modifier = Modifier.size(24.dp)
                )

                Spacer(modifier = Modifier.width(8.dp))

                // 2. 입력 필드 및 placeholder 영역
                Box(
                    modifier = Modifier.weight(1f),
                    contentAlignment = Alignment.CenterStart
                ) {
                    // 텍스트가 비어있을 때만 Placeholder 표시
                    if (query.isEmpty()) {
                        Text(
                            text = "약 이름, 성분, 식별 문자 검색",
                            color = Color.Gray.copy(alpha = 0.5f),
                            fontSize = 14.sp
                        )
                    }
                    // 실제 텍스트 입력 컴포넌트 위치
                    innerTextField()
                }

                // 3. 우측 아이콘들 (마이크, 카메라)
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = onMicClick,
                        modifier = Modifier.size(36.dp) // 터치 영역 확보를 위해 약간 크게
                    ) {
                        Icon(
                            imageVector = Icons.Default.Mic,
                            contentDescription = "음성 검색",
                            tint = Color.Gray.copy(alpha = 0.5f),
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(4.dp))

                    IconButton(
                        onClick = onCameraClick,
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.CameraAlt,
                            contentDescription = "사진 검색",
                            tint = Color.Gray.copy(alpha = 0.5f),
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }
        }
    )
}

@Composable
@Preview(showBackground = true)
fun SearchBarPrev() {
    SearchBar()
}