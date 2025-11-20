package com.alyak.detector.feature.map.data.repository

import com.alyak.detector.feature.map.data.model.LocationDto

interface LocationRepo {
    suspend fun getCurrentLocation(): LocationDto
    suspend fun startLocationUpdate(callback: (LocationDto) -> Unit)
    suspend fun stopLocationUpdate()
}

