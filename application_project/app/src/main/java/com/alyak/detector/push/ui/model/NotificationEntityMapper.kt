package com.alyak.detector.push.ui.model

import com.alyak.detector.push.dto.NotificationEntity
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

fun NotificationEntity.toNotificationItemUi(): NotificationItem {
    val fmt = SimpleDateFormat("M/d HH:mm", Locale.getDefault())
    val normalizedType = type?.trim().orEmpty().ifBlank { "GENERAL" }
    return NotificationItem(
        id = notificationId,
        title = title,
        body = body,
        time = fmt.format(Date(timestamp)),
        type = normalizedType,
        isRead = isRead,
        inviterUserId = inviterUserId,
    )
}
