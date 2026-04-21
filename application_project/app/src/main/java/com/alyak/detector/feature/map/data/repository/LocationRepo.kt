package com.alyak.detector.feature.map.data.repository

import com.alyak.detector.feature.map.data.model.LocationDto

interface LocationRepo {
    /** 단발 현재 위치 */
    suspend fun getCurrentLocation(): LocationDto?

    /**
     * 맵 첫 표시용: 저장된 좌표 → 없으면 fused lastLocation.
     */
    suspend fun getInitialLocationForMap(): LocationDto?

    /** 위치 연속 업데이트 */
    fun startLocationUpdate(onLocation: (LocationDto) -> Unit)

    fun stopLocationUpdate()
}
