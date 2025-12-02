package com.alyak.detector.feature.pill.data.model.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "recent_searches")
data class RecentSearchEntity(
    @PrimaryKey(autoGenerate = false) val id: Long,
    val pillName: String,
    val manufacturer: String?,
    val type: String?,
    val classification: String?,
    val timestamp: Long = System.currentTimeMillis() // 정렬용 시간
)


