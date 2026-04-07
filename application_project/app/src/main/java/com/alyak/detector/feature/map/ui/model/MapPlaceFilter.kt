package com.alyak.detector.feature.map.ui.model

/**
 * 맵 장소 목록·마커 필터 (메모리 상 `places` 기준).
 *
 * @property chipLabel 필터 칩에 표시하는 짧은 라벨
 * @property sheetCountNoun 바텀시트 "총 n개의 …" 에 붙는 명사
 */
enum class MapPlaceFilter(
    val chipLabel: String,
    val sheetCountNoun: String,
) {
    ALL("전체", "병원·약국"),
    HOSPITAL("병원", "병원"),
    PHARMACY("약국", "약국"),
    /** 임시 속성 — 목록은 [ALL]과 동일 */
    OPEN_NOW("영업중", "장소"),
}
