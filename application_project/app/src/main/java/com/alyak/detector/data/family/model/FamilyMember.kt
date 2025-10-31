package com.alyak.detector.data.family.model

data class FamilyMember(
    val role: String,
    val name: String,
    val isSelected: Boolean,
    val stats: MemberStats    // 기본값 없음
)

data class MemberStats(
    val successRate: Int,
    val completeCount: Int,
    val missedCount: Int,
    val delayedCount: Int,
    val scheduledCount: Int
)