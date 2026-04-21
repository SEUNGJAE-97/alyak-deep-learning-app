package com.alyak.detector.feature.notification

/**
 * 포그라운드에서 인앱 상단 배너로 표시할 푸시 페이로드입니다.
 */
data class InAppPushEvent(
    val title: String,
    val body: String,
    val notificationId: Int,
    val type: String?,
    val inviterUserId: String?,
    val inviterName: String?,
) {
    val isFamilyInvite: Boolean
        get() = type == "FAMILY_INVITE"
}
