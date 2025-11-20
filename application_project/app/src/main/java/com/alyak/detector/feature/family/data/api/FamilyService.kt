package com.alyak.detector.feature.family.data.api

import com.alyak.detector.feature.family.data.model.FamilyMember
import retrofit2.http.GET

interface FamilyService {
    @GET
    suspend fun getFamilyMembers(): List<FamilyMember>
}

