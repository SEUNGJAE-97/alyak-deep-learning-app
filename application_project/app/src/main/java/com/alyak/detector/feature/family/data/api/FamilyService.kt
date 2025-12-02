package com.alyak.detector.feature.family.data.api

import com.alyak.detector.feature.family.data.model.FamilyMember
import com.alyak.detector.feature.family.data.model.MedicineSchedule
import retrofit2.http.GET

interface FamilyService {
    @GET("/api/family/members")
    suspend fun getFamilyMembers(): List<FamilyMember>

    @GET("/api/schedule/search")
    suspend fun getSchedule(): List<MedicineSchedule>
}

