package com.alyak.detector.feature.notification.data.api

import com.alyak.detector.feature.notification.data.model.ScheduleBackupRequest
import com.alyak.detector.feature.notification.data.model.ScheduleBackupResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface ScheduleApi {

    @POST("api/schedule/backup")
    suspend fun postScheduleBackups(
        @Body body: List<ScheduleBackupRequest>,
    ): Response<List<ScheduleBackupResponse>>

    /** 재설치 후 서버에 남아 있는 백업 일정을 그대로 가져옵니다. */
    @GET("api/schedule/restore")
    suspend fun getRestoreSchedules(): Response<List<ScheduleBackupResponse>>
}
