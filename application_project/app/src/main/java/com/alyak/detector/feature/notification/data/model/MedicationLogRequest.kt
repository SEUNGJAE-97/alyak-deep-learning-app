package com.alyak.detector.feature.notification.data.model

import com.google.gson.annotations.SerializedName

/**
 * [POST /api/medication/log] — 서버는 [takenTime] null이면 SKIPPED, 이후 [scheduledTime]+30분과 비교해 TAKEN/DELAYED를 판정합니다.
 */
data class MedicationLogRequest(
    @SerializedName("pillName") val pillName: String,
    @SerializedName("dosage") val dosage: Int,
    @SerializedName("scheduledTime") val scheduledTime: String,
    @SerializedName("takenTime") val takenTime: String?,
)
