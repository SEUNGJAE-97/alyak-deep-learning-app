package com.alyak.detector.feature.notification.data.model

import com.google.gson.annotations.SerializedName

/**
 * 서버 [ScheduleBackupRequest]와 동일. Gson은 LocalTime/LocalDate 대신 ISO 문자열로 전송.
 */
data class ScheduleBackupRequest(
    @SerializedName("pillId") val pillId: Long? = null,
    @SerializedName("pillName") val pillName: String,
    @SerializedName("dosage") val dosage: Int = 1,
    /** "HH:mm:ss" */
    @SerializedName("scheduledTime") val scheduledTime: String,
    /** "yyyy-MM-dd" */
    @SerializedName("startDate") val startDate: String,
    /** "yyyy-MM-dd" */
    @SerializedName("endDate") val endDate: String,
)
