package com.alyak.detector.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun CustomUnderlineTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    hint: String = "",
    textStyle: TextStyle = TextStyle(color = Color.Black, fontSize = 16.sp),
    underlineColor: Color = Color.Gray,
    textAlign: TextAlign = TextAlign.Start,
    trailingIcon: (@Composable (() -> Unit))? = null,
    visualTransformation: VisualTransformation = VisualTransformation.None
) {
    val mergedTextStyle = textStyle.merge(TextStyle(textAlign = textAlign))

    Column(modifier = modifier.fillMaxWidth()) {
        Box(
            Modifier
                .fillMaxWidth()
                .padding(bottom = 4.dp)
        ) {
            BasicTextField(
                value = value,
                onValueChange = onValueChange,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(end = 40.dp),
                textStyle = mergedTextStyle,
                visualTransformation = visualTransformation,
                decorationBox = { innerTextField ->
                    if (value.isEmpty() && hint.isNotEmpty()) {
                        Text(
                            text = hint,
                            color = Color.LightGray,
                            style = mergedTextStyle
                        )
                    }
                    innerTextField()
                }
            )
            if (trailingIcon != null) {
                Box(
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .padding(end = 4.dp)
                ) {
                    trailingIcon()
                }
            }
        }
        HorizontalDivider(
            color = underlineColor,
            thickness = 1.dp
        )
    }
}