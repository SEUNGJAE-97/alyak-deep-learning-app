package com.alyak.detector.feature.auth.ui.signIn.components

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.alyak.detector.R
import com.alyak.detector.ui.components.ContentBox
import com.alyak.detector.ui.components.CustomButton
import com.alyak.detector.ui.components.CustomUnderlineTextField
import com.alyak.detector.feature.auth.ui.signIn.SignInViewModel
import com.alyak.detector.feature.auth.ui.signIn.state.SignInState

@Composable
fun SignInForm(
    onNavigateToSignUp: () -> Unit,
    onNavigateToFindPassword: () -> Unit,
    state: SignInState,
    navController: NavController,
    viewModel: SignInViewModel
) {
    val context = LocalContext.current

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isPasswordVisible by remember { mutableStateOf(false) }

    ContentBox(modifier = Modifier.fillMaxWidth()) {
        Spacer(modifier = Modifier.height(30.dp))

        Text(
            text = "이메일",
            color = colorResource(R.color.primaryBlue)
        )

        Spacer(modifier = Modifier.height(30.dp))

        CustomUnderlineTextField(
            value = email,
            onValueChange = { email = it },
            hint = "이메일",
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
            text = "비밀번호",
            color = colorResource(R.color.primaryBlue)
        )

        Spacer(modifier = Modifier.height(30.dp))

        CustomUnderlineTextField(
            value = password,
            onValueChange = { password = it },
            hint = "password",
            trailingIcon = {
                Icon(
                    imageVector = if (isPasswordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                    contentDescription = "Toggle password visibility",
                    modifier = Modifier
                        .clickable { isPasswordVisible = !isPasswordVisible }
                        .size(24.dp)
                )
            },
            visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "회원가입",
                    modifier = Modifier.clickable { onNavigateToSignUp() }
                )
                Spacer(modifier = Modifier.height(20.dp))
                Text(
                    text = "비밀번호 찾기",
                    modifier = Modifier.clickable { onNavigateToFindPassword() }
                )
            }

            CustomButton(
                text = "",
                onClick = {
                    /* TODO: 로그인 로직 */
                    viewModel.signIn(email, password)
                },
                image = painterResource(R.drawable.arrow),
                containerColor = colorResource(R.color.primaryBlue),
                modifier = Modifier.size(80.dp),
                shape = RoundedCornerShape(50.dp),
                imageSize = 120.dp
            )
        }

        if (state.loginSuccess) {
            LaunchedEffect(Unit) {
                navController.navigate("MainScreen") {
                    popUpTo("SignInScreen") { inclusive = true }
                }
            }
        }

        if (state.loginError) {
            Toast.makeText(context, "Login Error", Toast.LENGTH_LONG).show()
        }

        Spacer(modifier = Modifier.height(20.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            HorizontalDivider(modifier = Modifier.weight(1f), thickness = 1.dp)
            Text("간편 로그인", Modifier.padding(5.dp))
            HorizontalDivider(modifier = Modifier.weight(1f), thickness = 1.dp)
        }

        Spacer(modifier = Modifier.height(20.dp))

        Column(modifier = Modifier.fillMaxWidth()) {
            CustomButton(
                text = "카카오 로그인",
                textColor = colorResource(R.color.black),
                onClick = {
                    //TODO : 카카오 로그인

                },
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(5f),
                containerColor = colorResource(R.color.kakaoYellow),
                image = painterResource(R.drawable.kakao),
                shape = RoundedCornerShape(8.dp)
            )
            CustomButton(
                text = "구글 로그인",
                textColor = colorResource(R.color.black),
                onClick = {},
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(5f),
                containerColor = colorResource(R.color.googleGray),
                image = painterResource(R.drawable.google),
                shape = RoundedCornerShape(8.dp)
            )
        }
    }
}