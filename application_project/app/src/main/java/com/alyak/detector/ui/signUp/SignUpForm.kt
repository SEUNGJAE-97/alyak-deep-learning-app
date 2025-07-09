package com.alyak.detector.ui.signUp

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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.alyak.detector.R
import com.alyak.detector.data.dto.Gender
import com.alyak.detector.ui.components.ContentBox
import com.alyak.detector.ui.components.CustomButton
import com.alyak.detector.ui.components.CustomUnderlineTextField
import com.alyak.detector.ui.signIn.state.SignInState

@Composable
fun SignUpForm(
    name: String,
    email: String,
    password: String,
    gender: Gender,
    residentNumber: String,
    phoneNumber: String,
    isPasswordVisible: Boolean,
    onEmailChange: (String) -> Unit,
    onTogglePassword: () -> Unit,
    onNavigateToSignUp: () -> Unit,
    state: SignInState,
    navController: NavController,
    signUpViewModel: SignUpViewModel
) {
    val state by signUpViewModel.state.collectAsState()
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var checkPassword by remember { mutableStateOf("") }
    var userName by remember { mutableStateOf(" ") }
    var userPhoneNumber by remember { mutableStateOf("") }
    var userSSN by remember { mutableStateOf("") }

    ContentBox(modifier = Modifier.fillMaxWidth()) {
        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "비밀번호",
            color = colorResource(R.color.primaryBlue)
        )

        Spacer(modifier = Modifier.height(30.dp))

        CustomUnderlineTextField(
            value = email,
            onValueChange = {
                email = it
                signUpViewModel.validateEmail(it)
            },
            hint = "이메일",
            trailingIcon = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = if (isPasswordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                        contentDescription = "Toggle password visibility",
                        modifier = Modifier
                            .clickable { onTogglePassword() }
                            .size(24.dp)
                    )
                    Icon(
                        painter = painterResource(R.drawable.check),
                        modifier = Modifier.size(24.dp),
                        contentDescription = "check email state"
                    )
                }
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
            value = password,
            onValueChange = {
                password = it
                signUpViewModel.validatePassword(it)
            },
            hint = "이메일",
            trailingIcon = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = if (isPasswordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                        contentDescription = "Toggle password visibility",
                        modifier = Modifier
                            .clickable { onTogglePassword() }
                            .size(24.dp)
                    )
                    Icon(
                        painter = painterResource(R.drawable.cancle),
                        modifier = Modifier.size(24.dp),
                        contentDescription = "check email state"
                    )
                }
            }
        )
        // 비밀번호 규칙 체크
        if (password.isNotEmpty() && !state.validPassword) {
//            Toast.makeText(
//                Context,
//                "영문, 숫자, 특수문자 중 2가지 이상을 조합해 최소 8자리를 입력해주세요",
//            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "이름",
            color = colorResource(R.color.primaryBlue)
        )

        Spacer(modifier = Modifier.height(20.dp))

        CustomUnderlineTextField(
            value = name,
            onValueChange = onEmailChange,
        )

        Spacer(modifier = Modifier.height(30.dp))

        Text(
            text = "주민번호",
            color = colorResource(R.color.primaryBlue)
        )

        Spacer(modifier = Modifier.height(20.dp))

        CustomUnderlineTextField(
            value = residentNumber,
            onValueChange = onEmailChange,
            modifier = Modifier
                .fillMaxWidth(0.30f),
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(30.dp))

        Text(
            text = "전화번호",
            color = colorResource(R.color.primaryBlue)
        )

        Spacer(modifier = Modifier.height(20.dp))

        CustomUnderlineTextField(
            value = userPhoneNumber,
            onValueChange = {
                userPhoneNumber = it
                signUpViewModel.validatePhoneNumber(it)
            },
            hint = "전화번호",
            modifier = Modifier.fillMaxWidth(0.45f),
            textAlign = TextAlign.Center
        )
        //전화번호가 비어있거나 유효하지 않을때
        if (userPhoneNumber.isNotEmpty() && !state.validPhoneNumber) {
//            Text("유효하지 않은 전화번호 입니다.", color = Color.Red, fontSize = 10.sp)
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier
                    .size(65.dp)
                    .clickable { /* onClick */ },
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
            CustomButton(
                text = "",
                onClick = { },
                image = painterResource(R.drawable.arrow),
                containerColor = colorResource(R.color.primaryBlue),
                modifier = Modifier.size(80.dp),
                shape = RoundedCornerShape(50.dp),
                imageSize = 40.dp,

                )
        }
    }
}


@Preview(showBackground = true)
@Composable
fun SignUpFormPreview() {
    SignUpForm(
        name = "김싸피",
        email = "test@example.com",
        password = "**********************",
        gender = Gender.male,
        residentNumber = "970324",
        phoneNumber = "010-1234-5678",
        isPasswordVisible = false,
        onEmailChange = {},
        onTogglePassword = {},
        onNavigateToSignUp = {},
        state = SignInState(),
        navController = TODO(),
        signUpViewModel = TODO(),
    )
}