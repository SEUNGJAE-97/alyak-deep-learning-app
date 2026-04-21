package com.github.seungjae97.alyak.alyakapiserver.domain.schedule.service;

import com.github.seungjae97.alyak.alyakapiserver.domain.family.dto.response.DailyMedicationStat;
import com.github.seungjae97.alyak.alyakapiserver.domain.family.dto.response.MemberStats;
import com.github.seungjae97.alyak.alyakapiserver.domain.medication.entity.MedicationLog;
import com.github.seungjae97.alyak.alyakapiserver.domain.medication.enums.MedicationStatus;
import com.github.seungjae97.alyak.alyakapiserver.domain.medication.repository.MedicationLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MedicationStatsService {

    private final MedicationLogRepository medicationLogRepository;

    /**
     * 사용자의 전체 통계 계산 (복용 기록 로그 기준)
     */
    public MemberStats calculateMemberStats(Long userId) {
        List<MedicationLog> logs = medicationLogRepository.findByUser_UserId(userId);

        int completeCount = 0;
        int missedCount = 0;
        int delayedCount = 0;

        for (MedicationLog log : logs) {
            MedicationStatus s = log.getStatus();
            if (s == null) {
                continue;
            }
            switch (s) {
                case TAKEN -> completeCount++;
                case DELAYED -> {
                    delayedCount++;
                    completeCount++;
                }
                case SKIPPED -> missedCount++;
            }
        }

        int total = completeCount + missedCount;
        int successRate = total > 0 ? (completeCount * 100 / total) : 0;

        return MemberStats.builder()
                .successRate(successRate)
                .completeCount(completeCount)
                .missedCount(missedCount)
                .delayedCount(delayedCount)
                .build();
    }

    /**
     * 최근 7일간 일별 통계 계산
     */
    public List<DailyMedicationStat> calculateWeeklyStats(Long userId) {
        LocalDate today = LocalDate.now();
        List<DailyMedicationStat> weeklyStats = new ArrayList<>();

        for (int i = 6; i >= 0; i--) {
            LocalDate date = today.minusDays(i);
            LocalDateTime startOfDay = date.atStartOfDay();
            LocalDateTime endOfDay = date.atTime(23, 59, 59);

            List<MedicationLog> dayLogs = medicationLogRepository
                    .findByUser_UserIdAndScheduledTimeBetween(userId, startOfDay, endOfDay);

            int total = dayLogs.size();
            int successCount = 0;
            int delayedCount = 0;
            int missedCount = 0;

            for (MedicationLog log : dayLogs) {
                MedicationStatus s = log.getStatus();
                if (s == null) {
                    continue;
                }
                switch (s) {
                    case TAKEN -> successCount++;
                    case DELAYED -> {
                        delayedCount++;
                        successCount++;
                    }
                    case SKIPPED -> missedCount++;
                    default -> {
                    }
                }
            }

            float successRatio = total > 0 ? (float) successCount / total : 0f;
            float delayedRatio = total > 0 ? (float) delayedCount / total : 0f;
            float missedRatio = total > 0 ? (float) missedCount / total : 0f;

            weeklyStats.add(DailyMedicationStat.builder()
                    .date(date)
                    .successRatio(successRatio)
                    .delayedRatio(delayedRatio)
                    .missedRatio(missedRatio)
                    .build());
        }

        return weeklyStats;
    }
}
