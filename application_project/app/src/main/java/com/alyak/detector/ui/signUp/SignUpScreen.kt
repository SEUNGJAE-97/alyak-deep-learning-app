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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.alyak.detector.ui.components.CustomButton
import com.alyak.detector.viewModel.SignUpViewModel

@Composable
fun SignUpScreen(
    navController: NavController,
    signUpViewModel: SignUpViewModel
) {
    val state by signUpViewModel.state.collectAsState()
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var checkPassword by remember { mutableStateOf("") }
    var userName by remember { mutableStateOf(" ") }
    var userPhoneNumber by remember { mutableStateOf("") }
    var userSSN by remember { mutableStateOf("") }


    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Sign Up", style = MaterialTheme.typography.headlineMedium)

        Spacer(modifier = Modifier.height(16.dp))

        //Email input
        OutlinedTextField(
            value = email,
            onValueChange = {
                email = it
                signUpViewModel.validateEmail(it)
            },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth()
        )
        if (email.isNotEmpty() && !state.validEmail) {
            Text("Invalid Email Format please check", color = Color.Red, fontSize = 10.sp)
        }

        Spacer(modifier = Modifier.height(16.dp))

        //password input
        OutlinedTextField(
            value = password,
            onValueChange = {
                password = it
                signUpViewModel.validatePassword(it)
            },
            label = { Text("password") },
            modifier = Modifier.fillMaxWidth()
        )
        if (password.isNotEmpty() && !state.validPassword) {
            Text("영문, 숫자, 특수문자 중 2가지 이상을 조합해 최소 8자리를 입력해주세요", color = Color.Red, fontSize = 10.sp)
        }

        Spacer(modifier = Modifier.height(16.dp))

        //userAddress input
        OutlinedTextField(
            value = checkPassword,
            onValueChange = { checkPassword = it },
            label = { Text("Password check") },
            modifier = Modifier.fillMaxWidth()
        )
        if (checkPassword != password) {
            Text("비밀번호를 확인해주세요", color = Color.Red, fontSize = 10.sp)
        }

        Spacer(modifier = Modifier.height(16.dp))

        //userName input
        OutlinedTextField(
            value = userName,
            onValueChange = { userName = it },
            label = { Text("userName") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        //userPhoneNumber input
        OutlinedTextField(
            value = userPhoneNumber,
            onValueChange = {
                userPhoneNumber = it
                signUpViewModel.validatePhoneNumber(it)
            },
            label = { Text("userPhoneNumber") },
            modifier = Modifier.fillMaxWidth()
        )
        if (userPhoneNumber.isNotEmpty() && !state.validPhoneNumber) {
            Text("유효하지 않은 전화번호 입니다.", color = Color.Red, fontSize = 10.sp)
        }

        Spacer(modifier = Modifier.height(16.dp))

        //userBirthDay input
        OutlinedTextField(
            value = userSSN,
            onValueChange = {
                userSSN = it
                signUpViewModel.validateSSN(it)
            },
            label = { Text("userSSN") },
            modifier = Modifier.fillMaxWidth()
        )
        if (userSSN.isNotEmpty() && !state.validSSN) {
            Text(
                "유효하지 않은 주민등록번호 입니다.",
                color = Color.Red,
                fontSize = 10.sp,
            )
        }

        //Sign Up Button
        CustomButton(
            text = "sign up",
            onClick = { Log.d("sign up", "가입할래우") },
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
@Preview(showBackground = true)
fun SignUpScreenPreview() {
    SignUpScreen(navController = rememberNavController(), SignUpViewModel())
}