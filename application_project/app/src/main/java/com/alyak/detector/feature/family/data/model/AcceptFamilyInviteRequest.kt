package com.alyak.detector.feature.family.data.model

import com.google.gson.annotations.SerializedName

data class AcceptFamilyInviteRequest(
    @SerializedName("inviterUserId") val inviterUserId: Long,
)
