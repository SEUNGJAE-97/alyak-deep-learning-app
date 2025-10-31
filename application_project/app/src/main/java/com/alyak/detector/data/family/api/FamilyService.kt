package com.alyak.detector.data.family.api

import com.alyak.detector.ui.main.FamilyMember
import retrofit2.http.GET

interface FamilyService {
    @GET
    suspend fun getFamilyMembers() : List<FamilyMember>
}