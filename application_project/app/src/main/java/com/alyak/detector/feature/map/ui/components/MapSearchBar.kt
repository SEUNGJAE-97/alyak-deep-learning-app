package com.alyak.detector.feature.map.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
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
        trailing = {},
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
