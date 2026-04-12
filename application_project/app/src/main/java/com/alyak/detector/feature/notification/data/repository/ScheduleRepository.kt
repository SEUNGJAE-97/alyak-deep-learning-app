package com.alyak.detector.feature.notification.data.repository

import com.alyak.detector.core.network.ApiResult
import com.alyak.detector.core.network.safeCall
import com.alyak.detector.feature.notification.data.api.ScheduleApi
import com.alyak.detector.feature.notification.data.model.ScheduleBackupRequest
import com.alyak.detector.feature.notification.data.model.ScheduleBackupResponse
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ScheduleRepository @Inject constructor(
    private val scheduleApi: ScheduleApi,
) {

    suspend fun backupSchedules(requests: List<ScheduleBackupRequest>): ApiResult<List<ScheduleBackupResponse>> =
        safeCall { scheduleApi.postScheduleBackups(requests) }
}
