package com.alyak.detector.push.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.alyak.detector.push.dto.NotificationEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface NotificationDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotification(notification: NotificationEntity)

    @Query("SELECT COUNT(*) FROM notifications WHERE isRead = false")
    fun getUnreadCountFlow(): Flow<Int>

    @Query("SELECT * FROM notifications ORDER BY timestamp DESC")
    fun getAllNotificationsFlow(): Flow<List<NotificationEntity>>

    @Query("DELETE FROM notifications WHERE notificationId = :id")
    suspend fun deleteById(id: Int)

    @Query("UPDATE notifications SET isRead = 1 WHERE notificationId = :id")
    suspend fun markAsRead(id: Int)

    @Query("UPDATE notifications SET isRead = 1")
    suspend fun markAllAsRead()

    @Query("DELETE FROM notifications WHERE isRead = 1")
    suspend fun deleteAllRead()
}