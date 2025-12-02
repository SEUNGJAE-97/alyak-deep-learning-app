package com.alyak.detector.feature.pill.data.model.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.alyak.detector.feature.pill.data.model.local.dao.RecentSearchDao
import com.alyak.detector.feature.pill.data.model.local.entity.RecentSearchEntity

@Database(
    entities = [RecentSearchEntity::class],
    version = 1,
    exportSchema = false
)
abstract class PillDatabase : RoomDatabase() {
    abstract fun recentSearchDao(): RecentSearchDao
}

