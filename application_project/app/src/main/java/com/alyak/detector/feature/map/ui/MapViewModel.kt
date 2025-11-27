package com.alyak.detector.feature.map.ui

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alyak.detector.feature.map.data.model.KakaoPlaceDto
import com.alyak.detector.feature.map.data.model.LocationDto
import com.alyak.detector.feature.map.data.repository.ApiRepo
import com.alyak.detector.feature.map.data.repository.FusedLocationRepo
import com.alyak.detector.feature.map.data.repository.KakaoPlaceRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MapViewModel @Inject constructor(
    private val repo: KakaoPlaceRepo,
    private val locRepo: FusedLocationRepo,
    private val ApiRepo: ApiRepo
) : ViewModel() {
    private val _curLocation = MutableStateFlow(LocationDto(37.2, 127.1))
    private val _places = MutableStateFlow<List<KakaoPlaceDto>>(emptyList())
    val places: StateFlow<List<KakaoPlaceDto>> = _places
    val curLocation: StateFlow<LocationDto> = _curLocation
    private val _routePath = MutableStateFlow<List<LocationDto>>(emptyList())
    val routePath: StateFlow<List<LocationDto>> = _routePath
    private val _moveToCurrentLocationEvent = MutableSharedFlow<LocationDto>()
    val moveToCurrentLocationEvent: SharedFlow<LocationDto> = _moveToCurrentLocationEvent.asSharedFlow()

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

    fun fetchLocation() {
        viewModelScope.launch {
            val result = locRepo.getCurrentLocation()
            _curLocation.value = result
            _moveToCurrentLocationEvent.emit(result)
            Log.d("MapViewModel", "fetchLocation: $result")
        }
    }

    fun findPath(destination: LocationDto) {
        viewModelScope.launch {
            try {
                val start = _curLocation.value
                val pathDto = ApiRepo.pathFind(start, destination)
                _routePath.value = pathDto.path
            } catch (e: Exception) {
                // 에러 처리
            }
        }
    }
}
