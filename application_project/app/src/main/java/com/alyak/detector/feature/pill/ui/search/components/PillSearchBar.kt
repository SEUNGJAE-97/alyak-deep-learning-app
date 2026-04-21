package com.alyak.detector.feature.pill.ui.search.components

import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.alyak.detector.ui.components.SearchBar

@Composable
fun PillSearchBar(
    modifier: Modifier = Modifier,
    query: String,
    onQueryChange: (String) -> Unit,
    onSearch: (String) -> Unit,
    onCameraClick: () -> Unit = {},
) {
    SearchBar(
        modifier = modifier,
        query = query,
        onQueryChange = onQueryChange,
        placeholder = "약 이름, 성분 검색",
        onSearch = onSearch,
        trailing = {
            IconButton(
                onClick = onCameraClick,
                modifier = Modifier.size(36.dp),
            ) {
                Icon(
                    imageVector = Icons.Default.CameraAlt,
                    contentDescription = "사진 검색",
                    tint = Color.Gray.copy(alpha = 0.5f),
                    modifier = Modifier.size(24.dp),
                )
            }
        },
    )
}

@Composable
fun getAnnotatedString(
    fullText: String,
    query: String,
    highlightColor: Color = Color(0xFF6200EE)
): AnnotatedString {
    return buildAnnotatedString {
        if (query.isEmpty()) {
            append(fullText)
            return@buildAnnotatedString
        }

        // 대소문자 무시하고 query 위치 탐색
        val lowerFull = fullText.lowercase()
        val lowerQuery = query.lowercase()
        val startIndex = lowerFull.indexOf(lowerQuery)

        if (startIndex == -1) {
            // query가 fullText에 없으면 그냥 전체 출력
            append(fullText)
            return@buildAnnotatedString
        }

        val endIndex = startIndex + query.length

        // query 앞부분
        append(fullText.substring(0, startIndex))

        // query 매칭 부분 하이라이트
        withStyle(style = SpanStyle(color = highlightColor, fontWeight = FontWeight.Bold)) {
            append(fullText.substring(startIndex, endIndex))
        }

        // query 뒷부분
        append(fullText.substring(endIndex))
    }
}

@Preview(showBackground = true)
@Composable
private fun PillSearchBarPreview() {
    PillSearchBar(
        query = "",
        onQueryChange = {},
        onSearch = {},
    )
}
