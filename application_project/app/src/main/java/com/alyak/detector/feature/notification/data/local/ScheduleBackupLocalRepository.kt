package com.alyak.detector.feature.notification.data.local

import com.alyak.detector.feature.notification.data.local.dao.ScheduleBackupDao
import com.alyak.detector.feature.notification.data.local.entity.ScheduleBackupEntity
import com.alyak.detector.feature.notification.data.model.ScheduleBackupResponse
import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ScheduleBackupLocalRepository @Inject constructor(
    private val scheduleBackupDao: ScheduleBackupDao,
) {

    suspend fun insertOrReplaceFromServer(responses: List<ScheduleBackupResponse>) {
        val syncedAtMillis = System.currentTimeMillis()
        val entities = responses.mapNotNull { it.toEntityOrNull(minCreatedAtEpochMillis = syncedAtMillis) }
        if (entities.isNotEmpty()) {
            scheduleBackupDao.insertAll(entities)
        }
    }

    /**
     * 서버 스냅샷과 로컬을 일치시킵니다(재설치 복구용).
     * 서버에 없는 로컬 행은 제거됩니다.
     */
    suspend fun replaceAllFromServerRestore(responses: List<ScheduleBackupResponse>) {
        val restoredAtMillis = System.currentTimeMillis()
        scheduleBackupDao.deleteAll()
        val entities = responses.mapNotNull { it.toEntityOrNull(minCreatedAtEpochMillis = restoredAtMillis) }
        if (entities.isNotEmpty()) {
            scheduleBackupDao.insertAll(entities)
        }
    }

    suspend fun getAllForAlarms(): List<ScheduleBackupEntity> = scheduleBackupDao.getAll()
}

private fun ScheduleBackupResponse.toEntityOrNull(minCreatedAtEpochMillis: Long): ScheduleBackupEntity? {
    val id = scheduleId ?: return null
    val name = pillName ?: return null
    val d = dosage ?: return null
    val (h, m) = scheduledTime.toHourMinuteOrNull() ?: return null
    val start = startDate.toEpochDayOrNull() ?: return null
    val end = endDate.toEpochDayOrNull() ?: return null
    val parsedCreated = createdAt.toEpochMillisOrNull() ?: minCreatedAtEpochMillis
    val created = maxOf(parsedCreated, minCreatedAtEpochMillis)
    return ScheduleBackupEntity(
        scheduleId = id,
        pillId = pillId,
        pillName = name,
        dosage = d,
        scheduledHour = h,
        scheduledMinute = m,
        startDateEpochDay = start,
        endDateEpochDay = end,
        createdAtEpochMillis = created,
    )
}

private fun String?.toHourMinuteOrNull(): Pair<Int, Int>? {
    if (this.isNullOrBlank()) return null
    val s = this.trim()
    return try {
        val t = LocalTime.parse(s, DateTimeFormatter.ISO_LOCAL_TIME)
        t.hour to t.minute
    } catch (_: Exception) {
        val parts = s.split(":")
        if (parts.size >= 2) {
            parts[0].toInt() to parts[1].toInt()
        } else {
            null
        }
    }
}

private fun String?.toEpochDayOrNull(): Long? {
    if (this.isNullOrBlank()) return null
    return try {
        LocalDate.parse(this, DateTimeFormatter.ISO_LOCAL_DATE).toEpochDay()
    } catch (_: Exception) {
        null
    }
}

private fun String?.toEpochMillisOrNull(): Long? {
    if (this.isNullOrBlank()) return null
    return try {
        Instant.parse(this).toEpochMilli()
    } catch (_: Exception) {
        try {
            val ldt = java.time.LocalDateTime.parse(this, DateTimeFormatter.ISO_LOCAL_DATE_TIME)
            ldt.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
        } catch (_: Exception) {
            null
        }
    }
}
