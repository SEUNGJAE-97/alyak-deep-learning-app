package com.alyak.detector.feature.auth.ui.signIn

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
import com.alyak.detector.feature.auth.ui.signIn.components.FindPasswordCodeStep
import com.alyak.detector.feature.auth.ui.signIn.components.FindPasswordEmailStep
import com.alyak.detector.feature.auth.ui.signIn.components.FindPasswordNewPasswordStep

private const val RouteFindPwEmail = "find_pw_email"
private const val RouteFindPwCode = "find_pw_code"
private const val RouteFindPwNew = "find_pw_new"

@Composable
fun FindPasswordScreen(navController: NavController) {
    val viewModel: FindPasswordViewModel = hiltViewModel()
    val innerNav = rememberNavController()
    val navBackStackEntry by innerNav.currentBackStackEntryAsState()
    val headline = buildAnnotatedString {
        when (navBackStackEntry?.destination?.route) {
            RouteFindPwNew -> append("새 비밀번호를\n입력해주세요")
            RouteFindPwCode -> {
                withStyle(style = SpanStyle(fontSize = 32.sp, fontWeight = FontWeight.Bold)) {
                    append("이메일 본인인증\n")
                }
                withStyle(style = SpanStyle(fontSize = 14.sp, fontWeight = FontWeight.Thin)) {
                    append("발송된 인증코드를 입력해주세요")
                }
            }

            else -> {
                withStyle(style = SpanStyle(fontSize = 32.sp, fontWeight = FontWeight.Bold)) {
                    append("비밀번호 찾기\n")
                }
                withStyle(style = SpanStyle(fontSize = 14.sp, fontWeight = FontWeight.Thin)) {
                    append("가입 시 사용한 이메일을 입력해주세요")
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
            startDestination = RouteFindPwEmail,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            composable(RouteFindPwEmail) {
                FindPasswordEmailStep(
                    viewModel = viewModel,
                    onBackToSignIn = { navController.popBackStack() },
                    onNextToCodeStep = {
                        innerNav.navigate(RouteFindPwCode) {
                            launchSingleTop = true
                        }
                    }
                )
            }
            composable(RouteFindPwCode) {
                FindPasswordCodeStep(
                    viewModel = viewModel,
                    onBackToEmailStep = { innerNav.popBackStack() },
                    onVerifiedNext = {
                        innerNav.navigate(RouteFindPwNew) {
                            launchSingleTop = true
                        }
                    }
                )
            }
            composable(RouteFindPwNew) {
                FindPasswordNewPasswordStep(
                    viewModel = viewModel,
                    onBackToCodeStep = { innerNav.popBackStack() },
                    onResetSuccess = { navController.popBackStack() },
                )
            }
        }
    }
}
