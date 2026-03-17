package com.alyak.detector.feature.family.ui.invitation

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Sms
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.alyak.detector.feature.family.ui.invitation.component.InvitationOptionItem

@Composable
fun InvitationBottomSheet(
    viewModel: FamilyInvitationViewModel = hiltViewModel(),
    onDismiss: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .navigationBarsPadding() // 네비게이션 바 영역 확보
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // QR 코드 초대
            InvitationOptionItem(
                title = "QR 코드로 추가하기",
                description = "초대하고자 하는 사람의 QR 코드를 스캔",
                icon = Icons.Default.QrCodeScanner,
                onClick = { /* NavController를 이용해 QR 화면으로 이동 */ }
            )

            // 이메일 초대
            InvitationOptionItem(
                title = "이메일로 추가하기",
                description = "초대하고자 하는 사람의 이메일을 입력",
                icon = Icons.Default.Email,
                onClick = { /* 이메일 발송 로직 실행 */ }
            )

            // SMS 초대 (비활성화)
            InvitationOptionItem(
                title = "휴대폰 번호로 추가하기",
                description = "미구현",
                icon = Icons.Default.Sms,
                iconColor = Color.LightGray,
                onClick = { /* 처리 없음 */ }
            )
        }
    }
}