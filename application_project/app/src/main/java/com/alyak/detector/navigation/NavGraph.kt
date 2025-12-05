package com.alyak.detector.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.alyak.detector.core.util.PermissionManager
import com.alyak.detector.feature.auth.ui.signIn.SignInScreen
import com.alyak.detector.feature.auth.ui.signIn.SignInViewModel
import com.alyak.detector.feature.auth.ui.signUp.SignUpViewModel
import com.alyak.detector.feature.camera.ui.CameraScreen
import com.alyak.detector.feature.family.ui.main.MainScreen
import com.alyak.detector.feature.map.ui.MapScreen
import com.alyak.detector.feature.pill.ui.PillDetail.PillDetailScreen
import com.alyak.detector.feature.pill.ui.search.PillSearchScreen
import com.alyak.detector.feature.splash.ui.SplashScreen

@Composable
fun Navigator(permissionManager : PermissionManager) {
    LaunchedEffect(Unit) {
        permissionManager.setOnGrantedListener {
            // 권한 허용 후 실행할 작업
        }
        permissionManager.requestPermissions()
    }

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
            MapScreen(navController)
        }
        composable("PillSearchScreen"){
            PillSearchScreen(navController)
        }
        composable(
            route = "PillDetailScreen/{pillId}",
            arguments = listOf(
                navArgument("pillId") {
                    type = NavType.LongType
                    defaultValue = 0L
                }
            )
        ) { backStackEntry ->
            PillDetailScreen(
                pillId = backStackEntry.arguments?.getLong("pillId") ?: 0L,
                navController = navController
            )
        }
    }
}