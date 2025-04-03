package com.alyak.detector.ui.signUp

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.alyak.detector.ui.components.CustomButton

@Composable
fun SignUpScreen(){
    var email by remember { mutableStateOf(" ") }
    var password by remember { mutableStateOf(" ") }
    var userName by remember { mutableStateOf(" ") }
    var userPhoneNumber by remember { mutableStateOf(" ") }
    var userBirthDay by remember { mutableStateOf(" ") }
    var userAddress by remember { mutableStateOf(" ") }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        Text(text = "Sign Up", style = MaterialTheme.typography.headlineMedium)

        Spacer(modifier = Modifier.height(16.dp))

        //Email input
        OutlinedTextField(
            value = email,
            onValueChange = {email = it},
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        //password input
        OutlinedTextField(
            value = password,
            onValueChange = {password = it},
            label = { Text("password") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        //userName input
        OutlinedTextField(
            value = userName,
            onValueChange = {userName = it},
            label = { Text("userName") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        //userPhoneNumber input
        OutlinedTextField(
            value = userPhoneNumber,
            onValueChange = {userPhoneNumber = it},
            label = { Text("userPhoneNumber") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        //userBirthDay input
        OutlinedTextField(
            value = userBirthDay,
            onValueChange = {userBirthDay = it},
            label = { Text("userBirthDay") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        //userAddress input
        OutlinedTextField(
            value = userAddress,
            onValueChange = {userAddress = it},
            label = { Text("userAddress") },
            modifier = Modifier.fillMaxWidth()
        )

        //Sign Up Button
        CustomButton(
            text = "sign up",
            onClick = { Log.d("sign up" , "가입할래우")},
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
@Preview(showBackground = true)
fun SignUpScreenPreview(){
    SignUpScreen()
}