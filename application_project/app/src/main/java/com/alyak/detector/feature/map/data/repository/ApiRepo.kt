package com.alyak.detector.feature.map.data.repository

import com.alyak.detector.feature.map.data.model.LocationDto
import com.alyak.detector.feature.map.data.model.PathDto

interface ApiRepo {
    suspend fun pathFind(start: LocationDto, end: LocationDto): PathDto
}