package com.alyak.detector.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.alyak.detector.R
import com.alyak.detector.ui.components.ContentBox
import com.alyak.detector.ui.components.CustomButton
import com.alyak.detector.ui.components.CustomUnderlineTextField

@Composable
fun ValidationForm() {
    var email by remember { mutableStateOf("") }

    ContentBox {
        Column {
            Text(
                text = "이메일",
                color = colorResource(R.color.primaryBlue)
            )

            Spacer(modifier = Modifier.height(30.dp))

            CustomUnderlineTextField(
                value = email,
                onValueChange = { email = it },
                hint = "email",
                trailingIcon = {
                    Icon(
                        painter = painterResource(R.drawable.check),
                        modifier = Modifier.size(24.dp),
                        contentDescription = "check email state"
                    )
                }
            )

            Spacer(modifier = Modifier.height(30.dp))

            Text(
                text = "인증번호",
                color = colorResource(R.color.primaryBlue)
            )

            Spacer(modifier = Modifier.height(30.dp))

            val (values, setValues) = remember { mutableStateOf(listOf("4", "8", "6", "1")) }

            NumberInputRow(
                values = values,
                onValueChange = { index, newValue ->
                    setValues(values.toMutableList().also { it[index] = newValue })
                }
            )

            Spacer(modifier = Modifier.height(30.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    modifier = Modifier
                        .size(65.dp)
                        .clickable { },
                    shape = RoundedCornerShape(50.dp),
                    color = colorResource(R.color.white),
                    shadowElevation = 2.dp
                ) {
                    Image(
                        painter = painterResource(R.drawable.arrow_back),
                        contentDescription = "뒤로가기",
                        modifier = Modifier
                            .padding(12.dp)
                            .size(30.dp)
                    )
                }

                Spacer(modifier = Modifier.padding(8.dp))

                CustomButton(
                    text = "",
                    onClick = { /* TODO: 인증코드 전송 로직 */ },
                    image = painterResource(R.drawable.arrow),
                    containerColor = colorResource(R.color.primaryBlue),
                    modifier = Modifier.size(80.dp),
                    shape = RoundedCornerShape(50.dp),
                    imageSize = 40.dp
                )
            }

        }
    }

}

@Composable
fun NumberInputRow(
    values: List<String>,
    onValueChange: (Int, String) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        values.forEachIndexed { index, value ->
            SquareNumberBox(
                value = value,
                onValueChange = { newValue -> onValueChange(index, newValue) }
            )
        }
    }
}

@Composable
fun SquareNumberBox(
    value: String,
    onValueChange: (String) -> Unit
) {
    TextField(
        value = value,
        onValueChange = { newValue ->
            if (newValue.all { it.isDigit() }) {
                onValueChange(newValue)
            }
        },
        modifier = Modifier
            .size(80.dp)
            .border(
                2.dp,
                colorResource(R.color.primaryBlue),
                RoundedCornerShape(12.dp)
            ),
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        textStyle = TextStyle(
            fontSize = 22.sp,
            textAlign = TextAlign.Center
        ),
        colors = TextFieldDefaults.colors(
            focusedContainerColor = colorResource(R.color.white),
            unfocusedContainerColor = colorResource(R.color.white)
        )
    )
}


@Composable
@Preview(showBackground = true)
fun ValidationFormPreview() {
    ValidationForm()
}