package com.alyak.detector.feature.notification.alarm

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.alyak.detector.MainActivity
import com.alyak.detector.R
import com.alyak.detector.feature.notification.data.local.entity.ScheduleBackupEntity

/**
 * 백그라운드에서 표시하는 로컬 복약 알림(FCM 아님). 채널 importance HIGH로 헤드업에 가깝게 노출.
 */
object MedicationReminderNotificationHelper {

    const val CHANNEL_ID = "medication_reminder_channel"

    fun ensureChannel(context: Context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return
        val name = "복약 알림"
        val description = "등록한 복약 일정 시간에 알려줍니다."
        val channel = NotificationChannel(
            CHANNEL_ID,
            name,
            NotificationManager.IMPORTANCE_HIGH,
        ).apply {
            this.description = description
            enableVibration(true)
        }
        val nm = context.getSystemService(NotificationManager::class.java)
        nm.createNotificationChannel(channel)
    }

    fun showMedicationReminder(
        context: Context,
        entity: ScheduleBackupEntity,
        notificationId: Int,
    ) {
        ensureChannel(context)

        val intent = Intent(context, MainActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
            putExtra("open_medication_reminder", true)
            putExtra("schedule_id", entity.scheduleId)
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            notificationId,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )

        val title = "복약 시간"
        val body = "${entity.pillName} ${entity.dosage}정을 복용해 주세요."

        val remoteViews = RemoteViews(context.packageName, R.layout.notification_custom).apply {
            setTextViewText(R.id.notif_title, title)
            setTextViewText(R.id.notif_body, body)
            setImageViewResource(R.id.notif_icon, R.drawable.notification_icon)
        }

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.notification_icon)
            .setStyle(NotificationCompat.DecoratedCustomViewStyle())
            .setCustomContentView(remoteViews)
            .setCustomBigContentView(remoteViews)
            .setColor(ContextCompat.getColor(context, R.color.primaryBlue))
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .build()

        NotificationManagerCompat.from(context).notify(notificationId, notification)
    }
}
