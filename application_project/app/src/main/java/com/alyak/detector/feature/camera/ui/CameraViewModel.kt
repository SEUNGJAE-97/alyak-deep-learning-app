package com.alyak.detector.feature.camera.ui

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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

    private val _sendResult = MutableStateFlow<MedicineInfoDto?>(null)
    val sendResult = _sendResult.asStateFlow()

    private val _sendError = MutableStateFlow<String?>(null)
    val sendError = _sendError.asStateFlow()

    fun setCameraPermission(granted: Boolean) {
        cameraPermission.value = granted
    }

    fun setCapturedImage(bitmap: Bitmap) {
        _capturedBitmap.value = bitmap
    }

    fun sendImage() {
        val bitmap = _capturedBitmap.value ?: return

        viewModelScope.launch {
            _isSending.value = true
            _sendError.value = null
            try {
                val result = cameraRepo.sendImage(bitmap)
                _sendResult.value = result
            } catch (e: Exception) {
                _sendError.value = e.message
            } finally {
                _isSending.value = false
            }
        }
    }
}
