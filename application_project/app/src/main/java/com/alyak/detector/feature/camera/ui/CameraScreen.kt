package com.alyak.detector.feature.camera.ui


import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
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
import androidx.compose.ui.graphics.Shape

@Composable
fun CameraScreen(
    navController: NavController,
    viewModel: CameraViewModel = hiltViewModel(),
) {

    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val cameraPermission by viewModel.cameraPermission.collectAsState()
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
                        startCamera(this, lifecycleOwner, imageCapture)
                    }
                },
                modifier = Modifier.fillMaxSize()
            )
            CameraOverlay {
                imageCapture.takePicture(
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
    imageCapture: ImageCapture
) {
    val cameraProviderFuture = ProcessCameraProvider.getInstance(previewView.context)
    cameraProviderFuture.addListener({
        val cameraProvider = cameraProviderFuture.get()
        val preview = Preview.Builder().build().also {
            it.surfaceProvider = previewView.surfaceProvider
        }
        val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
        cameraProvider.unbindAll()
        cameraProvider.bindToLifecycle(
            lifecycleOwner, cameraSelector, preview,imageCapture
        )
    }, ContextCompat.getMainExecutor(previewView.context))
}