package com.alyak.detector.feature.family.ui.invitation.component

import androidx.compose.ui.graphics.asComposeRenderEffect
import android.graphics.RenderEffect as NativeRenderEffect
import android.graphics.Shader
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asComposeRenderEffect
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.alyak.detector.R
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
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White, RoundedCornerShape(16.dp)),
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 4.dp, vertical = 16.dp)
                        .graphicsLayer {
                            renderEffect = if (state.isExpired) {
                                NativeRenderEffect.createBlurEffect(
                                    20f,
                                    20f,
                                    Shader.TileMode.DECAL
                                ).asComposeRenderEffect()
                            } else {
                                null
                            }
                        }
                        .alpha(if (state.isExpired) 0.5f else 1f),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    val qrSize = 160.dp
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(qrSize),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(qrSize)
                                .background(Color.White, RoundedCornerShape(12.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Image(
                                bitmap = state.inviteCode.asImageBitmap(),
                                contentDescription = "Invitation QR Code",
                                modifier = Modifier.fillMaxSize()
                            )
                        }

                        Spacer(Modifier.width(10.dp))

                        Column(
                            horizontalAlignment = Alignment.Start,
                            verticalArrangement = Arrangement.Center
                        ) {
                            FlipCounter(
                                targetNumber = state.remainingSeconds,
                                cardWidth = 72.dp,
                                cardHeight = qrSize * 0.6f,
                                textColor = TextColor
                            )
                        }
                    }

                    Text(
                        text = "5분 안에 가족이 이 QR 코드를 스캔하면 초대가 완료됩니다.",
                        textAlign = TextAlign.Start,
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 12.dp, start =25.dp, end = 4.dp)
                    )
                }

                if (state.isExpired) {
                    Box(
                        modifier = Modifier
                            .matchParentSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "QR이 만료되었습니다",
                                color = Color.Black,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 16.sp
                            )
                            Button(
                                onClick = onClickRegenerate,
                                modifier = Modifier.padding(top = 10.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = colorResource(R.color.primaryBlue),
                                    contentColor = Color.White
                                )
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Refresh,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(Modifier.width(4.dp))
                                Text(text = "새로 발급")
                            }
                        }
                    }
                }
            }
        }
        is InvitationUiState.Error -> {
            Text(state.message, color = Color.Red)
        }
        else -> {}
    }
}