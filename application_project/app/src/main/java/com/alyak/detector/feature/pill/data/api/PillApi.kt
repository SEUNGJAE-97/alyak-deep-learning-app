package com.alyak.detector.feature.pill.data.api

import com.alyak.detector.feature.pill.data.model.MedicineDetailDto
import com.alyak.detector.feature.pill.data.model.Pill
import com.alyak.detector.feature.pill.data.model.ServerResponsePillDetail
import retrofit2.http.GET
import retrofit2.http.Query

interface PillApi {
    @GET("api/pill/search")
    suspend fun getPillSearchResult(
        @Query("shape") shape:String,
        @Query("color") color:String,
        @Query("score") score:String,
    ): List<Pill>

    @GET("api/pill/find")
    suspend fun getPillFindResult(
        @Query("pillName") name : String
    ): List<Pill>

    @GET("api/pill/detail")
    suspend fun getPillDetail(
        @Query("pillId")pid : Long
    ): ServerResponsePillDetail
}