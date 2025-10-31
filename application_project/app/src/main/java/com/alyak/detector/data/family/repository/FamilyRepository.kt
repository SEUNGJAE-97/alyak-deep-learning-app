package com.alyak.detector.data.family.repository

import com.alyak.detector.data.family.api.FamilyService
import com.alyak.detector.data.family.model.FamilyMember

class FamilyRepository(private val familyService : FamilyService){
    suspend fun fetchMembers() : List<FamilyMember> {
        return familyService.getFamilyMembers()
    }
}