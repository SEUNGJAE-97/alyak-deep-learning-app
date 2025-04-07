package com.alyak.detector.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.alyak.detector.ui.signIn.SignInScreen
import com.alyak.detector.ui.signUp.SignUpScreen

@Composable
fun Navigator() {
    //NavController
    val navController = rememberNavController()

    //NavHost
    NavHost(
        navController = navController,
        startDestination = "SignInScreen"
    ) {
        navigationGraph(navController)
    }
}

//NavGraph
fun NavGraphBuilder.navigationGraph(navController: NavController) {
    composable("SignInScreen") {
        SignInScreen()
    }

    composable("SignUpScreen") {
        SignUpScreen()
    }
}