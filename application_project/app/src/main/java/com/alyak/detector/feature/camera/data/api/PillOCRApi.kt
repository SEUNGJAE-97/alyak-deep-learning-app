package com.alyak.detector.feature.camera.data.api

import com.alyak.detector.feature.pill.data.model.MedicineInfoDto
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface PillOCRApi {
    @Multipart
    @POST("api/pill/recognize")
    suspend fun uploadPillImages(
        @Part images: List<MultipartBody.Part>,
        @Part("boxes") boxes: RequestBody
    ): Response<List<MedicineInfoDto>>
}
