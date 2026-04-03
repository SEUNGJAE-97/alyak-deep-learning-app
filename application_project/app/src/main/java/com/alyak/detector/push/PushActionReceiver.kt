package com.alyak.detector.push

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationManagerCompat

/**
 * 알림의 [수락] / [거절] 액션 탭을 처리합니다.
 *
 * 길게 눌러서만 버튼을 띄우는 API는 없고, 알림을 펼치면 보이는 표준 액션 버튼으로 동작합니다.
 */
class PushActionReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val notificationId = intent.getIntExtra(EXTRA_NOTIFICATION_ID, -1)
        if (notificationId != -1) {
            NotificationManagerCompat.from(context).cancel(notificationId)
        }

        when (intent.action) {
            ACTION_FAMILY_INVITE_ACCEPT -> {
                // TODO: 가족 초대 수락 API 연동
                // intent.getStringExtra(EXTRA_INVITER_USER_ID), getStringExtra(EXTRA_INVITER_NAME)
            }

            ACTION_FAMILY_INVITE_REJECT -> {
                // TODO: 가족 초대 거절 API 연동 또는 무시
            }
        }
    }

    companion object {
        const val ACTION_FAMILY_INVITE_ACCEPT = "com.alyak.detector.push.ACTION_FAMILY_INVITE_ACCEPT"
        const val ACTION_FAMILY_INVITE_REJECT = "com.alyak.detector.push.ACTION_FAMILY_INVITE_REJECT"

        const val EXTRA_NOTIFICATION_ID = "extra_notification_id"
        const val EXTRA_INVITER_USER_ID = "extra_inviter_user_id"
        const val EXTRA_INVITER_NAME = "extra_inviter_name"
    }
}
