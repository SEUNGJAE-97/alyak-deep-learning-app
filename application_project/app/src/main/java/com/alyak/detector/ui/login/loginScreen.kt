package com.alyak.detector.ui.login

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.alyak.detector.ui.components.CustomButton
import java.util.Scanner

@Composable
fun loginScreen(){
    var email by remember { mutableStateOf(" ") }
    var password by remember { mutableStateOf(" ") }

    //ui layout
    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "login", style = MaterialTheme.typography.headlineMedium)

        Spacer(modifier = Modifier.height(16.dp))

        //Email input
        OutlinedTextField(
            value = email,
            onValueChange = {email = it},
            label = { Text("Email")},
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        //password input
        OutlinedTextField(
            value = password,
            onValueChange = {password = it},
            label = { Text("Password")},
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))

        // login Button
        CustomButton(
            text = "sign in",
            onClick = { Log.d("sign in", "$email , $password")},
            modifier = Modifier.padding(16.dp).fillMaxWidth()
        )

        CustomButton(
            text = "sign up",
            onClick = { Log.d("sign up" , "가입할래우")},
            modifier = Modifier.padding(16.dp).fillMaxWidth()
        )
    }
}

@Preview(showBackground = true)
@Composable
fun loginScreenPreview(){
    loginScreen()
}