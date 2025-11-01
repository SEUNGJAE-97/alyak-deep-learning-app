package com.alyak.detector.data.family.model

import java.util.Date

data class FamilyMember(
    val role: String,
    val name: String,
    val isSelected: Boolean,
    val stats: MemberStats,
    val dailyMedicationStat: DailyMedicationStat
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