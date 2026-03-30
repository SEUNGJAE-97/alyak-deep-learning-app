package com.alyak.detector.feature.camera.ui


import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Rect
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
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import androidx.compose.ui.graphics.Shape
import com.google.zxing.BarcodeFormat
import com.google.zxing.BinaryBitmap
import com.google.zxing.DecodeHintType
import com.google.zxing.MultiFormatReader
import com.google.zxing.PlanarYUVLuminanceSource
import com.google.zxing.Result
import com.google.zxing.common.HybridBinarizer
import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicBoolean

@Composable
fun CameraScreen(
    navController: NavController,
    viewModel: CameraViewModel = hiltViewModel(),
    mode: String = CAMERA_MODE_PILL,
    onQrScanned: ((String) -> Unit)? = null
) {

    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val cameraPermission by viewModel.cameraPermission.collectAsState()
    val imageCapture = remember(mode) {
        if (mode == CAMERA_MODE_QR) null
        else ImageCapture.Builder()
            .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
            .build()
    }

    val analysisExecutor = remember(mode) {
        if (mode == CAMERA_MODE_QR) Executors.newSingleThreadExecutor() else null
    }
    DisposableEffect(analysisExecutor) {
        onDispose { analysisExecutor?.shutdown() }
    }

    val qrHandled = remember { AtomicBoolean(false) }
    val qrReader = remember {
        MultiFormatReader().apply {
            setHints(
                mapOf(
                    DecodeHintType.POSSIBLE_FORMATS to listOf(BarcodeFormat.QR_CODE),
                    DecodeHintType.TRY_HARDER to true
                )
            )
        }
    }

    val imageAnalysis = remember(mode, analysisExecutor) {
        if (mode != CAMERA_MODE_QR || analysisExecutor == null) return@remember null
        ImageAnalysis.Builder()
            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
            .build()
    }

    var lastQrText by remember { mutableStateOf<String?>(null) }
    LaunchedEffect(lastQrText) {
        val text = lastQrText ?: return@LaunchedEffect
        if (!qrHandled.compareAndSet(false, true)) return@LaunchedEffect
        onQrScanned?.invoke(text)
        navController.popBackStack()
    }

    DisposableEffect(mode, imageAnalysis, analysisExecutor) {
        if (mode == CAMERA_MODE_QR && imageAnalysis != null && analysisExecutor != null) {
            imageAnalysis.setAnalyzer(analysisExecutor) { image ->
                try {
                    if (qrHandled.get()) return@setAnalyzer
                    val decoded = decodeQrFromImageProxy(qrReader, image)
                    if (!decoded.isNullOrBlank()) {
                        lastQrText = decoded
                    }
                } finally {
                    image.close()
                }
            }
        }
        onDispose {
            imageAnalysis?.clearAnalyzer()
        }
    }

    val fallbackImageCapture = remember {
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
                        startCamera(
                            previewView = this,
                            lifecycleOwner = lifecycleOwner,
                            imageCapture = imageCapture ?: fallbackImageCapture,
                            imageAnalysis = imageAnalysis,
                            mode = mode
                        )
                    }
                },
                modifier = Modifier.fillMaxSize()
            )
            CameraOverlay(
                mode = mode,
                onClose = { navController.popBackStack() }
            ) {
                if (mode == CAMERA_MODE_QR) return@CameraOverlay
                (imageCapture ?: fallbackImageCapture).takePicture(
                    ContextCompat.getMainExecutor(context),
                    object : ImageCapture.OnImageCapturedCallback() {
                        override fun onCaptureSuccess(image: ImageProxy) {
                            val rotationDegrees = image.imageInfo.rotationDegrees
                            val bitmap = image.toBitmap()
                            val matrix = android.graphics.Matrix().apply {
                                postRotate(rotationDegrees.toFloat())
                            }
                            val rotatedBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)

                            val metrics = context.resources.displayMetrics
                            val screenRatio = metrics.widthPixels.toFloat() / metrics.heightPixels.toFloat()
                            val cropSize = (rotatedBitmap.height * screenRatio * 0.8f).toInt()

                            val left = (rotatedBitmap.width - cropSize) / 2
                            val top = (rotatedBitmap.height - cropSize) / 2
                            val finalCroppedBitmap = Bitmap.createBitmap(rotatedBitmap, left, top, cropSize, cropSize)
                            viewModel.setCapturedImage(finalCroppedBitmap)
                            image.close()

                            navController.navigate("ResultScreen")
                        }
                    }
                )
            }
        } else {
            // 권한이 없는 경우
        }
    }
}

