package com.alyak.detector.feature.notification.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * 서버 `medication_schedule_backup`과 동일한 컬럼 의미를 갖는 로컬 캐시.
 */
@Entity(tableName = "medication_schedule_backup")
data class ScheduleBackupEntity(
    @PrimaryKey val scheduleId: Long,
    val pillId: Long?,
    val pillName: String,
    val dosage: Int,
    val scheduledHour: Int,
    val scheduledMinute: Int,
    val startDateEpochDay: Long,
    val endDateEpochDay: Long,
    val createdAtEpochMillis: Long,
)
