package com.alyak.detector.push.dto

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "notifications")
data class NotificationEntity(
    @PrimaryKey
    val notificationId: Int,
    val title: String,
    val body: String,
    val type: String?,
    val timestamp: Long = System.currentTimeMillis(),
    val isRead: Boolean = false,
    /** FCM `inviterUserId` — 가족 초대 알림에서만 사용 */
    val inviterUserId: Long? = null,
)
