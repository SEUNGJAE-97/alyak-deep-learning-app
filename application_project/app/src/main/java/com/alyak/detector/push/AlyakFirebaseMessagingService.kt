package com.alyak.detector.push

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import com.alyak.detector.core.auth.TokenManager
import com.alyak.detector.MainActivity
import com.alyak.detector.feature.notification.data.DeviceTokenRegistrar
import com.alyak.detector.feature.family.ui.main.MainScreen
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import dagger.hilt.android.AndroidEntryPoint
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class AlyakFirebaseMessagingService : FirebaseMessagingService() {

    @Inject
    lateinit var deviceTokenRegistrar: DeviceTokenRegistrar

    @Inject
    lateinit var tokenManager: TokenManager

    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    private fun notificationPermissionGranted(context: Context): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            true
        }
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        serviceScope.launch {
            if (!tokenManager.getAccessToken().isNullOrBlank()) {
                deviceTokenRegistrar.register(token)
            }
        }
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)

        val data = message.data
        val type = data["type"]
        val inviterName = data["inviterName"]
        val inviterUserId = data["inviterUserId"]
        val title = message.notification?.title ?: data["title"] ?: "ALYAK 알림"
        val body = message.notification?.body ?: data["body"] ?: ""

        Log.i(
            TAG,
            "onMessageReceived: type=$type inviterUserId=$inviterUserId inviterName=$inviterName title=$title body=$body dataKeys=${data.keys}"
        )

        // 포그라운드에서도 보이도록 로컬 알림을 추가로 표시한다.
        // (서버가 setNotification(...)을 사용하므로, 앱 포그라운드에서는 시스템 알림이 안 보일 수 있음)
        if (!notificationPermissionGranted(this)) {
            Log.w(TAG, "POST_NOTIFICATIONS permission not granted. Skip local notification.")
            return
        }

        val channelId = FCM_INVITE_CHANNEL_ID
        createChannelIfNeeded(channelId)

        val intent = Intent(this, MainActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
            // 추후 data type에 따라 화면 분기 가능
            putExtra("fcm_type", type)
            putExtra("inviter_name", inviterName)
        }

        val pendingIntent = PendingIntent.getActivity(
            this,
            (System.currentTimeMillis() % Int.MAX_VALUE).toInt(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notificationId = (System.currentTimeMillis() % Int.MAX_VALUE).toInt()
        val notification = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(title)
            .setContentText(body)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .build()

        NotificationManagerCompat.from(this).notify(notificationId, notification)
    }

    private fun createChannelIfNeeded(channelId: String) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return
        val channelName = "FCM 알림"
        val channel = NotificationChannel(
            channelId,
            channelName,
            NotificationManager.IMPORTANCE_HIGH
        )
        val manager = getSystemService(NotificationManager::class.java)
        manager.createNotificationChannel(channel)
    }

    private companion object {
        const val TAG = "AlyakFCM"
        const val FCM_INVITE_CHANNEL_ID = "fcm_invite_channel"
    }
}
