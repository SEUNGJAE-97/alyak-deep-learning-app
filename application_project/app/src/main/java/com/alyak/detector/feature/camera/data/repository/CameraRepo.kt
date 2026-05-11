package com.alyak.detector.feature.camera.data.repository

import android.graphics.Bitmap
import com.alyak.detector.feature.camera.data.model.PillDetection
import com.alyak.detector.feature.pill.data.model.MedicineInfoDto

interface CameraRepo {
    /**
     * @param detectedImages : 검출된 이미지들
     * @param originalImage : 원본 이미지
     * @return List<MedicineInfoDto> : 인식된 알약 기본 정보 목록
     * */
    suspend fun sendImage(
        originalImage: Bitmap,
        detectedImages: List<Bitmap>,
        detections: List<PillDetection>
    ): List<MedicineInfoDto>
}
