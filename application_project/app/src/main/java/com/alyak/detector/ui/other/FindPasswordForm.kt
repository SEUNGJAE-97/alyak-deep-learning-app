package com.alyak.detector.ui.other

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.alyak.detector.R
import com.alyak.detector.ui.components.ContentBox
import com.alyak.detector.ui.components.CustomButton
import com.alyak.detector.ui.components.CustomUnderlineTextField

@Composable
fun FindPasswordForm(
) {
    var password by remember { mutableStateOf("") }
    var isPasswordVisible by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.Bottom,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        ContentBox(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = "비밀번호",
                color = colorResource(R.color.primaryBlue)
            )

            CustomUnderlineTextField(
                value = password,
                onValueChange = {
                    password = it
                },
                hint = "새 패스워드",
                trailingIcon = {
                    Icon(
                        painter = painterResource(R.drawable.check),
                        modifier = Modifier.size(24.dp),
                        contentDescription = "check email state"
                    )
                }
            )

            Text(
                text = "10자리 이상의 영문, 숫자, 특수기호를 포함해야합니다.",
                color = colorResource(R.color.lightGray),
                fontSize = 10.sp
            )

            Spacer(modifier = Modifier.height(30.dp))
            Text(
                text = "비밀번호 재입력",
                color = colorResource(R.color.primaryBlue)
            )

            CustomUnderlineTextField(
                value = password,
                onValueChange = {
                    password = it
                },
                hint = "새 패스워드 재입력",
                trailingIcon = {
                    Icon(
                        imageVector = if (isPasswordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                        contentDescription = "Toggle password visibility",
                        modifier = Modifier
                            .clickable { isPasswordVisible = !isPasswordVisible }
                            .size(24.dp)
                    )
                }
            )

            Spacer(Modifier.height(150.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                CustomButton(
                    text = "",
                    onClick = { /* TODO: 비밀번호 수정 로직 */ },
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

@Preview(showBackground = true)
@Composable
fun FindPasswordFormnPreview() {
    FindPasswordForm()
}