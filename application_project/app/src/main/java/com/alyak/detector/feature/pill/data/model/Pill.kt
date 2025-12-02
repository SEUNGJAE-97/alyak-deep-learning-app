package com.alyak.detector.feature.pill.data.model

import com.google.gson.annotations.SerializedName

data class Pill(
    @SerializedName("pillName") val name: String,
    val classification: String?,
    val manufacturer: String?,
    val pillType: String?,
    @SerializedName("pillId") val pid: String,
)

