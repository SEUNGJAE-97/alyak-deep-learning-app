package com.alyak.detector.data.api

import com.alyak.detector.data.dto.KakaoPlaceResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface KakaoLocalApi {
    @GET("v2/local/search/category.json")
    suspend fun searchByCategory(
        @Header("Authorization") apiKey: String,
        @Query("category_group_code") categoryGroupCode: String,
        @Query("x") x: String,
        @Query("y") y: String,
        @Query("radius") radius: Int,
        @Query("page") page: Int? = null,
        @Query("size") size: Int? = null,
        @Query("sort") sort: String? = null
    ): Response<KakaoPlaceResponse>
}