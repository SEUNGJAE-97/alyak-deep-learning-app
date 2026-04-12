package com.alyak.detector.feature.notification.data.api

import com.alyak.detector.feature.notification.data.model.MedicationLogRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface MedicationLogApi {

    @POST("api/medication/log")
    suspend fun postMedicationLog(@Body body: MedicationLogRequest): Response<Unit>
}
