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
                // markerList에 값 할당 전
                Log.d("MapViewModel", "places size: ${size}")
                // 네트워크 응답 후
                Log.d("MapViewModel", "fetchPlaces result: $result")
                _places.value = result
            } catch (e: Exception) {
                // 에러 처리
                Log.e("MapViewModel", "fetchPlaces error: ${e.message}", e)
            }
        }
    }
}
