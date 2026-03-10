package com.alyak.detector.feature.camera.data.repository

import android.graphics.Bitmap
import com.alyak.detector.feature.camera.data.api.PillOCRApi
import com.alyak.detector.feature.pill.data.model.MedicineInfoDto
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.ByteArrayOutputStream
import javax.inject.Inject

class CameraRepoImpl @Inject constructor(
    private val api: PillOCRApi
) : CameraRepo {

    override suspend fun sendImage(bitmap: Bitmap): MedicineInfoDto {
        val bos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 90, bos)
        val imageBytes = bos.toByteArray()

        val imageRequestBody = imageBytes.toRequestBody("image/jpeg".toMediaType())
        val imagePart = MultipartBody.Part.createFormData(
            name = "images",
            filename = "pill.jpg",
            body = imageRequestBody
        )

        val response = api.uploadPillImages(
            images = listOf(imagePart)
        )

        if (response.isSuccessful) {
            val body = response.body()
            if (body != null) {
                return body
            } else {
                throw IllegalStateException("API 응답 본문이 없습니다.")
            }
        } else {
            throw IllegalStateException(
                "API 호출 실패: ${response.code()} - ${response.message()}"
            )
        }
    }
}
