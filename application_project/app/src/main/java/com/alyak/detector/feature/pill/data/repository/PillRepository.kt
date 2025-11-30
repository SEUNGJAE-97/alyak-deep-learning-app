package com.alyak.detector.feature.pill.data.repository

import com.alyak.detector.feature.pill.data.model.local.entity.RecentSearchEntity
import com.alyak.detector.feature.pill.data.model.Pill
import com.alyak.detector.feature.pill.data.model.local.dao.RecentSearchDao
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class PillRepository @Inject constructor(
    private val recentSearchDao: RecentSearchDao
) {
    /**
     * Entity -> Pill 변환
     * */
    fun fetchRecentPills(): Flow<List<Pill>> {
        return recentSearchDao.getRecentSearches().map { entities ->
            entities.map { entity ->
                Pill(
                    name = entity.pillName,
                    ingredient = entity.ingredient,
                    manufacturer = entity.manufacturer,
                    category = entity.type,
                    pid = entity.id.toString()
                )
            }
        }
    }

    /**
     * Pill -> Entity 변환
     * */
    suspend fun saveRecentSearch(pill: Pill) {
        val entity = RecentSearchEntity(
            id = pill.pid.toLongOrNull() ?: 0L,
            pillName = pill.name,
            manufacturer = pill.manufacturer,
            type = pill.category,
            ingredient = pill.ingredient,
            timestamp = System.currentTimeMillis()
        )
        recentSearchDao.insertSearch(entity)
    }
}