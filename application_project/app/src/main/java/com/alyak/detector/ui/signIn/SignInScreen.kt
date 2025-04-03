package com.alyak.detector.ui.signIn

import android.util.Log
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text2.BasicSecureTextField
import androidx.compose.foundation.text2.input.TextFieldState
import androidx.compose.foundation.text2.input.TextObfuscationMode
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.alyak.detector.ui.components.CustomButton

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun SignInScreen(){
    var email by remember { mutableStateOf(" ") }
    var password by remember { mutableStateOf(false) }
    val state = remember {TextFieldState()}
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
        BasicSecureTextField(
            state = state,
            textObfuscationMode =
            if(password){
                TextObfuscationMode.Visible
            }else{
                TextObfuscationMode.RevealLastTyped
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            decorator = {
                innerTextField -> Box(       )
            }
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
            onClick = { Log.d("sign In" , "가입할래우")},
            modifier = Modifier.padding(16.dp).fillMaxWidth()
        )
    }
}

@Preview(showBackground = true)
@Composable
fun SignInScreenPreview(){
    SignInScreen()
}