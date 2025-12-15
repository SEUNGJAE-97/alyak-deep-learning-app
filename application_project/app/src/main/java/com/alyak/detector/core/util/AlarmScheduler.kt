package com.alyak.detector.core.util

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import dagger.hilt.android.qualifiers.ApplicationContext
import jakarta.inject.Inject
import java.util.Calendar

class AlarmScheduler @Inject constructor(
@ApplicationContext private val context: Context
){
    private val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    fun scheduleAlarm(delayMinutes: Int) {
        val intent = Intent(context, AlarmReceiver::class.java)

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val triggerTime = Calendar.getInstance().apply {
            add(Calendar.MINUTE, delayMinutes)
        }

        try {
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                triggerTime.timeInMillis,
                pendingIntent
            )
        } catch (e: SecurityException) {
            e.printStackTrace()
        }
    }
}