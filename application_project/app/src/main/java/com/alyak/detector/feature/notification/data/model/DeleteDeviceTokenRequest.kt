package com.alyak.detector.feature.notification.data.model

import com.google.gson.annotations.SerializedName

data class DeleteDeviceTokenRequest(
    @SerializedName("deviceId")
    val deviceId: String,
)
