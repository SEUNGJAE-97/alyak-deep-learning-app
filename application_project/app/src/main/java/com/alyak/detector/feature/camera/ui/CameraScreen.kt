package com.alyak.detector.feature.camera.ui


import android.Manifest
import android.content.pm.PackageManager
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.Icon
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
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import androidx.compose.ui.graphics.Shape
import com.alyak.detector.feature.camera.detector.PillAnalyzer
import com.alyak.detector.feature.camera.detector.PillDetection
import com.alyak.detector.feature.camera.detector.PillDetector

@Composable
fun CameraScreen(
    navController: NavController,
    viewModel: CameraViewModel = hiltViewModel(),
) {

    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val cameraPermission by viewModel.cameraPermission.collectAsState()
    val pillDetector = remember { PillDetector(context) }
    val detectedObjects = remember { mutableStateOf<List<PillDetection>>(emptyList()) }
    val pillAnalyzer = remember {
        PillAnalyzer(pillDetector) { detections, _, _ ->
            detectedObjects.value = detections // 검출된 리스트 업데이트
        }
    }
    val imageCapture = remember {
        ImageCapture.Builder()
            .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
            .build()
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        viewModel.setCameraPermission(isGranted)
    }

    LaunchedEffect(Unit) {
        val granted = ContextCompat.checkSelfPermission(
            context, Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED
        viewModel.setCameraPermission(granted)
        if (!granted) {
            permissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        if (cameraPermission) {
            AndroidView(
                factory = { context ->
                    PreviewView(context).apply {
                        startCamera(this, lifecycleOwner, imageCapture, pillAnalyzer)
                    }
                },
                modifier = Modifier.fillMaxSize()
            )
            CameraOverlay {
                imageCapture.takePicture(
                    ContextCompat.getMainExecutor(context),
                    object : ImageCapture.OnImageCapturedCallback() {
                        override fun onCaptureSuccess(image: ImageProxy) {
                            val bitmap = image.toBitmap()
                            viewModel.setCapturedImage(bitmap)
                            image.close()
                            navController.navigate("ResultScreen")
                        }
                    }
                )
            }

            // 박스치기
            Canvas(modifier = Modifier.fillMaxSize()) {
                val screenW = size.width
                val screenH = size.height

                // 오버레이 사각형의 위치와 크기 계산 (기존 CameraOverlay와 동일한 로직)
//                val overlaySize = screenW * 0.8f
//                val offsetX = (screenW - overlaySize) / 2f
//                val offsetY = (screenH - overlaySize) / 2f

                val overlayWidth = screenW * 0.8f
                val overlayHeight = overlayWidth  // 정사각형 유지
                val overlayLeft = (screenW - overlayWidth) / 2f
                val overlayTop = (screenH - overlayHeight) / 2f

                detectedObjects.value.forEach { pill -> val rect = pill.boundingBox
                    // 90도 회전
                    val rotatedLeft = 1f - rect.bottom
                    val rotatedTop = rect.left
                    val rotatedRight = 1f - rect.top
                    val rotatedBottom = rect.right

                    val screenLeft = overlayLeft + rotatedLeft * overlayWidth
                    val screenTop = overlayTop + rotatedTop * overlayHeight
                    val screenRight = overlayLeft + rotatedRight * overlayWidth
                    val screenBottom = overlayTop + rotatedBottom * overlayHeight

                    drawRoundRect(
                        color = Color.Green,
                        topLeft = Offset(screenLeft, screenTop),
                        size = Size(screenRight - screenLeft, screenBottom - screenTop),
                        cornerRadius = CornerRadius(8.dp.toPx()),
                        style = Stroke(width = 4.dp.toPx())
                    )
                }
            }
        } else {
            // 권한이 없는 경우
        }
    }
}

@Composable
fun CameraOverlay(
    modifier: Modifier = Modifier,
    previewMode: Boolean = false,
    onCaptureClick: () -> Unit
) {
    Box(modifier = Modifier.fillMaxSize()) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val width = size.width
            val height = size.height

            val overlayWidth = width * 0.8f
            val overlayHeight = overlayWidth

            val left = (width - overlayWidth) / 2f
            val top = (height - overlayHeight) / 2f
            val cornerRadious = 32.dp.toPx()

            drawRect(
                color = Color.Black.copy(alpha = 0.6f)
            )

            drawRoundRect(
                color = Color.Transparent,
                topLeft = Offset(left, top),
                size = Size(overlayWidth, overlayHeight),
                cornerRadius = CornerRadius(cornerRadious, cornerRadious),
                blendMode = BlendMode.Clear
            )

            drawRoundRect(
                color = Color.Yellow,
                topLeft = Offset(left, top),
                size = Size(overlayWidth, overlayHeight),
                cornerRadius = CornerRadius(cornerRadious, cornerRadious),
                style = Stroke(width = 4.dp.toPx())
            )
        }
        IconElevatedButton(
            onClick = onCaptureClick,
            icon = Icons.Default.CameraAlt,
            iconDescription = "촬영 버튼",
            modifier = Modifier
                .padding(bottom = 50.dp)
                .size(80.dp)
                .align(Alignment.BottomCenter)

        )
    }
}

//@Composable
//@androidx.compose.ui.tooling.preview.Preview(showBackground = true)
//fun PreviewCameraScreen() {
//    CameraOverlay(previewMode = true, onCaptureClick = {})
//    CameraScreen(navController = rememberNavController())
//}

@Composable
fun IconElevatedButton(
    onClick: () -> Unit,
    icon: ImageVector? = null,
    iconDescription: String? = null,
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(50)

) {
    ElevatedButton(
        onClick = onClick,
        modifier = modifier,
        shape = shape,
        contentPadding = PaddingValues(0.dp)
    ) {
        if (icon != null) {
            Box(
                modifier = Modifier.size(40.dp),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = iconDescription,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}

fun startCamera(
    previewView: PreviewView,
    lifecycleOwner: LifecycleOwner,
    imageCapture: ImageCapture,
    analyzer: ImageAnalysis.Analyzer
) {
    val cameraProviderFuture = ProcessCameraProvider.getInstance(previewView.context)
    cameraProviderFuture.addListener({
        val cameraProvider = cameraProviderFuture.get()
        val preview = Preview.Builder().build().also {
            it.surfaceProvider = previewView.surfaceProvider
        }
        val imageAnalysis = ImageAnalysis.Builder()
            .setTargetResolution(android.util.Size(640, 640))
            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
            .build()
            .also {
                it.setAnalyzer(ContextCompat.getMainExecutor(previewView.context), analyzer)
            }
        val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
        cameraProvider.unbindAll()
        cameraProvider.bindToLifecycle(
            lifecycleOwner, cameraSelector, preview, imageAnalysis,imageCapture
        )
    }, ContextCompat.getMainExecutor(previewView.context))
}