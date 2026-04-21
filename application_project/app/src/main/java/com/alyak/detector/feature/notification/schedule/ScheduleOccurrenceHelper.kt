package com.alyak.detector.feature.notification.schedule

import com.alyak.detector.feature.notification.data.local.entity.ScheduleBackupEntity
import java.time.LocalDate
import java.time.ZoneId

/** 복용 예정 시각으로부터 1시간(ms) */
private const val WINDOW_MS = 60L * 60L * 1000L

/** 정상 복용: 예정 시각 이후 30분 이내 */
private const val TAKEN_WINDOW_MS = 30L * 60L * 1000L

data class NextMedicationUi(
    val entity: ScheduleBackupEntity,
    /** 이번 회차 복용 예정 시각 (epoch ms) */
    val scheduledMillis: Long,
    val windowEndMillis: Long,
    val doseTimeLabel: String,
    val detail: String,
    /** 남은 분 (예정 전이면 예정까지, 창 안이면 창 종료까지) */
    val minutesLeft: Long,
    /** 체크 아이콘 강조: [scheduled, windowEnd] */
    val isCheckWindowActive: Boolean,
    /** 체크 탭 가능 (창 안에서만) */
    val canTapCheck: Boolean,
)

sealed class OccurrencePriority {
    data class Missed(val entity: ScheduleBackupEntity, val scheduledMillis: Long) : OccurrencePriority()
    data class Show(val ui: NextMedicationUi) : OccurrencePriority()
}

/**
 * Room에 있는 일정들 중, 화면에 보여줄 다음 상태를 계산합니다.
 * 우선순위: (1) 가장 이른 **미처리 미복용** (2) **창 안** (3) **다가오는 예정**
 *
 * 각 일정은 [ScheduleBackupEntity.createdAtEpochMillis] **이후**에 도래하는 회차만 대상으로 합니다.
 * (저장 이전 날짜의 복약 시각은 ‘이미 놓친 회차’로 자동 미복용 처리하지 않습니다.)
 */
object ScheduleOccurrenceHelper {

    /** 로컬 저장 시각 이후(포함)에 해당하는 회차만 카운트 */
    private fun isOccurrenceOnOrAfterSave(e: ScheduleBackupEntity, scheduledMillis: Long): Boolean =
        scheduledMillis >= e.createdAtEpochMillis

    fun resolve(entities: List<ScheduleBackupEntity>, nowMillis: Long): OccurrencePriority? {
        if (entities.isEmpty()) return null

        val missed = entities.mapNotNull { firstMissed(it, nowMillis) }
            .minByOrNull { it.scheduledMillis }
        if (missed != null) return OccurrencePriority.Missed(missed.entity, missed.scheduledMillis)

        val inWindow = entities.mapNotNull { firstInWindow(it, nowMillis) }
            .minByOrNull { it.scheduledMillis }
        if (inWindow != null) return OccurrencePriority.Show(inWindow)

        val upcoming = entities.mapNotNull { firstUpcoming(it, nowMillis) }
            .minByOrNull { it.scheduledMillis }
        if (upcoming != null) return OccurrencePriority.Show(upcoming)

        return null
    }

    /**
     * 서버 [MedicationLogService.resolveStatus]와 동일한 구분(참고용).
     * 실제 기록은 [takenTime]을 보내면 서버가 TAKEN/DELAYED를 판정합니다.
     */
    fun classifyTakenStatus(nowMillis: Long, scheduledMillis: Long): TakenKind {
        val delta = nowMillis - scheduledMillis
        return when {
            delta < 0 -> TakenKind.SKIPPED
            delta < TAKEN_WINDOW_MS -> TakenKind.TAKEN
            delta < WINDOW_MS -> TakenKind.DELAYED
            else -> TakenKind.SKIPPED
        }
    }

    enum class TakenKind { TAKEN, DELAYED, SKIPPED }

    private data class MissedOcc(val entity: ScheduleBackupEntity, val scheduledMillis: Long)

