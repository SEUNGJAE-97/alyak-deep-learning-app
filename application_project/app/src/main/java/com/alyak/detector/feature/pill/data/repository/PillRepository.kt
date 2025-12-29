package com.alyak.detector.feature.pill.data.repository

import com.alyak.detector.feature.pill.data.model.MedicineDetailDto
import com.alyak.detector.feature.pill.data.model.Pill
import kotlinx.coroutines.flow.Flow

interface PillRepository {
    fun fetchRecentPills(): Flow<List<Pill>>
    suspend fun saveRecentSearch(pill: Pill)
    suspend fun searchPills(shape: String, color: String, score: String): List<Pill>
    suspend fun findPills(pillName: String): List<Pill>
    suspend fun searchPillDetail(pid : Long): MedicineDetailDto
}