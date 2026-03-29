package com.alyak.detector.feature.family.ui.invitation.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.text.font.FontWeight
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
        is InvitationUiState.Loading -> {
            CircularProgressIndicator(
                modifier = Modifier.size(40.dp),
                color = Color(0xFF5864D9)
            )
            Text("QR 코드를 생성 중입니다...", modifier = Modifier.padding(top = 8.dp), fontSize = 13.sp)
        }
        is InvitationUiState.Success -> {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White, RoundedCornerShape(16.dp)),
                horizontalAlignment = Alignment.Start
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 4.dp, vertical = 16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                    Box(
                        modifier = Modifier
                            .size(160.dp)
                            .background(Color.White, RoundedCornerShape(12.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            bitmap = state.inviteCode.asImageBitmap(),
                            contentDescription = "Invitation QR Code",
                            modifier = Modifier.fillMaxSize()
                        )
                    }

                    Spacer(Modifier.width(16.dp))

                    Column(
                        horizontalAlignment = Alignment.Start,
                        verticalArrangement = Arrangement.Center
                    ) {
                        if (!state.isExpired) {
                            FlipCounter(
                                targetNumber = state.remainingSeconds,
                                cardSize = 42.dp,
                                textColor = TextColor
                            )
                        } else {
                            Text(
                                text = "만료되었습니다.",
                                color = Color.Red,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                        }
                        Button(
                            onClick = { onClickRegenerate() },
                            modifier = Modifier.padding(top = 8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Refresh,
                                contentDescription = "",
                                modifier = Modifier.size(16.dp)
                            )
                            Text(text = "새로 발급")
                        }
                    }
                    }
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