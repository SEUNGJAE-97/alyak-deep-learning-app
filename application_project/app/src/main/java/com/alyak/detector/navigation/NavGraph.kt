package com.alyak.detector.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHost
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.alyak.detector.ui.signIn.SignInScreen
import com.alyak.detector.ui.signUp.SignUpScreen
import com.alyak.detector.viewModel.SignInState
import com.alyak.detector.viewModel.SignInViewModel

@Composable
fun Navigator() {
    //NavController
    val navController = rememberNavController()
    val signInState : SignInViewModel = viewModel()
    //NavHost
            NavHost(
        navController = navController,
        startDestination = "SignInScreen"
    ) {
        navigationGraph(navController, signInState)
    }
}

//NavGraph
fun NavGraphBuilder.navigationGraph(
    navController: NavController,
    signInState : SignInViewModel
) {
    composable("SignInScreen") {
        SignInScreen(navController, signInState)
    }

    composable("SignUpScreen") {
        SignUpScreen()
    }
}