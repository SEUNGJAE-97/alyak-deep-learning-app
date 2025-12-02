package com.alyak.detector.feature.family.ui.main.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun PillIntakeBadge(
    icon: ImageVector,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(Color.White)
    ) {
        Row(
            modifier = Modifier
        ) {
            Icon(
                modifier = Modifier,
                imageVector = icon,
                contentDescription = null,
            )
        }
    }
}


@Composable
@Preview(showBackground = true)
fun PillIntakeBadgePreview() {
    //PillIntakeBadge()
}