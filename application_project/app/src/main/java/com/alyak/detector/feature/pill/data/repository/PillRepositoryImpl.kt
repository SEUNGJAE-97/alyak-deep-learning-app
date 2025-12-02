package com.alyak.detector.feature.pill.data.repository

import com.alyak.detector.feature.pill.data.api.PillApi
import com.alyak.detector.feature.pill.data.model.MedicineDetailDto
import com.alyak.detector.feature.pill.data.model.Pill
import com.alyak.detector.feature.pill.data.model.local.dao.RecentSearchDao
import com.alyak.detector.feature.pill.data.model.local.entity.RecentSearchEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class PillRepositoryImpl @Inject constructor(
    private val recentSearchDao: RecentSearchDao,
    private val api: PillApi
) : PillRepository {

    /**
     * 분할선, 외형, 색상으로 검색하는 결과를 가져온다.
     * */
    override suspend fun searchPills(shape: String, color: String, score: String): List<Pill> {
        return api.getPillSearchResult(shape, color, score)
    }

    /**
     * 알약명으로 검색한 결과를 모두 가져온다.
     * */
    override suspend fun findPills(pillName: String): List<Pill> {
        return api.getPillFindResult(pillName)
    }

    /**
     * pid로 약품의 상세정보를 가져온다.
     * */
    override suspend fun searchPillDetail(pid: Long): MedicineDetailDto {
        return api.getPillDetail(pid)
    }
    /**
     * Entity -> Pill 변환
     * */
    override fun fetchRecentPills(): Flow<List<Pill>> {
        return recentSearchDao.getRecentSearches().map { entities ->
            entities.map { entity ->
                Pill(
                    name = entity.pillName,
                    classification = entity.classification,
                    manufacturer = entity.manufacturer,
                    pillType = entity.type,
                    pid = entity.id.toString(),
                    pillImg = entity.img
                )
            }
        }
    }

    /**
     * Pill -> Entity 변환
     * */
    override suspend fun saveRecentSearch(pill: Pill) {
        val entity = RecentSearchEntity(
            id = pill.pid.toLongOrNull() ?: 0L,
            pillName = pill.name,
            manufacturer = pill.manufacturer,
            type = pill.pillType,
            classification = pill.classification,
            timestamp = System.currentTimeMillis(),
            img = pill.pillImg
        )
        recentSearchDao.insertSearch(entity)
    }
}