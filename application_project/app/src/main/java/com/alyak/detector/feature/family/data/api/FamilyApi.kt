package com.alyak.detector.feature.family.data.api

import retrofit2.Response
import retrofit2.http.POST
import retrofit2.http.Query

interface FamilyApi {

    @POST("/api/family/invite")
    suspend fun inviteByEmail(
        @Query("email") email: String,
    ): Response<Boolean>
}
