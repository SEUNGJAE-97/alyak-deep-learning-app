package com.alyak.detector.data.family.repository

import com.alyak.detector.core.network.ApiResult
import com.alyak.detector.core.network.safeCall
import com.alyak.detector.data.family.api.FamilyService
import com.alyak.detector.data.family.model.FamilyMember


class FamilyRepo(private val familyService: FamilyService) {
    suspend fun fetchMembers(): ApiResult<List<FamilyMember>> =
        safeCall { familyService.getFamilyMembers() }
}