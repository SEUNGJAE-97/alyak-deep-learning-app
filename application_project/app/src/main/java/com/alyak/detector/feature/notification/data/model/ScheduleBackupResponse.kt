package com.alyak.detector.feature.notification.data.model

import com.google.gson.annotations.SerializedName

data class ScheduleBackupResponse(
    @SerializedName("scheduleId") val scheduleId: Long?,
    @SerializedName("pillId") val pillId: Long?,
    @SerializedName("pillName") val pillName: String?,
    @SerializedName("dosage") val dosage: Int?,
    @SerializedName("scheduledTime") val scheduledTime: String?,
    @SerializedName("startDate") val startDate: String?,
    @SerializedName("endDate") val endDate: String?,
    @SerializedName("createdAt") val createdAt: String?,
)
