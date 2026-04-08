package com.alyak.detector.feature.auth.ui.signUp

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.alyak.detector.R
import com.alyak.detector.feature.auth.ui.signUp.components.SignUpCodeStep
import com.alyak.detector.feature.auth.ui.signUp.components.SignUpEmailStep
import com.alyak.detector.feature.auth.ui.signUp.components.SignUpProfileStep

private const val RouteSignUpEmail = "sign_up_email"
private const val RouteSignUpCode = "sign_up_code"
private const val RouteSignUpProfile = "sign_up_profile"

@Composable
fun SignUpScreen(
    navController: NavController,
    signUpViewModel: SignUpViewModel = hiltViewModel(),
) {
    val innerNav = rememberNavController()
    val navBackStackEntry by innerNav.currentBackStackEntryAsState()
    val headline = buildAnnotatedString {
        when (navBackStackEntry?.destination?.route) {
            RouteSignUpProfile -> append("비밀번호 · 이름을\n입력해주세요")
            RouteSignUpCode -> {
                withStyle(style = SpanStyle(fontSize = 32.sp, fontWeight = FontWeight.Bold)) {
                    append("이메일 본인인증\n")
                }
                withStyle(style = SpanStyle(fontSize = 14.sp, fontWeight = FontWeight.Thin)) {
                    append("입력하신 이메일로 발송된 인증코드를 입력해주세요")
                }
            }

            else -> {
                withStyle(style = SpanStyle(fontSize = 32.sp, fontWeight = FontWeight.Bold)) {
                    append("이메일 인증\n")
                }
                withStyle(style = SpanStyle(fontSize = 14.sp, fontWeight = FontWeight.Thin)) {
                    append("원활한 서비스 이용을 위해 이메일 인증을 해주세요")
                }
            }
        }
    }

    BackHandler {
        if (!innerNav.popBackStack()) {
            navController.popBackStack()
        }
    }

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
            )
            .padding(top = 24.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = headline,
            style = MaterialTheme.typography.headlineMedium,
            color = Color.White,
            lineHeight = 40.sp,
            modifier = Modifier
                .padding(start = 30.dp, top = 50.dp, end = 20.dp)
                .align(Alignment.Start)
        )

        Spacer(modifier = Modifier.height(32.dp))

        NavHost(
            navController = innerNav,
            startDestination = RouteSignUpEmail,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            composable(RouteSignUpEmail) {
                SignUpEmailStep(
                    viewModel = signUpViewModel,
                    onNavigateToLogin = { navController.popBackStack() },
                    onNextToCodeStep = {
                        innerNav.navigate(RouteSignUpCode) {
                            launchSingleTop = true
                        }
                    }
                )
            }
            composable(RouteSignUpCode) {
                SignUpCodeStep(
                    viewModel = signUpViewModel,
                    onBackToEmailStep = { innerNav.popBackStack() },
                    onVerifiedNext = {
                        innerNav.navigate(RouteSignUpProfile) {
                            launchSingleTop = true
                        }
                    }
                )
            }
            composable(RouteSignUpProfile) {
                SignUpProfileStep(
                    viewModel = signUpViewModel,
                    navController = navController,
                    onBackToEmailStep = { innerNav.popBackStack() }
                )
            }
        }
    }
}
