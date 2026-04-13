package com.alyak.detector.feature.notification.alarm

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.alyak.detector.feature.notification.data.local.entity.ScheduleBackupEntity
import dagger.hilt.android.qualifiers.ApplicationContext
import java.time.LocalDate
import java.time.ZoneId
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 서버에서 받은 일정과 동일한 로컬 Room 데이터를 기준으로 다음 복약 시각에 [MedicationAlarmReceiver]를 알립니다.
 */
@Singleton
class MedicationAlarmScheduler @Inject constructor(
    @ApplicationContext private val context: Context,
) {

    private val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    fun cancelAlarmForSchedule(scheduleId: Long) {
        val intent = Intent(context, MedicationAlarmReceiver::class.java).apply {
            action = MedicationAlarmReceiver.ACTION_MEDICATION_ALARM
        }
        val pi = PendingIntent.getBroadcast(
            context,
            requestCode(scheduleId),
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT,
        )
        alarmManager.cancel(pi)
        pi.cancel()
    }

    /**
     * 단일 일정에 대해 [다음] 복약 시각 알람만 등록합니다.
     */
    fun scheduleNextAlarm(entity: ScheduleBackupEntity) {
        val triggerMillis = nextTriggerMillis(entity) ?: run {
            cancelAlarmForSchedule(entity.scheduleId)
            return
        }

        val intent = Intent(context, MedicationAlarmReceiver::class.java).apply {
            action = MedicationAlarmReceiver.ACTION_MEDICATION_ALARM
            putExtra(MedicationAlarmReceiver.EXTRA_SCHEDULE_ID, entity.scheduleId)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            requestCode(entity.scheduleId),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )

        try {
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                triggerMillis,
                pendingIntent,
            )
        } catch (_: SecurityException) {
            try {
                alarmManager.setExact(
                    AlarmManager.RTC_WAKEUP,
                    triggerMillis,
                    pendingIntent,
                )
            } catch (_: SecurityException) {
            }
        }
    }

    /**
     * Room에 있는 모든 일정에 대해 다음 알람을 다시 잡습니다(저장 직후 호출).
     */
    suspend fun rescheduleAllFromLocal(entities: List<ScheduleBackupEntity>) {
        for (e in entities) {
            cancelAlarmForSchedule(e.scheduleId)
        }
        for (e in entities) {
            scheduleNextAlarm(e)
        }
    }

    companion object {
        fun requestCode(scheduleId: Long): Int =
            (scheduleId xor (scheduleId shr 32)).toInt()
    }
}

/**
 * [startDate, endDate] 구간 안에서 가장 가까운 미래 트리거 시각(ms). 없으면 null.
 */
private fun nextTriggerMillis(entity: ScheduleBackupEntity): Long? {
    val zone = ZoneId.systemDefault()
    val start = LocalDate.ofEpochDay(entity.startDateEpochDay)
    val end = LocalDate.ofEpochDay(entity.endDateEpochDay)
    var date = LocalDate.now(zone)
    if (date.isBefore(start)) {
        date = start
    }
    while (!date.isAfter(end)) {
        val dt = date.atTime(entity.scheduledHour, entity.scheduledMinute)
        val millis = dt.atZone(zone).toInstant().toEpochMilli()
        if (millis > System.currentTimeMillis()) {
            return millis
        }
        date = date.plusDays(1)
    }
    return null
}
