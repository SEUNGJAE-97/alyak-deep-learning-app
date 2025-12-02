package com.alyak.detector.feature.map.data.api

import com.alyak.detector.feature.map.data.model.PathDto
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface MapApi {

    @GET("/api/map")
    suspend fun findPath(
        @Query("startLat") startLat: Double,
        @Query("startLng") startLng: Double,
        @Query("endLat") endLat: Double,
        @Query("endLng") endLng: Double,
        @Query("destinationId") destinationId: Int
    ): Response<PathDto>
}