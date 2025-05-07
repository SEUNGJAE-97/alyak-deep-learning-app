package com.alyak.detector.ui.map

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alyak.detector.data.dto.KakaoPlaceDto
import com.alyak.detector.data.repository.KakaoPlaceRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MapViewModel @Inject constructor(
    private val repo: KakaoPlaceRepo
) : ViewModel() {
    private val _places = MutableStateFlow<List<KakaoPlaceDto>>(emptyList())
    val places: StateFlow<List<KakaoPlaceDto>> = _places

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
    fun fetchPlaces(
        apiKey: String,
        categoryGroupCode: String,
        x: String,
        y: String,
        radius: Int,
        page: Int? = null,
        size: Int? = null,
        sort: String? = null
    ) {

        viewModelScope.launch {
            try {
                val result = repo.searchPlace(
                    apiKey, categoryGroupCode, x, y, radius, page, size, sort
                )
                _places.value = result
            } catch (e: Exception) {
                // 에러 처리
                Log.e("MapViewModel", "fetchPlaces error: ${e.message}", e)
            }
        }
    }
}
