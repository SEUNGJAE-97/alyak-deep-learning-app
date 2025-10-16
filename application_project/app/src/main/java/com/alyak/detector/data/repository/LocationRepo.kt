package com.alyak.detector.data.repository

import com.alyak.detector.data.dto.map.LocationDto

interface LocationRepo {
    suspend fun getCurrentLocation() : LocationDto
    suspend fun startLocationUpdate(callback : (LocationDto) -> Unit )
    suspend fun stopLocationUpdate()
}