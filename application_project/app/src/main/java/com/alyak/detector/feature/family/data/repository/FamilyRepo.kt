package com.alyak.detector.feature.family.data.repository

import com.alyak.detector.core.network.ApiResult
import com.alyak.detector.core.network.safeCall
import com.alyak.detector.feature.family.data.api.FamilyService
import com.alyak.detector.feature.family.data.model.FamilyMember
import com.alyak.detector.feature.family.data.model.MedicineSchedule

class FamilyRepo(private val familyService: FamilyService) {
    suspend fun fetchMembers(): ApiResult<List<FamilyMember>> =
        safeCall { familyService.getFamilyMembers() }

    suspend fun fetchSchedule(): ApiResult<List<MedicineSchedule>> =
        safeCall { familyService.getSchedule() }
}

