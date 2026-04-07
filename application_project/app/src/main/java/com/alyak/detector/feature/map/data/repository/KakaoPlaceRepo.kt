package com.alyak.detector.feature.map.data.repository

import android.util.Log
import com.alyak.detector.feature.map.data.api.KakaoLocalApi
import com.alyak.detector.feature.map.data.model.KakaoPlaceDto
import javax.inject.Inject

class KakaoPlaceRepo @Inject constructor(
    private val api: KakaoLocalApi
) {
    /**
     * 카카오 장소 카테고리 검색 요청
     *
     * @param apiKey           카카오 REST API 키 ("KakaoAK {REST_API_KEY}" 형식)
     * @param categoryGroupCode 카테고리 그룹 코드 (예: "CE7"=카페, "FD6"=음식점 등)
     * @param x                중심 좌표의 경도(Longitude) (예: "127.027583")
     * @param y                중심 좌표의 위도(Latitude) (예: "37.497942")
     * @param radius           검색 반경(미터, 0~20000, 기본 500)
     * @param page             결과 페이지 번호 (1~45, 기본 1, 선택)
     * @param size             한 페이지에 보여질 문서 수 (1~15, 기본 15, 선택)
     * @param sort             정렬 방식 ("distance" 또는 "accuracy", 기본 "accuracy", 선택)
     */
    suspend fun searchPlace(
        apiKey: String,
        categoryGroupCode: String,
        x: String,
        y: String,
        radius: Int,
        page: Int? = null,
        size: Int? = null,
        sort: String? = null
    ): List<KakaoPlaceDto> {
        try {
            val response =
                api.searchByCategory(apiKey, categoryGroupCode, x, y, radius, page, size, sort)
            if (response.isSuccessful) {
                val places = response.body()?.documents ?: emptyList()
                Log.d("KakaoPlaceRepo", "API call successful. Found ${places.size} places")
                return places
            } else {
                Log.e(
                    "KakaoPlaceRepo",
                    "API call failed: ${response.code()} - ${response.message()}"
                )
                return emptyList()
            }
        } catch (e: Exception) {
            Log.e("KakaoPlaceRepo", "API call error", e)
            return emptyList()
        }
    }

    /**
     * @param apiKey           카카오 REST API 키
     * @param query            검색을 원하는 질의어
     * @param x                중심 좌표의 경도(Longitude)
     * @param y                중심 좌표의 위도(Latitude)
     * */
    suspend fun searchByKeyword(
        apiKey: String,
        query: String,
        x: String,
        y: String
    ): List<KakaoPlaceDto> {
        val response = api.searchByKeyword(
            apiKey = apiKey,
            query = query,
            x = x,
            y = y,
            radius = 10000 // 10km 반경 내
        )
        return response.body()?.documents ?: emptyList()
    }
}

