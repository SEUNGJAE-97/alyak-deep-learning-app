package com.alyak.detector.feature.auth.ui.signIn

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.alyak.detector.R
import com.alyak.detector.feature.auth.ui.signIn.components.FindPasswordForm

@Composable
fun FindPasswordScreen(navController: NavController) {
    BackHandler { navController.popBackStack() }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.horizontalGradient(
                    colorStops = arrayOf(
                        0.0f to colorResource(id = R.color.pink),
                        1.0f to colorResource(id = R.color.primaryBlue)
                    )
                )
            ),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "비밀번호 찾기\n이메일을 입력해주세요",
            style = MaterialTheme.typography.headlineMedium,
            color = Color.White,
            fontWeight = FontWeight.Bold,
            lineHeight = 80.sp,
            modifier = Modifier
                .padding(
                    start = 0.dp,
                    top = 50.dp,
                    end = 80.dp
                )
        )

        Spacer(modifier = Modifier.height(70.dp))

        FindPasswordForm()
    }
}
