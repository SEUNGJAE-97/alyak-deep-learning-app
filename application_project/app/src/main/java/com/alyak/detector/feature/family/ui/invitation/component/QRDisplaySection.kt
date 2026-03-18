package com.alyak.detector.feature.family.ui.invitation.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.alyak.detector.feature.family.ui.invitation.InvitationUiState

@Composable
fun QRDisplaySection(uiState: InvitationUiState) {
    when (uiState) {
        // 로딩
        is InvitationUiState.Loading -> {
            CircularProgressIndicator(
                modifier = Modifier.size(40.dp),
                color = Color(0xFF5864D9)
            )
            Text("QR 코드를 생성 중입니다...", modifier = Modifier.padding(top = 8.dp), fontSize = 13.sp)
        }
        // 성공 시 QR 코드 표시
        is InvitationUiState.Success -> {
            Box(
                modifier = Modifier
                    .size(220.dp)
                    .background(Color.White, RoundedCornerShape(16.dp))
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    bitmap = uiState.inviteCode.asImageBitmap(),
                    contentDescription = "Invitation QR Code",
                    modifier = Modifier.fillMaxSize()
                )
            }
            Text("상대방이 이 코드를 스캔하면 가족으로 추가됩니다.",
                modifier = Modifier.padding(top = 12.dp),
                fontSize = 12.sp,
                color = Color.Gray
            )
        }
        is InvitationUiState.Error -> {
            Text(uiState.message, color = Color.Red)
        }
        else -> {}
    }
}