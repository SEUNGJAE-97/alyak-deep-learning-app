package com.alyak.detector.feature.auth.ui.signIn

import android.net.Uri
import androidx.browser.customtabs.CustomTabsIntent
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.alyak.detector.R
import com.alyak.detector.feature.auth.ui.signIn.components.SignInForm

@Composable
fun SignInScreen(
    navController: NavController,
    signInViewModel: SignInViewModel
) {
    val state by signInViewModel.state.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        signInViewModel.loginEvent.collect { url ->
            val customTabsIntent = CustomTabsIntent.Builder().build()
            customTabsIntent.launchUrl(context, Uri.parse(url))
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
                    start = 30.dp,
                    top = 50.dp,
                    end = 80.dp
                )
                .align(Alignment.Start)
        )

        Spacer(modifier = Modifier.height(70.dp))
        SignInForm(
            onNavigateToSignUp = { navController.navigate("SignUpScreen") },
            onNavigateToFindPassword = { navController.navigate("FindPasswordScreen") },
            state = state,
            navController = navController,
            viewModel = signInViewModel,
            onKakaoLoginClick = { signInViewModel.startKakaoLogin() },
            onGoogleLoginClick = { signInViewModel.startGoogleLogin() }
        )
    }
}