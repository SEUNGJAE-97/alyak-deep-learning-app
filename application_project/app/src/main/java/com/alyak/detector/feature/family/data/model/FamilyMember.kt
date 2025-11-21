package com.alyak.detector.feature.family.data.model

import java.util.Date

data class FamilyMember(
    val role: String,
    val name: String,
    val stats: MemberStats,
    val weeklyMedicationStats: List<DailyMedicationStat>
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
