package com.alyak.detector.push.ui

import com.alyak.detector.R
import com.alyak.detector.push.ui.model.NotificationItem

/**
 * 알림 유형별 아이콘. [iconRes]가 null이면 M3 [Icons.Default.Notifications] 벨 아이콘 사용.
 */
enum class NotificationCategory {
    /** 가족 초대 — `@drawable/group_24` */
    FAMILY_INVITE,

    /** 가족 활동 — 기존 벨 아이콘 유지 */
    FAMILY_ACTIVITY,

    /** 약 복용 알림 — `@drawable/medication_24` */
    PILL,

    /** 그 외 — `@drawable/info_24` */
    GENERAL,
    ;

    val iconRes: Int?
        get() = when (this) {
            FAMILY_INVITE -> R.drawable.group_24
            FAMILY_ACTIVITY -> null
            PILL -> R.drawable.medication_24
            GENERAL -> R.drawable.info_24
        }

    val useMaterialBell: Boolean
        get() = this == FAMILY_ACTIVITY

    companion object {
        fun from(item: NotificationItem): NotificationCategory {
            val t = item.type.trim().uppercase()
            return when {
                t == "FAMILY_INVITE" || item.inviterUserId != null -> FAMILY_INVITE
                t == "FAMILY" -> FAMILY_ACTIVITY
                t == "PILL" || t == "MEDICATION" -> PILL
                else -> GENERAL
            }
        }
    }
}
