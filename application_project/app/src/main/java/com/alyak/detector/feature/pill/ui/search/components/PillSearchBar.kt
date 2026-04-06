package com.alyak.detector.feature.pill.ui.search.components

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.alyak.detector.ui.components.SearchBar

@Composable
fun PillSearchBar(
    modifier: Modifier = Modifier,
    query: String,
    onQueryChange: (String) -> Unit,
    onSearch: (String) -> Unit,
    onMicClick: () -> Unit = {},
    onCameraClick: () -> Unit = {},
) {
    SearchBar(
        modifier = modifier,
        query = query,
        onQueryChange = onQueryChange,
        placeholder = "약 이름, 성분, 식별 문자 검색",
        onSearch = onSearch,
        trailing = {
            IconButton(
                onClick = onMicClick,
                modifier = Modifier.size(36.dp),
            ) {
                Icon(
                    imageVector = Icons.Default.Mic,
                    contentDescription = "음성 검색",
                    tint = Color.Gray.copy(alpha = 0.5f),
                    modifier = Modifier.size(24.dp),
                )
            }

            Spacer(modifier = Modifier.width(4.dp))

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

@Preview(showBackground = true)
@Composable
private fun PillSearchBarPreview() {
    PillSearchBar(
        query = "",
        onQueryChange = {},
        onSearch = {},
    )
}
