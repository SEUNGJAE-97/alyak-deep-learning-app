package com.alyak.detector.navigation

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.alyak.detector.feature.auth.ui.signIn.SignInScreen
import com.alyak.detector.feature.auth.ui.signIn.SignInViewModel
import com.alyak.detector.feature.auth.ui.signUp.SignUpViewModel
import com.alyak.detector.feature.camera.ui.CameraScreen
import com.alyak.detector.feature.family.ui.main.MainScreen
import com.alyak.detector.feature.map.ui.KakaoMapView
import com.alyak.detector.feature.splash.ui.SplashScreen

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
            val signInViewModel: SignInViewModel = hiltViewModel()
            val signUpViewModel: SignUpViewModel = hiltViewModel()
            SignInScreen(navController, signInViewModel, signUpViewModel)
        }

        composable("MainScreen") {
            MainScreen(navController)
        }
        composable("CameraScreen") {
            CameraScreen(navController)
        }
        composable("MapScreen") {
            KakaoMapView(navController)
        }
    }
}