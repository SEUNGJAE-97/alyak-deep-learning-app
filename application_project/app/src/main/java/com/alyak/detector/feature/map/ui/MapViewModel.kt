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
import com.alyak.detector.feature.map.ui.model.MapPlaceFilter
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.supervisorScope
import javax.inject.Inject

private val FALLBACK_MAP_CENTER = LocationDto(37.2, 127.1)

/** 카카오 로컬 카테고리 그룹: 병원 */
const val KAKAO_CATEGORY_HOSPITAL = "HP8"

/** 카카오 로컬 카테고리 그룹: 약국 */
const val KAKAO_CATEGORY_PHARMACY = "PM9"

@HiltViewModel
class MapViewModel @Inject constructor(
    private val repo: KakaoPlaceRepo,
    private val locRepo: FusedLocationRepo,
    private val apiRepo: ApiRepo,
    private val sessionManager: SessionManager,
) : ViewModel() {

    private val _curLocation = MutableStateFlow(FALLBACK_MAP_CENTER)
    private val _places = MutableStateFlow<List<KakaoPlaceDto>>(emptyList())

    /** 병원+약국 병합 등 마지막으로 조회·저장한 전체 목록 */
    val places: StateFlow<List<KakaoPlaceDto>> = _places

    private val _placeFilter = MutableStateFlow(MapPlaceFilter.ALL)
    val placeFilter: StateFlow<MapPlaceFilter> = _placeFilter

    /** 필터 반영 후 맵·시트에 쓰는 목록 */
    val displayedPlaces: StateFlow<List<KakaoPlaceDto>> =
        combine(_places, _placeFilter) { list, filter ->
            when (filter) {
                MapPlaceFilter.ALL,
                MapPlaceFilter.OPEN_NOW,
                    -> list

                MapPlaceFilter.HOSPITAL -> list.filter { it.category_group_code == KAKAO_CATEGORY_HOSPITAL }
                MapPlaceFilter.PHARMACY -> list.filter { it.category_group_code == KAKAO_CATEGORY_PHARMACY }
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList(),
        )
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

    /**
     * 주변 병원(HP8)·약국(PM9)을 각각 조회한 뒤 합치고, 같은 [id]는 한 번만 둡니다.
     */
    fun fetchPlaces(
        apiKey: String,
        x: String,
        y: String,
        radius: Int,
        page: Int? = null,
        size: Int? = null,
        sort: String? = null,
    ) {
        viewModelScope.launch {
            try {
                supervisorScope {
                    val hospital = async {
                        repo.searchPlace(
                            apiKey, KAKAO_CATEGORY_HOSPITAL, x, y, radius, page, size, sort,
                        )
                    }
                    val pharmacy = async {
                        repo.searchPlace(
                            apiKey, KAKAO_CATEGORY_PHARMACY, x, y, radius, page, size, sort,
                        )
                    }
                    val merged = hospital.await() + pharmacy.await()
                    _places.value = merged.distinctBy { it.id }
                }
            } catch (e: Exception) {
                Log.e("MapViewModel", "fetchPlaces error: ${e.message}", e)
            }
        }
    }

    fun setPlaceFilter(filter: MapPlaceFilter) {
        _placeFilter.value = filter
    }

    /** 내 위치 버튼 */
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
