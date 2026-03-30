package com.alyak.detector.feature.camera.ui

import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageCapture
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner

/**
 * Preview/ImageCapture/ImageAnalysis use case를 lifecycle에 바인딩합니다.
 *
 * - pill 모드: Preview + ImageCapture
 * - qr 모드: Preview + ImageAnalysis
 */
fun startCamera(
    previewView: PreviewView,
    lifecycleOwner: LifecycleOwner,
    imageCapture: ImageCapture,
    imageAnalysis: ImageAnalysis? = null,
    mode: String = CAMERA_MODE_PILL,
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

