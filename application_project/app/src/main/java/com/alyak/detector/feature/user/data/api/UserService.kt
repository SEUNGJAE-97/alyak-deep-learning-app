package com.alyak.detector.feature.user.data.api

import retrofit2.Response
import retrofit2.http.DELETE
import retrofit2.http.POST

interface UserService {
    @POST("/api/auth/logout")
    suspend fun logoutUser(): Response<Unit>

    @DELETE("/api/users")
    suspend fun deleteUser(): Response<Unit>
}