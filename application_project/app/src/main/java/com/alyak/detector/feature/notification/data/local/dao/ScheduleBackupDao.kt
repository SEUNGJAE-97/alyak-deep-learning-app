package com.alyak.detector.feature.notification.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.alyak.detector.feature.notification.data.local.entity.ScheduleBackupEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ScheduleBackupDao {

    @Query("SELECT * FROM medication_schedule_backup ORDER BY createdAtEpochMillis DESC")
    fun observeAll(): Flow<List<ScheduleBackupEntity>>

    @Query("SELECT * FROM medication_schedule_backup")
    suspend fun getAll(): List<ScheduleBackupEntity>

    @Query("SELECT * FROM medication_schedule_backup WHERE scheduleId = :scheduleId LIMIT 1")
    suspend fun getByScheduleId(scheduleId: Long): ScheduleBackupEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(entities: List<ScheduleBackupEntity>)

    @Query("DELETE FROM medication_schedule_backup WHERE scheduleId = :scheduleId")
    suspend fun deleteByScheduleId(scheduleId: Long)

    @Query("DELETE FROM medication_schedule_backup")
    suspend fun deleteAll()
}
