package com.alyak.detector.feature.camera.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.alyak.detector.feature.camera.detector.PillDetection
import com.alyak.detector.feature.camera.detector.PillDetector

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

    LaunchedEffect(bitmap) {
        bitmap?.let { btm ->
            isLoading.value = true
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

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        bitmap?.let { btm ->
            val configuration = LocalConfiguration.current
            val overlaySize = (configuration.screenWidthDp * 0.8f).dp

            Box(modifier = Modifier.size(overlaySize)) {
                Image(
                    bitmap = btm.asImageBitmap(),
                    contentDescription = "Captured Pill Image",
                    modifier = Modifier.fillMaxSize()
                )

                if (isLoading.value) {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                } else {
                    ResultCanvas(detections = detectedObjects.value)
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