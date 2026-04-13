package com.alyak.detector.feature.auth.ui.signIn.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.alyak.detector.R
import com.alyak.detector.feature.auth.ui.signIn.FindPasswordViewModel
import com.alyak.detector.ui.components.ContentBox
import com.alyak.detector.ui.components.CustomButton
import com.alyak.detector.ui.components.CustomUnderlineTextField

@Composable
fun FindPasswordEmailStep(
    viewModel: FindPasswordViewModel,
    onBackToSignIn: () -> Unit,
    onNextToCodeStep: () -> Unit,
) {
    val state by viewModel.state.collectAsState()

    ContentBox(modifier = Modifier.fillMaxWidth()) {
        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "이메일",
            color = colorResource(R.color.primaryBlue)
        )

        Spacer(modifier = Modifier.height(30.dp))

        CustomUnderlineTextField(
            value = state.email,
            onValueChange = {
                viewModel.onEmailInputChanged(it)
                viewModel.clearRequestCodeError()
            },
            hint = "abc@example.com",
            trailingIcon = { },
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "가입 시 사용한 이메일로 인증번호를 받은 뒤, 다음 화면에서 인증번호를 입력해주세요.",
            color = colorResource(R.color.lightGray),
            fontSize = 12.sp,
        )

        Spacer(modifier = Modifier.height(30.dp))

        state.requestCodeErrorMessage?.let { err ->
            Text(
                text = err,
                color = Color(0xFFD32F2F),
                fontSize = 13.sp,
                modifier = Modifier.fillMaxWidth(),
            )
            Spacer(modifier = Modifier.height(12.dp))
        }

        Button(
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            onClick = { viewModel.requestResetCode(state.email) },
            enabled = viewModel.isEmailFormatValid(state.email),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = colorResource(R.color.primaryBlue),
                contentColor = Color.White,
                disabledContainerColor = Color.Gray,
                disabledContentColor = Color.White.copy(alpha = 0.6f),
            )
        ) {
            Text(text = "인증번호 받기")
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier
                    .size(65.dp)
                    .clickable(onClick = onBackToSignIn),
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

            Spacer(modifier = Modifier.padding(16.dp))

            CustomButton(
                text = "",
                onClick = onNextToCodeStep,
                image = painterResource(R.drawable.arrow),
                containerColor = colorResource(R.color.primaryBlue),
                modifier = Modifier.size(80.dp),
                shape = RoundedCornerShape(50.dp),
                imageSize = 40.dp,
                enabled = viewModel.isEmailFormatValid(state.email) && state.verificationMailSent,
            )
        }
    }
}
