package com.alyak.detector.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.alyak.detector.ui.main.MainScreen
import com.alyak.detector.ui.signIn.SignInScreen
import com.alyak.detector.ui.signIn.SignInViewModel
import com.alyak.detector.ui.signUp.SignUpScreen
import com.alyak.detector.ui.signUp.SignUpViewModel
import com.alyak.detector.ui.splash.SplashScreen

@Composable
fun Navigator() {
    //NavController
    val navController = rememberNavController()

    //NavHost
    NavHost(
        navController = navController,
        startDestination = "SignInScreen"
    ) {
        composable("SplashScreen") {
            SplashScreen(navController)
        }

        composable("SignInScreen") {
            val viewModel: SignInViewModel = viewModel()
            SignInScreen(navController, viewModel)
        }

        composable("SignUpScreen") {
            val viewModel: SignUpViewModel = viewModel()
            SignUpScreen(navController, viewModel)
        }

        composable("MainScreen") {
            MainScreen(navController)
        }
    }
}