    /** 시간순 첫 회차가 창(S+1h)을 넘겼으면 해당 회차를 미복용으로 간주 */
    private fun firstMissed(e: ScheduleBackupEntity, nowMillis: Long): MissedOcc? {
        val zone = ZoneId.systemDefault()
        var date = LocalDate.ofEpochDay(e.startDateEpochDay)
        val end = LocalDate.ofEpochDay(e.endDateEpochDay)
        while (!date.isAfter(end)) {
            val scheduledMs = date.atTime(e.scheduledHour, e.scheduledMinute)
                .atZone(zone).toInstant().toEpochMilli()
            if (!isOccurrenceOnOrAfterSave(e, scheduledMs)) {
                date = date.plusDays(1)
                continue
            }
            val windowEnd = scheduledMs + WINDOW_MS
            when {
                nowMillis < scheduledMs -> return null
                nowMillis <= windowEnd -> return null
                else -> return MissedOcc(e, scheduledMs)
            }
        }
        return null
    }

    private fun firstInWindow(e: ScheduleBackupEntity, nowMillis: Long): NextMedicationUi? {
        val zone = ZoneId.systemDefault()
        var date = LocalDate.ofEpochDay(e.startDateEpochDay)
        val end = LocalDate.ofEpochDay(e.endDateEpochDay)
        while (!date.isAfter(end)) {
            val scheduledMs = date.atTime(e.scheduledHour, e.scheduledMinute)
                .atZone(zone).toInstant().toEpochMilli()
            if (!isOccurrenceOnOrAfterSave(e, scheduledMs)) {
                date = date.plusDays(1)
                continue
            }
            val windowEnd = scheduledMs + WINDOW_MS
            if (nowMillis >= scheduledMs && nowMillis <= windowEnd) {
                return toUi(e, scheduledMs, windowEnd, nowMillis, inWindow = true)
            }
            date = date.plusDays(1)
        }
        return null
    }

    private fun firstUpcoming(e: ScheduleBackupEntity, nowMillis: Long): NextMedicationUi? {
        val zone = ZoneId.systemDefault()
        var date = LocalDate.ofEpochDay(e.startDateEpochDay)
        val end = LocalDate.ofEpochDay(e.endDateEpochDay)
        while (!date.isAfter(end)) {
            val scheduledMs = date.atTime(e.scheduledHour, e.scheduledMinute)
                .atZone(zone).toInstant().toEpochMilli()
            if (!isOccurrenceOnOrAfterSave(e, scheduledMs)) {
                date = date.plusDays(1)
                continue
            }
            if (nowMillis < scheduledMs) {
                val windowEnd = scheduledMs + WINDOW_MS
                return toUi(e, scheduledMs, windowEnd, nowMillis, inWindow = false)
            }
            date = date.plusDays(1)
        }
        return null
    }

    private fun toUi(
        e: ScheduleBackupEntity,
        scheduledMs: Long,
        windowEndMs: Long,
        nowMillis: Long,
        inWindow: Boolean,
    ): NextMedicationUi {
        val zone = ZoneId.systemDefault()
        val timeLabel = java.time.Instant.ofEpochMilli(scheduledMs)
            .atZone(zone)
            .toLocalTime()
            .let { t ->
                String.format("%02d:%02d", t.hour, t.minute)
            }
        val minutesLeft = if (inWindow) {
            (windowEndMs - nowMillis).coerceAtLeast(0) / 60_000
        } else {
            (scheduledMs - nowMillis).coerceAtLeast(0) / 60_000
        }
        return NextMedicationUi(
            entity = e,
            scheduledMillis = scheduledMs,
            windowEndMillis = windowEndMs,
            doseTimeLabel = timeLabel,
            detail = "${e.dosage}정 · ${e.pillName}",
            minutesLeft = minutesLeft,
            isCheckWindowActive = inWindow,
            canTapCheck = inWindow,
        )
    }
}
