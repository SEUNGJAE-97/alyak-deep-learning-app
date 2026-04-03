package com.alyak.detector.push

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.core.app.NotificationManagerCompat
import com.alyak.detector.feature.family.data.api.FamilyApi
import com.alyak.detector.feature.family.data.model.AcceptFamilyInviteRequest
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

/**
 * 알림의 [수락] / [거절] 액션 탭을 처리합니다.
 *
 * 수락 시 [FamilyApi.acceptFamilyInvite]를 호출합니다(Authorization은 Retrofit 인터셉터로 붙음).
 */
@AndroidEntryPoint
class PushActionReceiver : BroadcastReceiver() {

    @Inject
    lateinit var familyApi: FamilyApi

    override fun onReceive(context: Context, intent: Intent) {
        val pendingResult = goAsync()
        val appContext = context.applicationContext
        val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

        val notificationId = intent.getIntExtra(EXTRA_NOTIFICATION_ID, -1)
        if (notificationId != -1) {
            NotificationManagerCompat.from(appContext).cancel(notificationId)
        }

        when (intent.action) {
            ACTION_FAMILY_INVITE_ACCEPT -> {
                val inviterUserId = intent.getStringExtra(EXTRA_INVITER_USER_ID)?.toLongOrNull()
                if (inviterUserId == null) {
                    scope.launch {
                        try {
                            withContext(Dispatchers.Main) {
                                Toast.makeText(
                                    appContext,
                                    "초대 정보가 올바르지 않습니다.",
                                    Toast.LENGTH_SHORT,
                                ).show()
                            }
                        } finally {
                            pendingResult.finish()
                        }
                    }
                    return
                }

                scope.launch {
                    try {
                        val response = familyApi.acceptFamilyInvite(
                            AcceptFamilyInviteRequest(inviterUserId),
                        )
                        withContext(Dispatchers.Main) {
                            if (response.isSuccessful) {
                                Toast.makeText(
                                    appContext,
                                    "가족 초대를 수락했습니다.",
                                    Toast.LENGTH_SHORT,
                                ).show()
                            } else {
                                Toast.makeText(
                                    appContext,
                                    "수락에 실패했습니다. (${response.code()})",
                                    Toast.LENGTH_LONG,
                                ).show()
                            }
                        }
                    } catch (_: Exception) {
                        withContext(Dispatchers.Main) {
                            Toast.makeText(
                                appContext,
                                "네트워크 오류가 발생했습니다.",
                                Toast.LENGTH_SHORT,
                            ).show()
                        }
                    } finally {
                        pendingResult.finish()
                    }
                }
            }

            ACTION_FAMILY_INVITE_REJECT -> {
                scope.launch {
                    try {
                        withContext(Dispatchers.Main) {
                            Toast.makeText(
                                appContext,
                                "초대를 거절했습니다.",
                                Toast.LENGTH_SHORT,
                            ).show()
                        }
                    } finally {
                        pendingResult.finish()
                    }
                }
            }

            else -> pendingResult.finish()
        }
    }

    companion object {
        const val ACTION_FAMILY_INVITE_ACCEPT =
            "com.alyak.detector.push.ACTION_FAMILY_INVITE_ACCEPT"
        const val ACTION_FAMILY_INVITE_REJECT =
            "com.alyak.detector.push.ACTION_FAMILY_INVITE_REJECT"

        const val EXTRA_NOTIFICATION_ID = "extra_notification_id"
        const val EXTRA_INVITER_USER_ID = "extra_inviter_user_id"
        const val EXTRA_INVITER_NAME = "extra_inviter_name"
    }
}
