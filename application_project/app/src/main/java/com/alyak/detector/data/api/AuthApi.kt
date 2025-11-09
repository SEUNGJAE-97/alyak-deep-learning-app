package com.alyak.detector.data.api

import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Query

interface AuthApi {
    @POST("/api/auth/signup")
    suspend fun signUp(
        @Header("Authorization") apiKey: String,
        @Query("email") email : String,
        @Query("password") password : String,
        @Query("name") name: String,
        @Query("phoneNumber") phoneNumber: String
    )
}