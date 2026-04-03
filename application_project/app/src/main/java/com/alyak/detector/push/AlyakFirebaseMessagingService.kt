package com.alyak.detector.push

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ProcessLifecycleOwner
import com.alyak.detector.MainActivity
import com.alyak.detector.R
import com.alyak.detector.core.auth.TokenManager
import com.alyak.detector.feature.notification.InAppPushEvent
import com.alyak.detector.feature.notification.InAppPushNotifier
import com.alyak.detector.feature.notification.data.DeviceTokenRegistrar
import com.alyak.detector.push.dao.NotificationDao
import com.alyak.detector.push.dto.NotificationEntity
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Firebase Cloud Messaging(FCM) 수신 및 토큰 갱신을 처리하는 서비스입니다.
 *
 * - [onNewToken]: FCM 등록 토큰이 갱신되면 로그인 상태일 때 서버에 등록합니다.
 * - [onMessageReceived]: 푸시 메시지 수신 시 제목/본문을 바탕으로 로컬 알림을 표시합니다(포그라운드 대응).
 */
@AndroidEntryPoint
class AlyakFirebaseMessagingService : FirebaseMessagingService() {

    @Inject
    lateinit var notificationDao: NotificationDao

    @Inject
    lateinit var deviceTokenRegistrar: DeviceTokenRegistrar

    @Inject
    lateinit var tokenManager: TokenManager

    @Inject
    lateinit var inAppPushNotifier: InAppPushNotifier

    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    /**
     * 알림 표시 권한이 부여되었는지 확인합니다.
     *
     * Android 13(API 33) 이상에서는 [Manifest.permission.POST_NOTIFICATIONS] 런타임 권한을 검사하고,
     * 그 미만 버전에서는 항상 `true`를 반환합니다.
     */
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

    /**
     * FCM 등록 토큰이 갱신되었을 때 호출됩니다.
     *
     * 현재 로그인 상태(액세스 토큰 존재)인 경우에만 서버의 디바이스 토큰 등록 API로 토큰을 동기화합니다.
     *
     * @param token Firebase가 발급한 최신 등록 토큰
     */
    override fun onNewToken(token: String) {
        super.onNewToken(token)
        serviceScope.launch {
            if (!tokenManager.getAccessToken().isNullOrBlank()) {
                deviceTokenRegistrar.register(token)
            }
        }
    }

    /**
     * FCM 메시지를 수신했을 때 호출됩니다.
     *
     * notification payload와 data payload를 조합해 제목/본문을 만듭니다.
     * 포그라운드일 때는 [InAppPushNotifier]로 인앱 상단 배너만 띄우고,
     * 백그라운드일 때는 로컬 시스템 알림을 표시합니다.
     *
     * @param message 수신한 FCM 메시지
     */
    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)

        val data = message.data
        val title = message.notification?.title ?: data["title"] ?: "ALYAK 알림"
        val body = message.notification?.body ?: data["body"] ?: ""
        val type = data["type"]
        val notificationId = (System.currentTimeMillis() % Int.MAX_VALUE).toInt()

        serviceScope.launch {
            notificationDao.insertNotification(
                NotificationEntity(title = title, body = body, type = type)
            )
        }

        val isForeground = ProcessLifecycleOwner.get().lifecycle.currentState.isAtLeast(
            Lifecycle.State.STARTED
        )
        if (isForeground) {
            inAppPushNotifier.emit(
                InAppPushEvent(
                    title = title,
                    body = body,
                    notificationId = notificationId,
                    type = type,
                    inviterUserId = data["inviterUserId"],
                    inviterName = data["inviterName"],
                )
            )
            return
        }

        if (!notificationPermissionGranted(this)) return

        val intent = Intent(this, MainActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
            putExtra("fcm_type", type)
            putExtra("inviter_name", data["inviterName"])
        }

        val pendingIntent = PendingIntent.getActivity(
            this,
            notificationId,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val isFamilyInvite = data["type"] == "FAMILY_INVITE"

        val acceptPendingIntent: PendingIntent? = if (isFamilyInvite) {
            val acceptIntent = Intent(this, PushActionReceiver::class.java).apply {
                action = PushActionReceiver.ACTION_FAMILY_INVITE_ACCEPT
                putExtra(PushActionReceiver.EXTRA_NOTIFICATION_ID, notificationId)
                putExtra(PushActionReceiver.EXTRA_INVITER_USER_ID, data["inviterUserId"])
                putExtra(PushActionReceiver.EXTRA_INVITER_NAME, data["inviterName"])
            }
            PendingIntent.getBroadcast(
                this,
                notificationId + 1,
                acceptIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
        } else {
            null
        }

        val rejectPendingIntent: PendingIntent? = if (isFamilyInvite) {
            val rejectIntent = Intent(this, PushActionReceiver::class.java).apply {
                action = PushActionReceiver.ACTION_FAMILY_INVITE_REJECT
                putExtra(PushActionReceiver.EXTRA_NOTIFICATION_ID, notificationId)
                putExtra(PushActionReceiver.EXTRA_INVITER_USER_ID, data["inviterUserId"])
                putExtra(PushActionReceiver.EXTRA_INVITER_NAME, data["inviterName"])
            }
            PendingIntent.getBroadcast(
                this,
                notificationId + 2,
                rejectIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
        } else {
            null
        }

        val remoteViews = RemoteViews(packageName, R.layout.notification_custom).apply {
            setTextViewText(R.id.notif_title, title)
            setTextViewText(R.id.notif_body, body)
            setImageViewResource(R.id.notif_icon, R.drawable.notification_icon)
        }

        val notificationBuilder = NotificationCompat.Builder(this, FCM_INVITE_CHANNEL_ID)
            .setSmallIcon(R.drawable.notification_icon)
            .setStyle(NotificationCompat.DecoratedCustomViewStyle())
            .setCustomContentView(remoteViews)
            .setColor(ContextCompat.getColor(this, R.color.primaryBlue))
            .setAutoCancel(true)
            .setCustomBigContentView(remoteViews)
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setDefaults(NotificationCompat.DEFAULT_ALL)

        if (acceptPendingIntent != null) {
            notificationBuilder.addAction(
                0,
                getString(R.string.notification_action_accept),
                acceptPendingIntent
            )
        }
        if (rejectPendingIntent != null) {
            notificationBuilder.addAction(
                0,
                getString(R.string.notification_action_reject),
                rejectPendingIntent
            )
        }

        NotificationManagerCompat.from(this)
            .notify(notificationId, notificationBuilder.build())
    }

    companion object {
        const val FCM_INVITE_CHANNEL_ID = "fcm_invite_channel"

        /**
         * Android 8.0(API 26) 이상에서 알림 채널을 생성합니다.
         *
         * 동일한 채널 ID가 이미 존재하는 경우 시스템이 기존 채널을 재사용합니다.
         *
         * @param channelId 생성(또는 재사용)할 채널 ID
         */
        fun createNotificationChannel(context: Context) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val channelName = "가족 초대 및 알림"
                val descriptionText = "가족 초대 요청 및 앱 내 중요 알림을 수신합니다."
                val importance = NotificationManager.IMPORTANCE_HIGH

                val channel =
                    NotificationChannel(FCM_INVITE_CHANNEL_ID, channelName, importance).apply {
                        description = descriptionText
                    }

                val notificationManager =
                    context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                notificationManager.createNotificationChannel(channel)
            }
        }
    }
}
