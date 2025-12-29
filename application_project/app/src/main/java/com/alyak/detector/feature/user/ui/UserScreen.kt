package com.alyak.detector.feature.user.ui

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.alyak.detector.R
import com.alyak.detector.feature.user.data.model.UserEvent
import com.alyak.detector.feature.user.ui.components.SettingHeaderForm
import com.alyak.detector.ui.components.ContentBox
import com.alyak.detector.ui.components.CustomButton
import com.alyak.detector.ui.components.CustomModalDialog

@Composable
fun UserScreen(
    navController: NavController,
    viewModel: UserViewModel = hiltViewModel()
) {
    var isLogoutDialogVisible by remember { mutableStateOf(false) }
    var isAccountDeletionDialogVisible by remember { mutableStateOf(false) }
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.event.collect{ event ->
            when(event){
                is UserEvent.LogoutSuccess, is UserEvent.DeleteAccountSuccess -> {
                    navController.navigate("SignInScreen") {
                        popUpTo(0) { inclusive = true }
                        launchSingleTop = true
                    }
                }
                is UserEvent.Error -> {
                    Toast.makeText(context, event.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    Scaffold(
        topBar = { SettingHeaderForm() }
    )
    { paddingValues ->
        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .padding(paddingValues)
        ) {
            ContentBox(
                Modifier
                    .padding(10.dp)
                    .shadow(3.dp, RoundedCornerShape(40.dp))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {


                    EditableProfileImage()

                    Spacer(Modifier.height(20.dp))
                    Text(
                        "하승재",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.Black
                    )
                    Spacer(Modifier.height(10.dp))
                    Text(
                        "sj_hahaha@naver.com",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Thin,
                        color = Color.Black
                    )

                }
            }
            Spacer(Modifier.height(20.dp))

            // 로그아웃 버튼
            CustomButton(
                text = "로그아웃",
                onClick = { isLogoutDialogVisible = true },
                image = painterResource(R.drawable.logout_24px),
                containerColor = colorResource(R.color.primaryBlue),
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                imageSize = 20.dp,
                contentDescription = null,
                contentColor = colorResource(R.color.white),
                textColor = colorResource(R.color.white)
            )

            if (isLogoutDialogVisible) {
                CustomModalDialog(
                    onDismiss = {
                        isLogoutDialogVisible = false
                    },
                    onConfirm = {
                        viewModel.logout()
                    },
                    mainText = "로그아웃 하시겠습니까?",
                    subText = "로그아웃 후 언제든지 다시 로그인 가능합니다.",
                    btnText1 = "취소",
                    btnText2 = "로그아웃"
                )
            }

            Spacer(Modifier.height(10.dp))

            // 로그아웃 버튼
            CustomButton(
                text = "회원탈퇴",
                onClick = { isAccountDeletionDialogVisible = true },
                image = painterResource(R.drawable.account_circle_off_24px),
                containerColor = colorResource(R.color.RealRed).copy(alpha = 0.1f),
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        shape = RoundedCornerShape(20.dp),
                        color = Color.Transparent
                    ),
                shape = RoundedCornerShape(20.dp),
                imageSize = 20.dp,
                contentColor = Color.White,
                textColor = colorResource(R.color.RealRed)
            )

            if(isAccountDeletionDialogVisible){
                CustomModalDialog(
                    onDismiss = {
                        isAccountDeletionDialogVisible = false
                    },
                    onConfirm = {
                        viewModel.deleteAccount()
                    },
                    mainText = "회원탈퇴 하시겠습니까?",
                    subText = "삭제된 정보는 복구가 불가합니다.",
                    btnText1 = "계속 이용하기",
                    btnText2 = "탈퇴하기"
                )
            }
        }
    }

}

@Composable
fun EditableProfileImage(
    modifier: Modifier = Modifier,
    onClickEdit: () -> Unit = {}
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.BottomEnd
    ) {
        Image(
            painter = painterResource(R.drawable.dummy_profile),
            contentDescription = "사용자 프로필",
            modifier = Modifier
                .size(120.dp)
                .clip(CircleShape)
                .border(
                    width = 3.dp,
                    color = colorResource(R.color.primaryBlue),
                    shape = CircleShape
                ),
            contentScale = ContentScale.Crop
        )

        Surface(
            modifier = Modifier
                .offset(x = 6.dp, y = 6.dp)
                .size(40.dp)
                .clickable { onClickEdit() },
            shape = CircleShape,
            color = colorResource(R.color.primaryBlue),
            shadowElevation = 6.dp,
            tonalElevation = 6.dp
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.fillMaxSize()
            ) {
                Icon(
                    painter = painterResource(R.drawable.add_a_photo_24px),
                    contentDescription = "프로필 사진 변경",
                    tint = Color.White,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}