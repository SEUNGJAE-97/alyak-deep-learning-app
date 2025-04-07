package com.alyak.detector.ui.signIn

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.alyak.detector.ui.components.CustomButton
import com.alyak.detector.viewModel.SignInViewModel

@Composable
fun SignInScreen(
    navController: NavController,
    signInViewModel: SignInViewModel
) {
    var email by remember { mutableStateOf(" ") }
    var password by remember { mutableStateOf(" ") }
    var isPasswordVisible by remember { mutableStateOf(false) }
    val state by signInViewModel.state.collectAsState()

    //ui layout
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "login",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(16.dp))

        //Email input
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        //password input
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions.Default,
            trailingIcon = {
                Icon(
                    imageVector = if (isPasswordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                    contentDescription = "Toggle password visibility",
                    modifier = Modifier.clickable { isPasswordVisible = !isPasswordVisible }
                )
            },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        // login Button
        CustomButton(
            text = "sign in",
            onClick = { Log.d("sign in", "$email , $password") },
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    signInViewModel.signIn(email, password)
                }
        )
        if (state.loginSuccess) {
            LaunchedEffect(Unit) {
                navController.navigate("HomeScreen") {
                    popUpTo("SignInScreen") { inclusive = true }
                }
            }
        }
        if (state.loginError) {
            Toast.makeText(LocalContext.current, "Login Error", Toast.LENGTH_LONG).show()
        }
        CustomButton(
            text = "sign up",
            onClick = {
                Log.d("sign up", "가입할래우")
                navController.navigate("SignUpScreen") },
            modifier = Modifier
                .fillMaxWidth()
        )
    }
}

@Preview(showBackground = true)
@Composable
fun SignInScreenPreview() {
    SignInScreen(navController = rememberNavController(), SignInViewModel())
}