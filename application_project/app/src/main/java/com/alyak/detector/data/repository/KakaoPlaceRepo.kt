package com.alyak.detector.data.repository

import android.util.Log
import com.alyak.detector.data.api.KakaoLocalApi
import com.alyak.detector.data.dto.map.KakaoPlaceDto
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
        try {
            val response = api.searchByCategory(apiKey, categoryGroupCode, x, y, radius, page, size, sort)
            if(response.isSuccessful){
                val places = response.body()?.documents ?: emptyList()
                Log.d("KakaoPlaceRepo", "API call successful. Found ${places.size} places")
                return places
            } else {
                Log.e("KakaoPlaceRepo", "API call failed: ${response.code()} - ${response.message()}")
                return emptyList()
            }
        } catch (e: Exception) {
            Log.e("KakaoPlaceRepo", "API call error", e)
            return emptyList()
        }
    }
}