package com.alyak.detector.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.alyak.detector.ui.signIn.SignInScreen
import com.alyak.detector.ui.signUp.SignUpScreen
import com.alyak.detector.ui.splash.SplashScreen
import com.alyak.detector.viewModel.SignInViewModel
import com.alyak.detector.viewModel.SignUpViewModel

@Composable
fun Navigator() {
    //NavController
    val navController = rememberNavController()

    //NavHost
    NavHost(
        navController = navController,
        startDestination = "SignInScreen"
    ) {
        composable("SignInScreen") {
            val viewModel: SignInViewModel = viewModel()
            SignInScreen(navController, viewModel)
        }

        composable("SignUpScreen") {
            val viewModel: SignUpViewModel = viewModel()
            SignUpScreen(navController, viewModel)
        }

        composable("SplashScreen"){
            SplashScreen(navController)
        }
    }
}