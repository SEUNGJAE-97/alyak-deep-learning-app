package com.alyak.detector.ui.signIn

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.alyak.detector.R
import com.alyak.detector.ui.signIn.state.ContentState
import com.alyak.detector.ui.signUp.SignUpForm

@Composable
fun SignInScreen(
    navController: NavController,
    signInViewModel: SignInViewModel
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isPasswordVisible by remember { mutableStateOf(false) }
    val state by signInViewModel.state.collectAsState()
    var screenState by remember { mutableStateOf<ContentState>(ContentState.Login) }

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

        Spacer(modifier = Modifier.height(70.dp))

        when (screenState) {
            is ContentState.Login -> {
                SignInForm(
                    email = email,
                    password = password,
                    isPasswordVisible = isPasswordVisible,
                    onEmailChange = { email = it },
                    onPasswordChange = { password = it },
                    onTogglePassword = { isPasswordVisible = !isPasswordVisible },
                    onSignIn = { signInViewModel.signIn(email, password) },
                    onNavigateToSignUp = { screenState = ContentState.Login },
                    onNavigateToFindPassword = { screenState = ContentState.FindPassword },
                    state = state,
                    navController = navController
                )
            }

            is ContentState.SignUp -> {
                SignUpForm(
                    name = TODO(),
                    email = TODO(),
                    password = TODO(),
                    gender = TODO(),
                    residentNumber = TODO(),
                    phoneNumber = TODO(),
                    isPasswordVisible = TODO(),
                    onEmailChange = TODO(),
                    onTogglePassword = TODO(),
                    onNavigateToSignUp = TODO(),
                    state = state,
                    navController = navController,
                    signUpViewModel = TODO()
                )

            }

            is ContentState.FindPassword -> {

            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SignInScreenPreview() {
    SignInScreen(navController = rememberNavController(), SignInViewModel())
}