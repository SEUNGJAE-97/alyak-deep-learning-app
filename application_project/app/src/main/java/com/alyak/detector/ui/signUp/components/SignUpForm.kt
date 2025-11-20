package com.alyak.detector.ui.signUp.components

import android.widget.Toast
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.alyak.detector.R
import com.alyak.detector.ui.components.ContentBox
import com.alyak.detector.ui.components.CustomButton
import com.alyak.detector.ui.components.CustomUnderlineTextField
import com.alyak.detector.ui.signUp.SignUpViewModel

@Composable
fun SignUpForm(
    onNavigateToLogin: () -> Unit,
    navController: NavController,
    viewModel: SignUpViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val context = LocalContext.current
    val signUpResult by viewModel.signUpResult.collectAsState()

    // 자체 상태 관리
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var checkPassword by remember { mutableStateOf("") }
    var userName by remember { mutableStateOf("") }
    var isPasswordVisible by remember { mutableStateOf(false) }
    var verificationCode by remember { mutableStateOf("") }

    LaunchedEffect(signUpResult) {
        signUpResult?.onSuccess {
            Toast.makeText(context, "회원가입 성공!", Toast.LENGTH_SHORT).show()
            navController.navigate("SignInScreen")
        }
        signUpResult?.onFailure {
            Toast.makeText(context, it.message ?: "알 수 없는 오류 발생", Toast.LENGTH_SHORT).show()
        }
    }
    ContentBox(modifier = Modifier.fillMaxWidth()) {
        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "이메일",
            color = colorResource(R.color.primaryBlue)
        )

        Spacer(modifier = Modifier.height(30.dp))

        CustomUnderlineTextField(
            value = email,
            onValueChange = {
                email = it
                viewModel.validateEmail(it)
            },
            hint = "이메일",
            trailingIcon = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.clickable {
                        viewModel.requestCode(email)
                    }
                ) {
                    Icon(
                        painter = painterResource(R.drawable.check),
                        modifier = Modifier.size(24.dp),
                        contentDescription = "check email state"
                    )
                }
            }
        )
        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "이메일 인증 번호",
            color = colorResource(R.color.primaryBlue)
        )
        Spacer(modifier = Modifier.height(30.dp))
        CustomUnderlineTextField(
            value = verificationCode,
            onValueChange = {
                verificationCode = it
                viewModel.requestCode(it)
            },
            hint = "이메일로 전송된 인증 번호를 입력해주세요.",
            trailingIcon = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.clickable {
                        viewModel.verifyCode(email, verificationCode)
                    }
                ) {
                    Icon(
                        painter = painterResource(R.drawable.arrow),
                        modifier = Modifier.size(24.dp),
                        contentDescription = "check email state",
                    )
                }
            }
        )

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "비밀번호",
            color = colorResource(R.color.primaryBlue)
        )

        Spacer(modifier = Modifier.height(20.dp))

        CustomUnderlineTextField(
            value = password,
            onValueChange = {
                password = it
                viewModel.validatePassword(it)
            },
            hint = "비밀번호",
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

        Text(
            text = "10자리 이상의 영문, 숫자, 특수기호를 포함해야합니다.",
            color = colorResource(R.color.lightGray),
            fontSize = 10.sp
        )

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "비밀번호 재입력",
            color = colorResource(R.color.primaryBlue)
        )

        Spacer(modifier = Modifier.height(20.dp))

        CustomUnderlineTextField(
            value = checkPassword,
            onValueChange = { checkPassword = it },
            hint = "비밀번호 재입력",
            trailingIcon = {
                Icon(
                    painter = painterResource(R.drawable.cancle),
                    modifier = Modifier.size(24.dp),
                    contentDescription = "check password state"
                )
            }
        )

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "이름",
            color = colorResource(R.color.primaryBlue)
        )

        Spacer(modifier = Modifier.height(20.dp))

        CustomUnderlineTextField(
            value = userName,
            onValueChange = { userName = it },
            hint = "이름"
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
                    .clickable { onNavigateToLogin() },
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
                onClick = {
                    viewModel.signUpUser(email, password, userName, context)
                },
                image = painterResource(R.drawable.arrow),
                containerColor = colorResource(R.color.primaryBlue),
                modifier = Modifier.size(80.dp),
                shape = RoundedCornerShape(50.dp),
                imageSize = 40.dp
            )
        }
    }
}