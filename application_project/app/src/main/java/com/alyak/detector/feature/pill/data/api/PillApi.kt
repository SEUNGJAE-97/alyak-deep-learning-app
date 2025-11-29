package com.alyak.detector.feature.pill.data.api

import com.alyak.detector.feature.pill.data.model.Pill
import retrofit2.http.GET

interface PillApi {
    @GET("pills/search/recent")
    suspend fun getRecentSearchPills(): List<Pill>
}
