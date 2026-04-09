package com.alyak.detector.feature.auth.ui.signUp.components

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.alyak.detector.R
import com.alyak.detector.feature.auth.ui.signUp.SignUpState
import com.alyak.detector.feature.auth.ui.signUp.SignUpViewModel
import com.alyak.detector.feature.user.ui.EditableProfileImage
import com.alyak.detector.ui.components.ContentBox
import com.alyak.detector.ui.components.CustomButton
import com.alyak.detector.ui.components.CustomUnderlineTextField

@Composable
fun SignUpProfileStep(
    viewModel: SignUpViewModel,
    navController: NavController,
    onBackToEmailStep: () -> Unit,
) {
    val state by viewModel.state.collectAsState()
    val signUpResult by viewModel.signUpResult.collectAsState()
    val context = LocalContext.current

    SignUpProfileStepContent(
        state = state,
        signUpResult = signUpResult,
        onPasswordChanged = { viewModel.validatePassword(it) },
        onSignUp = { password, userName -> viewModel.signUpUser(password, userName, context) },
        navController = navController,
        onBackToEmailStep = onBackToEmailStep
    )
}

@Composable
fun SignUpProfileStepContent(
    state: SignUpState,
    signUpResult: Result<com.alyak.detector.feature.auth.data.model.SignUpResponse>? = null,
    onPasswordChanged: (String) -> Unit,
    onSignUp: (String, String) -> Unit,
    navController: NavController,
    onBackToEmailStep: () -> Unit,
) {
    val context = LocalContext.current

    var password by remember { mutableStateOf("") }
    var checkPassword by remember { mutableStateOf("") }
    var userName by remember { mutableStateOf("") }
    var isPasswordVisible by remember { mutableStateOf(false) }
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    val pickMedia = rememberLauncherForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            imageUri = uri
        }
    }
    LaunchedEffect(signUpResult) {
        signUpResult?.onSuccess {
            Toast.makeText(context, "회원가입 성공!", Toast.LENGTH_SHORT).show()
            navController.popBackStack()
        }
        signUpResult?.onFailure {
            Toast.makeText(context, it.message ?: "알 수 없는 오류 발생", Toast.LENGTH_SHORT).show()
        }
    }

    ContentBox(modifier = Modifier.fillMaxWidth()) {
        EditableProfileImage(
            modifier = Modifier.align(Alignment.CenterHorizontally),
            selectedImageUri = imageUri,
            onClickEdit = {
                pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
            }
        )
        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "비밀번호",
            color = colorResource(R.color.primaryBlue)
        )

        Spacer(modifier = Modifier.height(20.dp))

        CustomUnderlineTextField(
            value = password,
            onValueChange = {
                password = it
                onPasswordChanged(it)
            },
            hint = "비밀번호",
            trailingIcon = {
                Icon(
                    imageVector = if (isPasswordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                    contentDescription = "Toggle password visibility",
                    modifier = Modifier
                        .clickable { isPasswordVisible = !isPasswordVisible }
                        .size(24.dp)
                )
            },
            visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation()
        )

        Text(
            text = "10자리 이상의 영문, 숫자, 특수기호를 포함해야합니다.",
            color = colorResource(R.color.lightGray),
            fontSize = 10.sp
        )

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "비밀번호 재입력",
            color = colorResource(R.color.primaryBlue)
        )

        Spacer(modifier = Modifier.height(20.dp))

        CustomUnderlineTextField(
            value = checkPassword,
            onValueChange = { checkPassword = it },
            hint = "비밀번호 재입력",
            trailingIcon = {
                Icon(
                    painter = painterResource(R.drawable.cancle),
                    modifier = Modifier
                        .size(24.dp)
                        .clickable { checkPassword = "" },
                    contentDescription = "check password state"
                )
            }
        )

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "이름",
            color = colorResource(R.color.primaryBlue)
        )

        Spacer(modifier = Modifier.height(20.dp))

        CustomUnderlineTextField(
            value = userName,
            onValueChange = { userName = it },
            hint = "이름"
        )

        Spacer(modifier = Modifier.height(30.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier
                    .size(65.dp)
                    .clickable { onBackToEmailStep() },
                shape = RoundedCornerShape(50.dp),
                color = colorResource(R.color.white),
                shadowElevation = 2.dp
            ) {
                Image(
                    painter = painterResource(R.drawable.arrow_back),
                    contentDescription = "이전 단계",
                    modifier = Modifier
                        .padding(12.dp)
                        .size(30.dp)
                )
            }

            Spacer(modifier = Modifier.padding(16.dp))

            CustomButton(
                text = "",
                onClick = {
                    when {
                        !state.emailVerified ->
                            Toast.makeText(context, "이메일 인증 정보가 없습니다.", Toast.LENGTH_SHORT).show()

                        password != checkPassword ->
                            Toast.makeText(context, "비밀번호가 일치하지 않습니다.", Toast.LENGTH_SHORT).show()

                        !state.validPassword ->
                            Toast.makeText(context, "비밀번호 형식을 확인해주세요.", Toast.LENGTH_SHORT).show()

                        userName.isBlank() ->
                            Toast.makeText(context, "이름을 입력해주세요.", Toast.LENGTH_SHORT).show()

                        else -> onSignUp(password, userName)
                    }
                },
                image = painterResource(R.drawable.arrow),
                containerColor = colorResource(R.color.primaryBlue),
                modifier = Modifier.size(80.dp),
                shape = RoundedCornerShape(50.dp),
                imageSize = 40.dp
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SignUpProfileStepPreview() {
    val navController = rememberNavController()
    MaterialTheme {
        Surface(color = Color.White) {
            SignUpProfileStepContent(
                state = SignUpState(),
                signUpResult = null,
                onPasswordChanged = {},
                onSignUp = { _, _ -> },
                navController = navController,
                onBackToEmailStep = { /* 이전 단계 이동 액션 */ }
            )
        }
    }
}
