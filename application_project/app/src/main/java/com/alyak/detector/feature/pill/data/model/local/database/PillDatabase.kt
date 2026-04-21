package com.alyak.detector.feature.pill.data.model.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.alyak.detector.feature.notification.data.local.dao.ScheduleBackupDao
import com.alyak.detector.feature.notification.data.local.entity.ScheduleBackupEntity
import com.alyak.detector.feature.pill.data.model.local.dao.RecentSearchDao
import com.alyak.detector.feature.pill.data.model.local.entity.RecentSearchEntity
import com.alyak.detector.push.dao.NotificationDao
import com.alyak.detector.push.dto.NotificationEntity

@Database(
    entities = [RecentSearchEntity::class, NotificationEntity::class, ScheduleBackupEntity::class],
    version = 5,
    exportSchema = false
)
abstract class PillDatabase : RoomDatabase() {
    abstract fun recentSearchDao(): RecentSearchDao
    abstract fun notificationDao(): NotificationDao
    abstract fun scheduleBackupDao(): ScheduleBackupDao
}

