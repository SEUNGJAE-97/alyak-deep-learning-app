package com.alyak.detector.feature.pill.data.model.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.alyak.detector.feature.pill.data.model.local.dao.RecentSearchDao
import com.alyak.detector.feature.pill.data.model.local.entity.RecentSearchEntity
import com.alyak.detector.push.dao.NotificationDao
import com.alyak.detector.push.dto.NotificationEntity

@Database(
    entities = [RecentSearchEntity::class, NotificationEntity::class],
    version = 4,
    exportSchema = false
)
abstract class PillDatabase : RoomDatabase() {
    abstract fun recentSearchDao(): RecentSearchDao
    abstract fun notificationDao(): NotificationDao
}

