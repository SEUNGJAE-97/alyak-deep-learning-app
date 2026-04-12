package com.alyak.detector.navigation

import com.alyak.detector.feature.notification.ui.MedicineStatisticsScreen
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.alyak.detector.core.auth.TokenManager
import com.alyak.detector.core.util.PermissionManager
import com.alyak.detector.feature.auth.ui.signIn.FindPasswordScreen
import com.alyak.detector.feature.auth.ui.signIn.SignInScreen
import com.alyak.detector.feature.auth.ui.signIn.SignInViewModel
import com.alyak.detector.feature.auth.ui.signUp.SignUpScreen
import com.alyak.detector.feature.auth.ui.signUp.SignUpViewModel
import com.alyak.detector.feature.camera.ui.CAMERA_MODE_PILL
import com.alyak.detector.feature.camera.ui.CAMERA_MODE_QR
import com.alyak.detector.feature.camera.ui.CameraScreen
import com.alyak.detector.feature.camera.ui.CameraViewModel
import com.alyak.detector.feature.camera.ui.ResultScreen
import com.alyak.detector.feature.family.ui.invitation.FamilyInvitationViewModel
import com.alyak.detector.feature.family.ui.main.MainScreen
import com.alyak.detector.feature.map.ui.MapScreen
import com.alyak.detector.feature.notification.ui.MedicineStatisticsViewModel
import com.alyak.detector.feature.pill.ui.PillDetail.PillDetailScreen
import com.alyak.detector.feature.pill.ui.search.PillSearchScreen
import com.alyak.detector.feature.pill.ui.search.PillSearchViewModel
import com.alyak.detector.feature.splash.ui.SplashScreen
import com.alyak.detector.feature.user.ui.UserScreen

@Composable
fun Navigator(permissionManager: PermissionManager, tokenManager: TokenManager) {
    LaunchedEffect(Unit) {
        permissionManager.setOnGrantedListener {
            // 권한 허용 후 실행할 작업
        }
        permissionManager.requestPermissions()
    }

    val accessToken by tokenManager.accessTokenFlow.collectAsState(initial = null)
    val navController = rememberNavController()

    //NavHost
    NavHost(
        navController = navController,
        startDestination = if (accessToken == null) "SignInScreen" else "MainScreen"
    ) {
        composable("SplashScreen") {
            SplashScreen(navController)
        }

        composable("SignInScreen") {
            val signInViewModel: SignInViewModel = hiltViewModel()
            SignInScreen(
                navController = navController,
                signInViewModel = signInViewModel
            )
        }

        composable("SignUpScreen") {
            val signUpViewModel: SignUpViewModel = hiltViewModel()
            SignUpScreen(
                navController = navController,
                signUpViewModel = signUpViewModel
            )
        }

        composable("FindPasswordScreen") {
            FindPasswordScreen(navController = navController)
        }

        composable("MainScreen") {
            MainScreen(navController)
        }
        composable("CameraScreen") {
            val cameraViewModel: CameraViewModel = hiltViewModel()
            CameraScreen(navController, cameraViewModel, mode = CAMERA_MODE_PILL)
        }
        composable("CameraScreenQr") {
            val cameraViewModel: CameraViewModel = hiltViewModel()
            val mainEntry = runCatching {
                navController.getBackStackEntry("MainScreen")
            }.getOrNull()
            val invitationViewModel: FamilyInvitationViewModel? =
                mainEntry?.let { hiltViewModel(it) }

            CameraScreen(
                navController = navController,
                viewModel = cameraViewModel,
                mode = CAMERA_MODE_QR,
                onQrScanned = { token -> invitationViewModel?.onQrScanned(token) }
            )
        }
        composable("ResultScreen") {
            val cameraEntry = runCatching {
                navController.getBackStackEntry("CameraScreen")
            }.getOrNull()
            if (cameraEntry != null) {
                val cameraViewModel: CameraViewModel = hiltViewModel(cameraEntry)
                ResultScreen(navController, cameraViewModel)
            }
        }
        composable("MapScreen") {
            MapScreen(navController)
        }
        composable("PillSearchScreen") {
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
        composable("UserScreen") {
            UserScreen(navController)
        }
        composable("medicine_statistics") {
            val pillSearchViewModel: PillSearchViewModel = hiltViewModel()
            val medicineStatisticsViewModel: MedicineStatisticsViewModel = hiltViewModel()
            MedicineStatisticsScreen(
                navController,
                viewModel = medicineStatisticsViewModel,
                pillSearchViewModel = pillSearchViewModel
            )
        }
    }
}