package com.alyak.detector.feature.family.ui.invitation.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.alyak.detector.feature.family.ui.invitation.InvitationUiState

@Composable
fun QRDisplaySection(
    state: InvitationUiState,
    onClickRegenerate: () -> Unit
) {
    when (state) {
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
//            Box(
//                modifier = Modifier
//                    .size(220.dp)
//                    .background(Color.White, RoundedCornerShape(16.dp))
//                    .padding(16.dp),
//                contentAlignment = Alignment.Center
//            ) {
//                Image(
//                    bitmap = uiState.inviteCode.asImageBitmap(),
//                    contentDescription = "Invitation QR Code",
//                    modifier = Modifier.fillMaxSize()
//                )
//            }
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .background(Color.White, RoundedCornerShape(16.dp))
                    .padding(16.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(220.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        bitmap = state.inviteCode.asImageBitmap(),
                        contentDescription = "Invitation QR Code",
                        modifier = Modifier.fillMaxSize()
                    )
                }

                Text(
                    text = if (!state.isExpired)
                        "남은 시간: ${state.remainingSeconds}초"
                    else
                        "QR 코드가 만료되었습니다.",
                    color = if (state.remainingSeconds <= 30 && !state.isExpired) Color.Red else Color.Gray
                )

                Button(
                    onClick = { onClickRegenerate() },
                    modifier = Modifier.padding(top = 8.dp)
                ) {
                    Text(text = "QR 코드 새로 발급")
                }

                Text(
                    text = "5분 안에 가족이 이 QR 코드를 스캔하면 초대가 완료됩니다.",
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
        }
        is InvitationUiState.Error -> {
            Text(state.message, color = Color.Red)
        }
        else -> {}
    }
}