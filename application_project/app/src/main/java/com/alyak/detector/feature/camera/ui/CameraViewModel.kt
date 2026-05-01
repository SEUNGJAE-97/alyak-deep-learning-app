package com.alyak.detector.feature.camera.ui

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alyak.detector.feature.camera.data.model.PillDetection
import com.alyak.detector.feature.camera.data.repository.CameraRepo
import com.alyak.detector.feature.pill.data.model.MedicineInfoDto
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CameraViewModel @Inject constructor(
    private val cameraRepo: CameraRepo
) : ViewModel() {

    val cameraPermission = MutableStateFlow(false)

    private val _capturedBitmap = MutableStateFlow<Bitmap?>(null)
    val capturedBitmap = _capturedBitmap.asStateFlow()

    private val _isSending = MutableStateFlow(false)
    val isSending = _isSending.asStateFlow()

    private val _sendResult = MutableStateFlow<List<MedicineInfoDto>?>(null)
    val sendResult = _sendResult.asStateFlow()

    private val _sendError = MutableStateFlow<String?>(null)
    val sendError = _sendError.asStateFlow()

    private val _detectedPillBitmaps = MutableStateFlow<List<Bitmap>>(emptyList())
    val detectedPillBitmaps = _detectedPillBitmaps.asStateFlow()
    private val _detections = MutableStateFlow<List<PillDetection>>(emptyList())
    val detections = _detections.asStateFlow()

    fun setCameraPermission(granted: Boolean) {
        cameraPermission.value = granted
    }

    fun setCapturedImage(bitmap: Bitmap) {
        _capturedBitmap.value = bitmap
    }

    fun setDetectedPills(originalBitmap: Bitmap, detections: List<PillDetection>) {
        _detections.value = detections
        viewModelScope.launch {
            val cropped = cropDetectedPills(originalBitmap, detections)
            _detectedPillBitmaps.value = cropped
        }
    }

    fun sendImage() {
        val originalBitmap = _capturedBitmap.value ?: return
        val detectedImages = _detectedPillBitmaps.value
        val detections = _detections.value

        viewModelScope.launch {
            _isSending.value = true
            _sendError.value = null
            try {
                val result = cameraRepo.sendImage(
                    originalImage = originalBitmap,
                    detectedImages = detectedImages,
                    detections = detections
                )
                _sendResult.value = result
            } catch (e: Exception) {
                _sendError.value = e.message
            } finally {
                _isSending.value = false
            }
        }
    }

    private fun cropDetectedPills(
        originalBitmap: Bitmap,
        detections: List<PillDetection>
    ): List<Bitmap> {
        return detections.mapNotNull { detection ->
            val rect = detection.boundingBox // RectF 객체

            try {
                val left = (rect.left * originalBitmap.width).toInt().coerceIn(0, originalBitmap.width - 1)
                val top = (rect.top * originalBitmap.height).toInt().coerceIn(0, originalBitmap.height - 1)
                val width = (rect.width() * originalBitmap.width).toInt().coerceAtMost(originalBitmap.width - left)
                val height = (rect.height() * originalBitmap.height).toInt().coerceAtMost(originalBitmap.height - top)

                if (width > 0 && height > 0) {
                    Bitmap.createBitmap(originalBitmap, left, top, width, height)
                } else null
            } catch (e: Exception) {
                null
            }
        }
    }

}
