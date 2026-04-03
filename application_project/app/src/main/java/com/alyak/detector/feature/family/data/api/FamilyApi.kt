package com.alyak.detector.feature.family.data.api

import com.alyak.detector.feature.family.data.model.AcceptFamilyInviteRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Query

interface FamilyApi {

    @POST("/api/family/invite")
    suspend fun inviteByEmail(
        @Query("email") email: String,
    ): Response<Boolean>

    @POST("/api/family/invite/accept")
    suspend fun acceptFamilyInvite(
        @Body body: AcceptFamilyInviteRequest,
    ): Response<Unit>
}
