package com.alyak.detector.feature.camera.ui

import android.os.Build
import androidx.compose.foundation.Canvas
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
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.ImageLoader
import coil.compose.AsyncImage
import coil.decode.GifDecoder
import coil.decode.ImageDecoderDecoder
import com.alyak.detector.R
import com.alyak.detector.feature.camera.detector.PillDetection
import com.alyak.detector.feature.camera.detector.PillDetector
import com.valentinilk.shimmer.shimmer
import java.util.Collections.emptyList

@Composable
fun ResultScreen(
    navController: NavController,
    viewModel: CameraViewModel,
) {
    val context = LocalContext.current
    val pillDetector = remember { PillDetector(context) }

    val bitmap by viewModel.capturedBitmap.collectAsState()
    val detectedObjects = remember { mutableStateOf<List<PillDetection>>(emptyList()) }
    val isLoading = remember { mutableStateOf(true) }
    val imageLoader = remember {
        ImageLoader.Builder(context)
            .components {
                if (Build.VERSION.SDK_INT >= 28) {
                    add(ImageDecoderDecoder.Factory())
                } else {
                    add(GifDecoder.Factory())
                }
            }
            .build()
    }

    LaunchedEffect(bitmap) {
        bitmap?.let { btm ->
            isLoading.value = true
            kotlinx.coroutines.delay(2000)
            pillDetector.processImage(
                bitmap = btm,
                onSuccess = { detections ->
                    detectedObjects.value = detections
                    isLoading.value = false
                },
                onFailure = {
                    isLoading.value = false
                }
            )
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8F9FA))
    ) {
        // 1. 이미지 및 결과 영역
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1.3f)
                .padding(12.dp)
                .graphicsLayer {
                    shape = RoundedCornerShape(24.dp)
                    clip = true
                    shadowElevation = 15f
                    spotShadowColor = Color.Black.copy(alpha = 0.3f)
                }
                .background(Color.White),
            contentAlignment = Alignment.Center
        ) {
            bitmap?.let { btm ->
                Image(
                    bitmap = btm.asImageBitmap(),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Fit
                )
                ResultCanvas(detections = detectedObjects.value)
            }

            if (isLoading.value) {
                AsyncImage(
                    model = R.raw.result2,
                    contentDescription = "Loading",
                    imageLoader = imageLoader,
                    modifier = Modifier.size(150.dp)
                )
            } else {
                ResultCanvas(detections = detectedObjects.value)
            }
        }

        // 2. 정보 및 버튼 영역
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = if (isLoading.value) "알약을 분석하고 있어요" else "분석 완료!",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            // 검출 개수 표시
            Surface(
                tonalElevation = 2.dp,
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
                    .then(if(isLoading.value) Modifier.shimmer() else Modifier)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF4CAF50))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "총 ${detectedObjects.value.size}개의 알약을 찾았습니다.",
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            // 3. 버튼 그룹
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = { navController.popBackStack() },
                    modifier = Modifier.weight(1f).height(56.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = Color.Black
                    )
                ) {
                    Text("다시 찍기")
                }

                Button(
                    onClick = { /* 서버 전송 로직 */ },
                    modifier = Modifier.weight(1f).height(56.dp),
                    enabled = detectedObjects.value.isNotEmpty() && !isLoading.value,
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6200EE))
                ) {
                    Text("결과 전송하기", color = Color.White)
                }
            }
        }
    }
}

@Composable
fun ResultCanvas(
    detections: List<PillDetection>
) {
    Canvas(modifier = Modifier.fillMaxSize()) {
        val canvasW = size.width
        val canvasH = size.height
        detections.forEach { pill ->
            val rect = pill.boundingBox
            val left = rect.left * canvasW
            val top = rect.top * canvasH
            val right = rect.right * canvasW
            val bottom = rect.bottom * canvasH

            drawRoundRect(
                color = Color.Green,
                topLeft = Offset(left, top),
                size = Size(right - left, bottom - top),
                cornerRadius = CornerRadius(8.dp.toPx()),
                style = Stroke(width = 4.dp.toPx())
            )
        }
    }
}

