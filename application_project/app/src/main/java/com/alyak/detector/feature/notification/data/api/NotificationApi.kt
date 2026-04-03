package com.alyak.detector.feature.notification.data.api

import com.alyak.detector.feature.notification.data.model.DeleteDeviceTokenRequest
import com.alyak.detector.feature.notification.data.model.UpsertDeviceTokenRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.PUT

interface NotificationApi {

    @PUT("/api/notifications/device-token")
    suspend fun upsertDeviceToken(
        @Body request: UpsertDeviceTokenRequest,
    ): Response<Unit>

    @DELETE("/api/notifications/device-token")
    suspend fun disableDeviceToken(
        @Body request: DeleteDeviceTokenRequest,
    ): Response<Unit>
}
