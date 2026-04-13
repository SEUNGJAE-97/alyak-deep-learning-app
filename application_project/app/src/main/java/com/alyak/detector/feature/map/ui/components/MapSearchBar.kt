package com.alyak.detector.feature.map.ui.components

import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.alyak.detector.ui.components.SearchBar

@Composable
fun MapSearchBar(
    modifier: Modifier = Modifier,
    query: String,
    onQueryChange: (String) -> Unit,
    onSearch: (String) -> Unit = {},
) {
    SearchBar(
        modifier = modifier,
        query = query,
        onQueryChange = onQueryChange,
        placeholder = "장소 검색",
        onSearch = onSearch,
        trailing = {
            IconButton(
                onClick = {
                    onQueryChange("")
                },
                modifier = Modifier.size(36.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "검색어 지우기",
                    tint = Color.Gray.copy(alpha = 0.7f),
                    modifier = Modifier.size(20.dp)
                )
            }
        },
    )
}

@Preview(showBackground = true)
@Composable
private fun MapSearchBarPreview() {
    MapSearchBar(
        query = "",
        onQueryChange = {},
    )
}
