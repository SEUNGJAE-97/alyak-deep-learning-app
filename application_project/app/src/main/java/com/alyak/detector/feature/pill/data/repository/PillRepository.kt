package com.alyak.detector.feature.pill.data.repository

import com.alyak.detector.feature.pill.data.api.PillApi
import com.alyak.detector.feature.pill.data.model.Pill
import jakarta.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class PillRepository @Inject constructor(
    private val api: PillApi
) {
    fun fetchRecentPills(): Flow<List<Pill>> = flow {
        val pills = api.getRecentSearchPills()
        emit(pills)
    }
}