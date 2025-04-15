package com.alyak.detector.ui.camera

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject

@HiltViewModel
class CameraViewModel @Inject constructor() : ViewModel() {

    val cameraPermission = MutableStateFlow(false)

    fun setCameraPermission(granted: Boolean) {
        cameraPermission.value = granted
    }


}

