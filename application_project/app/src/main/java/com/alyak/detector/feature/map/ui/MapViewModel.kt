package com.alyak.detector.feature.map.ui

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alyak.detector.core.auth.SessionManager
import com.alyak.detector.core.auth.UserSession
import com.alyak.detector.feature.map.data.model.KakaoPlaceDto
import com.alyak.detector.feature.map.data.model.LocationDto
import com.alyak.detector.feature.map.data.repository.ApiRepo
import com.alyak.detector.feature.map.data.repository.FusedLocationRepo
import com.alyak.detector.feature.map.data.repository.KakaoPlaceRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

private val FALLBACK_MAP_CENTER = LocationDto(37.2, 127.1)

@HiltViewModel
class MapViewModel @Inject constructor(
    private val repo: KakaoPlaceRepo,
    private val locRepo: FusedLocationRepo,
    private val apiRepo: ApiRepo,
    private val sessionManager: SessionManager,
) : ViewModel() {

    private val _curLocation = MutableStateFlow(FALLBACK_MAP_CENTER)
    private val _places = MutableStateFlow<List<KakaoPlaceDto>>(emptyList())
    val places: StateFlow<List<KakaoPlaceDto>> = _places
    val curLocation: StateFlow<LocationDto> = _curLocation
    private val _routePath = MutableStateFlow<List<LocationDto>>(emptyList())
    val routePath: StateFlow<List<LocationDto>> = _routePath
    private val _moveToCurrentLocationEvent =
        MutableSharedFlow<LocationDto>(extraBufferCapacity = 1)
    val moveToCurrentLocationEvent: SharedFlow<LocationDto> =
        _moveToCurrentLocationEvent.asSharedFlow()

    val userName: StateFlow<String> = sessionManager.userSession
        .map { session ->
            when (session) {
                is UserSession.Authenticated -> session.userInfo.name
                else -> "로딩 중.."
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = "로딩 중..",
        )

    init {
        viewModelScope.launch {
            val initial = locRepo.getInitialLocationForMap() ?: FALLBACK_MAP_CENTER
            _curLocation.value = initial
        }
    }

    override fun onCleared() {
        locRepo.stopLocationUpdate()
        super.onCleared()
    }

    /**
     * 맵 준비 직후: 저장·캐시된 [curLocation]으로 카메라·주변 검색 한 번 동기화.
     */
    fun syncMapCameraToStoredLocation() {
        viewModelScope.launch {
            _moveToCurrentLocationEvent.emit(_curLocation.value)
        }
    }

    /** 맵 화면 ON_RESUME*/
    fun startContinuousLocationTracking() {
        locRepo.startLocationUpdate { dto ->
            _curLocation.value = dto
            viewModelScope.launch {
                _moveToCurrentLocationEvent.emit(dto)
            }
        }
    }

    /** 맵 화면 ON_PAUSE */
    fun stopContinuousLocationTracking() {
        locRepo.stopLocationUpdate()
    }

    fun fetchPlaces(
        apiKey: String,
        categoryGroupCode: String,
        x: String,
        y: String,
        radius: Int,
        page: Int? = null,
        size: Int? = null,
        sort: String? = null,
    ) {
        viewModelScope.launch {
            try {
                val result = repo.searchPlace(
                    apiKey, categoryGroupCode, x, y, radius, page, size, sort,
                )
                _places.value = result
            } catch (e: Exception) {
                Log.e("MapViewModel", "fetchPlaces error: ${e.message}", e)
            }
        }
    }

    /** 내 위치 갱신할때 사용, 맵 중앙에 내 위치가 나오게 이동 */
    fun fetchLocation() {
        viewModelScope.launch {
            val result = locRepo.getCurrentLocation() ?: return@launch
            _curLocation.value = result
            _moveToCurrentLocationEvent.emit(result)
            Log.d("MapViewModel", "fetchLocation: $result")
        }
    }

    fun findPath(destination: LocationDto, destinationId: String) {
        viewModelScope.launch {
            try {
                val start = _curLocation.value
                val pathDto = apiRepo.pathFind(start, destination, destinationId.toIntOrNull() ?: 0)
                _routePath.value = pathDto.path
            } catch (_: Exception) {
                // 에러 처리
            }
        }
    }
}
