package com.alyak.detector.feature.notification.data.repository

import com.alyak.detector.feature.notification.data.api.MedicationLogApi
import com.alyak.detector.feature.notification.data.model.MedicationLogRequest
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 서버 `medication_log` 테이블에 기록을 남깁니다 ([POST /api/medication/log]).
 *
 * 이후 단계에서 복용 확인 플로우·FCM 연동 시 이 레포지토리만 호출하도록 통일합니다.
 */
@Singleton
class MedicationLogRepository @Inject constructor(
    private val medicationLogApi: MedicationLogApi,
) {

    /**
     * @return HTTP 2xx 여부 (본문 없음)
     */
    suspend fun postLog(request: MedicationLogRequest): Result<Unit> {
        return try {
            val response = medicationLogApi.postMedicationLog(request)
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(
                    IllegalStateException("medication log failed: ${response.code()}"),
                )
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
