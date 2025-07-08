package com.alyak.detector.ui.signIn

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.draw.paint
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.alyak.detector.R
import com.alyak.detector.ui.components.CustomButton

@Composable
fun SignInScreen(
    navController: NavController,
    signInViewModel: SignInViewModel
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isPasswordVisible by remember { mutableStateOf(false) }
    val state by signInViewModel.state.collectAsState()

    //ui layout
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.horizontalGradient(
                    colorStops = arrayOf(
                        0.0f to colorResource(id = R.color.pink),       // 0% 위치에 핑크
                        1.0f to colorResource(id = R.color.primaryBlue) // 100% 위치에 블루
                    )
                )
            ),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "어서오세요!\n로그인을 진행해주세요",
            style = MaterialTheme.typography.headlineMedium,
            color = Color.White,
            fontWeight = FontWeight.Bold,
            lineHeight = 80.sp,
            modifier = Modifier
                .padding(
                    start = 0.dp,
                    top = 50.dp,
                    end = 80.dp
                )
        )

        Spacer(modifier = Modifier.height(150.dp))

        ContentBox(
            modifier = Modifier
                .fillMaxWidth()
        ) {

            //Email input
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            //password input
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password") },
                visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions.Default,
                trailingIcon = {
                    Icon(
                        imageVector = if (isPasswordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                        contentDescription = "Toggle password visibility",
                        modifier = Modifier.clickable { isPasswordVisible = !isPasswordVisible }
                    )
                },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            // login Button
            CustomButton(
                text = "",
                onClick = { signInViewModel.signIn(email, password) },
                image = painterResource(R.drawable.arrow),
                containerColor = colorResource(R.color.primaryBlue),
                modifier = Modifier
                    .size(80.dp)
                    .align(Alignment.End)
                    .clickable {
                        signInViewModel.signIn(email, password)
                    },

                shape = RoundedCornerShape(50.dp)
            )
            if (state.loginSuccess) {
                LaunchedEffect(Unit) {
                    navController.navigate("MainScreen") {
                        popUpTo("SignInScreen") { inclusive = true }
                    }
                }
            }
            if (state.loginError) {
                Toast.makeText(LocalContext.current, "Login Error", Toast.LENGTH_LONG).show()
            }
            Text(
                text = "회원가입",
                modifier = Modifier.clickable {
                    navController.navigate("SignUpScreen")
                }
            )
            Spacer(modifier = Modifier.height(20.dp))
            Text(
                text = "비밀번호 찾기",
                modifier = Modifier.clickable {
                    navController.navigate("SignUpScreen")
                }
            )
            Spacer(modifier = Modifier.height(20.dp))
            CustomButton(
                text = "",
                onClick = {},
                modifier = Modifier
                    .shadow(
                        elevation = 0.01.dp,
                        RoundedCornerShape(24.dp)
                    )
                    .fillMaxWidth(),
                containerColor = colorResource(R.color.kakaoYellow),
                image = painterResource(R.drawable.kakao),
                shape = RoundedCornerShape(8.dp)
            )
            CustomButton(
                text = "",
                onClick = {},
                modifier = Modifier
                    .shadow(
                        elevation = 0.1.dp,
                        RoundedCornerShape(24.dp)
                    )
                    .fillMaxWidth(),
                containerColor = colorResource(R.color.googleGray),
                image = painterResource(R.drawable.google),
                shape = RoundedCornerShape(8.dp)
            )
        }
    }
}

@Composable
fun ContentBox(
    modifier: Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(
                color = Color.White,
                shape = RoundedCornerShape(
                    topStart = 24.dp,
                    topEnd = 24.dp,
                    bottomEnd = 0.dp,
                    bottomStart = 0.dp
                )
            )
            .padding(24.dp)
            .fillMaxHeight()
    ) {
        content()
    }
}

@Preview(showBackground = true)
@Composable
fun SignInScreenPreview() {
    SignInScreen(navController = rememberNavController(), SignInViewModel())
}