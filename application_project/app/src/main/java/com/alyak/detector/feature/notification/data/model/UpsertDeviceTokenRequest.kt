package com.alyak.detector.feature.notification.data.model

import com.google.gson.annotations.SerializedName

data class UpsertDeviceTokenRequest(
    @SerializedName("deviceId")
    val deviceId: String,
    @SerializedName("fcmToken")
    val fcmToken: String,
    @SerializedName("platform")
    val platform: DevicePlatform,
    @SerializedName("appVersion")
    val appVersion: String? = null,
)
