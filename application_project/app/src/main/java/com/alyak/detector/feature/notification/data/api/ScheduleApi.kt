package com.alyak.detector.feature.notification.data.api

import com.alyak.detector.feature.notification.data.model.ScheduleBackupRequest
import com.alyak.detector.feature.notification.data.model.ScheduleBackupResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface ScheduleApi {

    @POST("api/schedule/backup")
    suspend fun postScheduleBackups(
        @Body body: List<ScheduleBackupRequest>,
    ): Response<List<ScheduleBackupResponse>>
}
