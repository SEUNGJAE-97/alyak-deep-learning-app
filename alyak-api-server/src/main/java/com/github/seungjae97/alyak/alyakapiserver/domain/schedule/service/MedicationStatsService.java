package com.github.seungjae97.alyak.alyakapiserver.domain.schedule.service;

import com.github.seungjae97.alyak.alyakapiserver.domain.family.dto.response.DailyMedicationStat;
import com.github.seungjae97.alyak.alyakapiserver.domain.family.dto.response.MemberStats;
import com.github.seungjae97.alyak.alyakapiserver.domain.schedule.entity.Schedule;
import com.github.seungjae97.alyak.alyakapiserver.domain.schedule.repository.ScheduleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MedicationStatsService {

    private final ScheduleRepository scheduleRepository;

    /**
     * 사용자의 전체 통계 계산
     * @param userId 사용자 ID
     * @return MemberStats 통계 정보
     */
    public MemberStats calculateMemberStats(Long userId) {
        List<Schedule> schedules = scheduleRepository.findByUser_userId(userId);
        
        int completeCount = 0;  // TAKEN
        int missedCount = 0;    // SKIPPED
        int delayedCount = 0;   // TAKEN이지만 지연
        int scheduledCount = 0; // SCHEDULED
        
        for (Schedule schedule : schedules) {
            String statusName = schedule.getStatus().getStatusName();
            
            if ("SCHEDULED".equals(statusName)) {
                scheduledCount++;
            } else if ("TAKEN".equals(statusName)) {
                completeCount++;
                // 지연 판단: takenTime이 scheduleTime보다 늦으면 지연
                if (schedule.getTakenTime() != null && 
                    schedule.getTakenTime().isAfter(schedule.getScheduleTime())) {
                    delayedCount++;
                }
            } else if ("SKIPPED".equals(statusName)) {
                missedCount++;
            }
        }
        
        // 성공률 계산: (완료 / (완료 + 놓친)) * 100
        int total = completeCount + missedCount;
        int successRate = total > 0 ? (completeCount * 100 / total) : 0;
        
        return MemberStats.builder()
            .successRate(successRate)
            .completeCount(completeCount)
            .missedCount(missedCount)
            .delayedCount(delayedCount)
            .scheduledCount(scheduledCount)
            .build();
    }
    
    /**
     * 최근 7일간 일별 통계 계산
     * @param userId 사용자 ID
     * @return 최근 7일간의 일별 통계 리스트
     */
    public List<DailyMedicationStat> calculateWeeklyStats(Long userId) {
        LocalDate today = LocalDate.now();
        List<DailyMedicationStat> weeklyStats = new ArrayList<>();
        
        for (int i = 6; i >= 0; i--) {
            LocalDate date = today.minusDays(i);
            LocalDateTime startOfDay = date.atStartOfDay();
            LocalDateTime endOfDay = date.atTime(23, 59, 59);
            
            // 해당 날짜의 스케줄 조회
            List<Schedule> daySchedules = scheduleRepository
                .findByUser_userIdAndScheduleTimeBetween(userId, startOfDay, endOfDay);
            
            int total = daySchedules.size();
            int successCount = 0;
            int delayedCount = 0;
            int missedCount = 0;
            
            for (Schedule schedule : daySchedules) {
                String statusName = schedule.getStatus().getStatusName();
                
                if ("TAKEN".equals(statusName)) {
                    successCount++;
                    // 지연 판단: takenTime이 scheduleTime보다 늦으면 지연
                    if (schedule.getTakenTime() != null && 
                        schedule.getTakenTime().isAfter(schedule.getScheduleTime())) {
                        delayedCount++;
                    }
                } else if ("SKIPPED".equals(statusName)) {
                    missedCount++;
                }
            }
            
            float successRatio = total > 0 ? (float) successCount / total : 0f;
            float delayedRatio = total > 0 ? (float) delayedCount / total : 0f;
            float missedRatio = total > 0 ? (float) missedCount / total : 0f;
            
            // LocalDate를 Date로 변환
            Date dateObj = Date.from(date.atStartOfDay(ZoneId.systemDefault()).toInstant());
            
            weeklyStats.add(DailyMedicationStat.builder()
                .date(dateObj)
                .successRatio(successRatio)
                .delayedRatio(delayedRatio)
                .missedRatio(missedRatio)
                .build());
        }
        
        return weeklyStats;
    }
}

