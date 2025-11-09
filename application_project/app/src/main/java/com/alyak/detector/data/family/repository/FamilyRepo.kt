package com.alyak.detector.data.family.repository

import com.alyak.detector.data.family.api.FamilyService
import com.alyak.detector.data.family.model.FamilyMember
import com.alyak.detector.core.network.ApiResult
import com.alyak.detector.core.network.safeCall

class FamilyRepo(private val familyService : FamilyService){
    suspend fun fetchMembers() : ApiResult<List<FamilyMember>> =
        safeCall { familyService.getFamilyMembers() }
}