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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.alyak.detector.R
import com.alyak.detector.feature.auth.ui.signIn.FindPasswordViewModel
import com.alyak.detector.ui.components.ContentBox
import com.alyak.detector.ui.components.CustomButton

@Composable
fun FindPasswordCodeStep(
    viewModel: FindPasswordViewModel,
    onBackToEmailStep: () -> Unit,
    onVerifiedNext: () -> Unit,
) {
    val state by viewModel.state.collectAsState()
    var verificationCode by remember { mutableStateOf("") }
    val verifyError = state.verifyCodeErrorMessage
    val email = state.email
    val isDisabled = state.emailVerified
    val timeLeft by viewModel.timeLeft.collectAsState()
    val timerText = "%02d:%02d".format(timeLeft / 60, timeLeft % 60)

    ContentBox(modifier = Modifier.fillMaxWidth()) {
        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "인증 메일 발송 주소",
            color = colorResource(R.color.primaryBlue)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = email.ifEmpty { "(이메일을 입력해주세요)" },
            color = colorResource(R.color.notification_title_text),
            fontSize = 15.sp,
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "이메일 인증 번호",
            color = colorResource(R.color.primaryBlue)
        )
        Spacer(modifier = Modifier.height(30.dp))

        OutlinedTextField(
            value = verificationCode,
            onValueChange = {
                verificationCode = it
                viewModel.clearVerifyCodeError()
            },
            placeholder = {
                Text(
                    text = "인증번호 6자리",
                    color = Color.Gray,
                    fontSize = 14.sp
                )
            },
            trailingIcon = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.padding(end = 8.dp)
                ) {
                    Text(
                        text = timerText,
                        fontSize = 14.sp,
                        color = if (timeLeft <= 60) Color.Red else Color.Gray
                    )
                }
            },
            enabled = !isDisabled && timeLeft > 0,
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            shape = RoundedCornerShape(8.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = colorResource(R.color.primaryBlue),
                unfocusedBorderColor = colorResource(R.color.black),
                disabledBorderColor = Color(0xFFDEE2E6),
            ),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(30.dp))

        if (verifyError != null) {
            Text(
                text = verifyError,
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
            onClick = { viewModel.verifyCode(email, verificationCode) },
            enabled = email.isNotBlank() && timeLeft > 0,
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = colorResource(R.color.primaryBlue),
                contentColor = Color.White,
                disabledContainerColor = Color.Gray,
                disabledContentColor = Color.White.copy(alpha = 0.6f),
            )
        ) {
            Text(text = "인증 확인")
        }

        Spacer(modifier = Modifier.height(16.dp))
        Row(
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "인증문자를 받지 못하셨나요?",
                color = colorResource(R.color.lightGray),
                fontSize = 15.sp,
            )
            Spacer(modifier = Modifier.size(10.dp))

            Text(
                text = "다시 보내기",
                color = colorResource(R.color.notification_title_text),
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                style = TextStyle(textDecoration = TextDecoration.Underline),
                modifier = Modifier.clickable {
                    viewModel.requestResetCode(email)
                }
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier
                    .size(65.dp)
                    .clickable(onClick = onBackToEmailStep),
                shape = RoundedCornerShape(50.dp),
                color = colorResource(R.color.white),
                shadowElevation = 2.dp
            ) {
                Image(
                    painter = painterResource(R.drawable.arrow_back),
                    contentDescription = "이전 단계",
                    modifier = Modifier
                        .padding(12.dp)
                        .size(30.dp)
                )
            }

            Spacer(modifier = Modifier.padding(16.dp))

            CustomButton(
                text = "",
                onClick = onVerifiedNext,
                image = painterResource(R.drawable.arrow),
                containerColor = colorResource(R.color.primaryBlue),
                modifier = Modifier.size(80.dp),
                shape = RoundedCornerShape(50.dp),
                imageSize = 40.dp,
                enabled = state.emailVerified,
            )
        }
    }
}
