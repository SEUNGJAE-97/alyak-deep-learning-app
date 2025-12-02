package com.alyak.detector.feature.map.data.repository

import com.alyak.detector.feature.map.data.api.MapApi
import com.alyak.detector.feature.map.data.model.LocationDto
import com.alyak.detector.feature.map.data.model.PathDto
import jakarta.inject.Inject

class ApiRepoImpl @Inject constructor(
    private val api: MapApi
) : ApiRepo {
    override suspend fun pathFind(
        start: LocationDto,
        end: LocationDto,
        destinationId: Int
    ): PathDto {
        val response = api.findPath(start.latitude, start.longitude, end.latitude, end.longitude, destinationId)

        if (response.isSuccessful) {
            val body = response.body()
            if (body != null) {
                return body
            } else {
                throw Exception("API 응답 본문이 없습니다.")
            }
        } else {
            throw Exception("API 호출 실패: ${response.code()} - ${response.message()}")
        }
    }
}