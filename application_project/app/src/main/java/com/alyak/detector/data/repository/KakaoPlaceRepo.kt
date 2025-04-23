package com.alyak.detector.data.repository

import com.alyak.detector.data.api.KakaoLocalApi
import com.alyak.detector.data.dto.KakaoPlaceDto
import javax.inject.Inject

class KakaoPlaceRepo @Inject constructor(
    private val api : KakaoLocalApi
){
    suspend fun searchPlace(
        apiKey: String,
        categoryGroupCode: String,
        x: String,
        y: String,
        radius: Int,
        page: Int? = null,
        size: Int? = null,
        sort: String? = null
    ) : List<KakaoPlaceDto> {
        val response = api.searchByCategory(apiKey, categoryGroupCode, x, y, radius, page, size, sort)
        if(response.isSuccessful){
            return response.body()?.documents ?: emptyList()
        }
        return emptyList()
    }
}