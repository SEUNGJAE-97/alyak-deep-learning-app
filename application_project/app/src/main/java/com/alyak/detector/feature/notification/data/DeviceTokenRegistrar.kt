package com.alyak.detector.feature.notification.data

import android.content.Context
import android.provider.Settings
import com.alyak.detector.feature.notification.data.api.NotificationApi
import com.alyak.detector.feature.notification.data.model.DeleteDeviceTokenRequest
import com.alyak.detector.feature.notification.data.model.DevicePlatform
import com.alyak.detector.feature.notification.data.model.UpsertDeviceTokenRequest
import android.util.Log
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

private const val TAG = "DeviceTokenRegistrar"

@Singleton
class DeviceTokenRegistrar @Inject constructor(
    private val notificationApi: NotificationApi,
    @ApplicationContext private val context: Context,
) {

    suspend fun register(fcmToken: String): Result<Unit> {
        return runCatching {
            val response = notificationApi.upsertDeviceToken(
                UpsertDeviceTokenRequest(
                    deviceId = resolveDeviceId(),
                    fcmToken = fcmToken,
                    platform = DevicePlatform.ANDROID,
                    appVersion = appVersionName(),
                )
            )
            if (!response.isSuccessful) {
                error("FCM 토큰 등록 실패: HTTP ${response.code()}")
            }
        }.onFailure { Log.w(TAG, "FCM 토큰 등록 실패", it) }
    }

    suspend fun unregister(): Result<Unit> {
        return runCatching {
            val response = notificationApi.disableDeviceToken(
                DeleteDeviceTokenRequest(deviceId = resolveDeviceId())
            )
            if (!response.isSuccessful) {
                error("FCM 토큰 해제 실패: HTTP ${response.code()}")
            }
        }.onFailure { Log.w(TAG, "FCM 토큰 해제 실패", it) }
    }

    private fun resolveDeviceId(): String {
        return Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID)
            ?: "unknown-device"
    }

    private fun appVersionName(): String? =
        try {
            context.packageManager.getPackageInfo(context.packageName, 0).versionName
        } catch (_: Exception) {
            null
        }
}
