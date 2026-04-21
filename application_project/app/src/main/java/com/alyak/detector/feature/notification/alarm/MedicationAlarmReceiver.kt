package com.alyak.detector.feature.notification.alarm

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ProcessLifecycleOwner
import com.alyak.detector.feature.notification.InAppPushEvent
import com.alyak.detector.feature.notification.InAppPushNotifier
import com.alyak.detector.feature.notification.data.local.dao.ScheduleBackupDao
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

/**
 * 복약 시각 [AlarmManager] 콜백. 포그라운드면 인앱 배너, 백그라운드면 로컬 알림(FCM 아님).
 */
@AndroidEntryPoint
class MedicationAlarmReceiver : BroadcastReceiver() {

    @Inject
    lateinit var scheduleBackupDao: ScheduleBackupDao

    @Inject
    lateinit var inAppPushNotifier: InAppPushNotifier

    @Inject
    lateinit var medicationAlarmScheduler: MedicationAlarmScheduler

    override fun onReceive(context: Context, intent: Intent?) {
        if (intent?.action != ACTION_MEDICATION_ALARM) return
        val scheduleId = intent.getLongExtra(EXTRA_SCHEDULE_ID, -1L)
        if (scheduleId < 0) return

        val pendingResult = goAsync()
        runBlocking(Dispatchers.IO) {
            try {
                val entity = scheduleBackupDao.getByScheduleId(scheduleId) ?: return@runBlocking
                val notificationId = (scheduleId % Int.MAX_VALUE).toInt().let { if (it < 0) -it else it }

                val isForeground = ProcessLifecycleOwner.get().lifecycle.currentState.isAtLeast(
                    Lifecycle.State.STARTED,
                )

                if (isForeground) {
                    inAppPushNotifier.emit(
                        InAppPushEvent(
                            title = "복약 시간",
                            body = "${entity.pillName} ${entity.dosage}정을 복용해 주세요.",
                            notificationId = notificationId,
                            type = "MEDICATION_REMINDER",
                            inviterUserId = null,
                            inviterName = null,
                        ),
                    )
                } else {
                    if (notificationPermissionGranted(context)) {
                        MedicationReminderNotificationHelper.showMedicationReminder(
                            context = context.applicationContext,
                            entity = entity,
                            notificationId = notificationId,
                        )
                    }
                }

                medicationAlarmScheduler.scheduleNextAlarm(entity)
            } finally {
                pendingResult.finish()
            }
        }
    }

    private fun notificationPermissionGranted(context: Context): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS,
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            true
        }
    }

    companion object {
        const val ACTION_MEDICATION_ALARM = "com.alyak.detector.ACTION_MEDICATION_ALARM"
        const val EXTRA_SCHEDULE_ID = "schedule_id"
    }
}
