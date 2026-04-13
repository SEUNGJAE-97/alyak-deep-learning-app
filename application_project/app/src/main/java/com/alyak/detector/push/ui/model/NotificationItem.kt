package com.alyak.detector.push.ui.model

/**
 * 알림 목록/카드에 표시하기 위한 UI 모델.
 */
data class NotificationItem(
    val id: Int,
    val title: String,
    val body: String,
    val time: String,
    val type: String,
    val isRead: Boolean = false,
    val inviterUserId: Long? = null,
)
