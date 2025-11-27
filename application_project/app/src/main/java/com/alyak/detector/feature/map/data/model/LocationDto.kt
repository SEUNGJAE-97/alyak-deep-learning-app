package com.alyak.detector.feature.map.data.model

import com.google.gson.annotations.SerializedName

data class LocationDto(
    @SerializedName("lat")
    val latitude: Double,
    @SerializedName("lng")
    val longitude: Double
)

