package com.alyak.detector.feature.map.data.model

/**
 * 카카오 장소 정보를 담는 DTO입니다.
 *
 * @property id 장소의 고유 ID
 * @property place_name 장소명
 * @property category_name 카테고리 이름
 * @property category_group_code 중요 카테고리만 그룹핑한 카테고리 그룹 코드
 * @property category_group_name 중요 카테고리만 그룹핑한 카테고리 그룹명
 * @property phone 전화번호
 * @property address_name 전체 지번 주소
 * @property road_address_name 전체 도로명 주소
 * @property x 경도(Longitude)
 * @property y 위도(Latitude)
 * @property place_url 장소 상세 페이지 URL
 * @property distance 중심좌표까지의 거리(미터)
 */
data class KakaoPlaceDto(
    val id: String,
    val place_name: String,
    val category_name: String,
    val category_group_code: String,
    val category_group_name: String,
    val phone: String,
    val address_name: String,
    val road_address_name: String,
    val x: String,
    val y: String,
    val place_url: String,
    val distance: String
)

