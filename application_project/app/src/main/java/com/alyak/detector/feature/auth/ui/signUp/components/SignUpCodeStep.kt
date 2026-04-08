package com.alyak.detector.feature.auth.ui.signUp.components

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
import androidx.compose.material3.Icon
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
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.alyak.detector.R
import com.alyak.detector.feature.auth.ui.signUp.SignUpViewModel
import com.alyak.detector.ui.components.ContentBox
import com.alyak.detector.ui.components.CustomButton
import com.alyak.detector.ui.components.CustomUnderlineTextField

@Composable
fun SignUpCodeStep(
    viewModel: SignUpViewModel,
    onBackToEmailStep: () -> Unit,
    onVerifiedNext: () -> Unit,
) {
    val state by viewModel.state.collectAsState()
    var verificationCode by remember { mutableStateOf("") }
    val email = state.email
    val isDisabled = state.emailVerified

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

        CustomUnderlineTextField(
            value = verificationCode,
            onValueChange = { verificationCode = it },
            hint = "이메일로 전송된 인증 번호를 입력해주세요.",
            trailingIcon = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.clickable(enabled = email.isNotBlank()) {
                        viewModel.verifyCode(email, verificationCode)
                    }
                ) {
                    Icon(
                        painter = painterResource(R.drawable.arrow),
                        modifier = Modifier.size(24.dp),
                        contentDescription = "인증 확인"
                    )
                }
            },
            enabled = !isDisabled
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
