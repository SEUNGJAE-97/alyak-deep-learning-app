package com.alyak.detector.feature.camera.data.repository

import android.graphics.Bitmap
import com.alyak.detector.feature.pill.data.model.MedicineInfoDto

interface CameraRepo {
    suspend fun sendImage(bitmap: Bitmap): MedicineInfoDto
}