@Composable
fun CameraOverlay(
    modifier: Modifier = Modifier,
    mode: String = CAMERA_MODE_PILL,
    onClose: () -> Unit = {},
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
                color = if (mode == CAMERA_MODE_QR) Color.White else Color.Yellow,
                topLeft = Offset(left, top),
                size = Size(overlayWidth, overlayHeight),
                cornerRadius = CornerRadius(cornerRadious, cornerRadious),
                style = Stroke(width = 4.dp.toPx())
            )
        }
        if (mode == CAMERA_MODE_QR) {
            Text(
                text = "QR코드를 스캔해 주세요.",
                color = Color.White,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 120.dp)
            )

            IconButton(
                onClick = onClose,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 32.dp)
                    .size(56.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.Close,
                    contentDescription = "닫기",
                    tint = Color.White
                )
            }
        } else {
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
}

const val CAMERA_MODE_PILL = "pill"
const val CAMERA_MODE_QR = "qr"

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
    imageAnalysis: ImageAnalysis? = null,
    mode: String = CAMERA_MODE_PILL
) {
    val cameraProviderFuture = ProcessCameraProvider.getInstance(previewView.context)
    cameraProviderFuture.addListener({
        val cameraProvider = cameraProviderFuture.get()
        val preview = Preview.Builder().build().also {
            it.surfaceProvider = previewView.surfaceProvider
        }
        val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
        cameraProvider.unbindAll()
        if (mode == CAMERA_MODE_QR && imageAnalysis != null) {
            cameraProvider.bindToLifecycle(
                lifecycleOwner,
                cameraSelector,
                preview,
                imageAnalysis
            )
        } else {
            cameraProvider.bindToLifecycle(
                lifecycleOwner,
                cameraSelector,
                preview,
                imageCapture
            )
        }
    }, ContextCompat.getMainExecutor(previewView.context))
}

private fun decodeQrFromImageProxy(reader: MultiFormatReader, image: ImageProxy): String? {
    val plane = image.planes.firstOrNull() ?: return null
    val buffer = plane.buffer
    val bytes = ByteArray(buffer.remaining())
    buffer.get(bytes)

    val width = image.width
    val height = image.height

    val rotation = image.imageInfo.rotationDegrees % 360
    val (data, w, h) = when (rotation) {
        90 -> Triple(rotateYPlane90(bytes, width, height), height, width)
        180 -> Triple(rotateYPlane180(bytes, width, height), width, height)
        270 -> Triple(rotateYPlane270(bytes, width, height), height, width)
        else -> Triple(bytes, width, height)
    }

    // 중앙 영역 위주로 시도(성능/정확도 개선)
    val rect = cropCenterRect(w, h, 0.72f)
    val source = PlanarYUVLuminanceSource(
        data,
        w,
        h,
        rect.left,
        rect.top,
        rect.width(),
        rect.height(),
        false
    )

    val bitmap = BinaryBitmap(HybridBinarizer(source))
    return try {
        val result: Result = reader.decodeWithState(bitmap)
        result.text
    } catch (_: Exception) {
        null
    } finally {
        reader.reset()
    }
}

private fun cropCenterRect(w: Int, h: Int, ratio: Float): Rect {
    val size = (minOf(w, h) * ratio).toInt().coerceAtLeast(1)
    val left = ((w - size) / 2).coerceAtLeast(0)
    val top = ((h - size) / 2).coerceAtLeast(0)
    return Rect(left, top, (left + size).coerceAtMost(w), (top + size).coerceAtMost(h))
}

private fun rotateYPlane90(src: ByteArray, width: Int, height: Int): ByteArray {
    val dst = ByteArray(src.size)
    var i = 0
    for (x in 0 until width) {
        for (y in height - 1 downTo 0) {
            dst[i++] = src[y * width + x]
        }
    }
    return dst
}

private fun rotateYPlane180(src: ByteArray, width: Int, height: Int): ByteArray {
    val dst = ByteArray(src.size)
    var i = 0
    for (p in src.size - 1 downTo 0) {
        dst[i++] = src[p]
    }
    return dst
}

private fun rotateYPlane270(src: ByteArray, width: Int, height: Int): ByteArray {
    val dst = ByteArray(src.size)
    var i = 0
    for (x in width - 1 downTo 0) {
        for (y in 0 until height) {
            dst[i++] = src[y * width + x]
        }
    }
    return dst
}