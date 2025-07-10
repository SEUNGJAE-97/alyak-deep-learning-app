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
import com.alyak.detector.ui.other.FindPasswordForm
import com.alyak.detector.ui.signIn.state.ContentState
import com.alyak.detector.ui.signUp.SignUpForm
import com.alyak.detector.ui.signUp.SignUpViewModel

@Composable
fun SignInScreen(
    navController: NavController,
    signInViewModel: SignInViewModel,
    signUpViewModel: SignUpViewModel
) {
    val state by signInViewModel.state.collectAsState()
    var screenState by remember { mutableStateOf<ContentState>(ContentState.Login) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.horizontalGradient(
                    colorStops = arrayOf(
                        0.0f to colorResource(id = R.color.pink),
                        1.0f to colorResource(id = R.color.primaryBlue)
                    )
                )
            ),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = when (screenState) {
                is ContentState.Login -> "어서오세요!\n로그인을 진행해주세요"
                is ContentState.SignUp -> "회원가입\n정보를 입력해주세요"
                is ContentState.FindPassword -> "비밀번호 찾기\n이메일을 입력해주세요"
            },
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

        // Fragment-like content switching
        when (screenState) {
            is ContentState.Login -> {
                SignInForm(
                    onNavigateToSignUp = { screenState = ContentState.SignUp },
                    onNavigateToFindPassword = { screenState = ContentState.FindPassword },
                    state = state,
                    navController = navController
                )
            }

            is ContentState.SignUp -> {
                SignUpForm(
                    onNavigateToLogin = { screenState = ContentState.Login },
                    navController = navController,
                    signUpViewModel = signUpViewModel
                )
            }

            is ContentState.FindPassword -> {
                FindPasswordForm(
                    //onNavigateToLogin = { screenState = ContentState.Login }
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SignInScreenPreview() {
    SignInScreen(
        navController = rememberNavController(),
        signInViewModel = SignInViewModel(),
        signUpViewModel = SignUpViewModel()
    )
}