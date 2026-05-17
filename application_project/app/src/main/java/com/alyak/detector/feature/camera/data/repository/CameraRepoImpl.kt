package com.alyak.detector.feature.camera.data.repository

import android.graphics.Bitmap
import com.alyak.detector.feature.camera.data.model.PillDetection
import com.alyak.detector.feature.camera.data.api.PillOCRApi
import com.alyak.detector.feature.pill.data.model.MedicineInfoDto
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import retrofit2.Response
import java.io.ByteArrayOutputStream
import javax.inject.Inject

class CameraRepoImpl @Inject constructor(
    private val api: PillOCRApi
) : CameraRepo {


    override suspend fun sendImage(
        originalImage: Bitmap,
        detectedImages: List<Bitmap>,
        detections: List<PillDetection>
    ): List<MedicineInfoDto> {
        val imageParts = mutableListOf<MultipartBody.Part>()
        // 1. 원본 이미지 추가
        val originalBytes = bitmapToBytes(originalImage)
        val originalPart = originalBytes.toRequestBody("image/jpeg".toMediaType())
        imageParts.add(MultipartBody.Part.createFormData(
            name = "images",
            filename = "original.jpg",
            body = originalPart
        ))
        // 2. 탐지된 알약 이미지들 추가
        for ((index, detectedImage) in detectedImages.withIndex()) {
            val detectedBytes = bitmapToBytes(detectedImage)
            val detectedPart = detectedBytes.toRequestBody("image/jpeg".toMediaType())
            imageParts.add(MultipartBody.Part.createFormData(
                name = "images",
                filename = "pill_${index + 1}.jpg",
                body = detectedPart
            ))
        }

        val boxesJson = buildBoxesJson(detections)
        val boxesBody = boxesJson.toRequestBody("application/json".toMediaType())

        // api 호출
        val response = api.uploadPillImages(images = imageParts, boxes = boxesBody)

        return handleResponse(response)
    }

    // Bitmap -> ByteArray 헬퍼 함수
    private fun bitmapToBytes(bitmap: Bitmap): ByteArray {
        return ByteArrayOutputStream().use { bos ->
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, bos)
            bos.toByteArray()
        }
    }

    private fun buildBoxesJson(detections: List<PillDetection>): String {
        val array = JSONArray()
        detections.forEachIndexed { index, detection ->
            val rect = detection.boundingBox
            val obj = JSONObject()
                .put("boxIndex", index)
                .put("xMin", rect.left)
                .put("yMin", rect.top)
                .put("xMax", rect.right)
                .put("yMax", rect.bottom)
            array.put(obj)
        }
        return array.toString()
    }

    // 응답 처리 공통화
    private fun handleResponse(response: Response<List<MedicineInfoDto>>): List<MedicineInfoDto> {
        if (response.isSuccessful) {
            return response.body() ?: emptyList()
        } else {
            throw IllegalStateException("API 호출 실패: ${response.code()} - ${response.message()}")
        }
    }
}
