package com.alyak.detector.feature.camera.ui

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class CameraViewModel @Inject constructor() : ViewModel() {

    val cameraPermission = MutableStateFlow(false)
    private val _capturedBitmap = MutableStateFlow<Bitmap?>(null)
    val capturedBitmap = _capturedBitmap.asStateFlow()

    fun setCameraPermission(granted: Boolean) {
        cameraPermission.value = granted
    }

    fun setCapturedImage(bitmap: Bitmap){
        _capturedBitmap.value = bitmap
    }
}

