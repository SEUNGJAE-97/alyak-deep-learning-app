package com.alyak.detector.push.dto

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "notifications")
data class NotificationEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val title: String,
    val body: String,
    val type: String?,
    val timestamp: Long = System.currentTimeMillis(),
    val isRead: Boolean = false
)
