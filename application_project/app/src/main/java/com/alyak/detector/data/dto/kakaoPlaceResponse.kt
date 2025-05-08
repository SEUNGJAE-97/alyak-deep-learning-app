package com.alyak.detector.data.dto

data class KakaoPlaceResponse(
    val meta: Meta,
    val documents: List<KakaoPlaceDto>
)

data class Meta(
    val totalCount: Int,
    val pageableCount: Int,
    val isEnd: Boolean
)