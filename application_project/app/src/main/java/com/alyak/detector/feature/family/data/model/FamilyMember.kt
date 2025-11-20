package com.alyak.detector.feature.family.data.model

import java.util.Date

data class FamilyMember(
    val role: String,
    val name: String,
    val isSelected: Boolean,
    val stats: MemberStats,
    val weeklyMedicationStats: List<DailyMedicationStat> // 주간 데이터 (최근 7일)
)

data class MemberStats(
    val successRate: Int,
    val completeCount: Int,
    val missedCount: Int,
    val delayedCount: Int,
    val scheduledCount: Int
)

data class DailyMedicationStat(
    val date: Date,
    val successRatio: Float,
    val delayedRatio: Float,
    val missedRatio: Float
)

