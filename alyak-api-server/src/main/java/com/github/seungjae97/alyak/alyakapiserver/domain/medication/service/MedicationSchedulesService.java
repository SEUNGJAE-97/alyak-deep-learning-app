package com.github.seungjae97.alyak.alyakapiserver.domain.medication.service;

import com.github.seungjae97.alyak.alyakapiserver.domain.medication.entity.MedicationSchedules;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface MedicationSchedulesService {
    /**
     * 특정 사용자의 모든 알약 스케줄 조회
     * @param userId 사용자 ID
     * @return 해당 사용자의 알약 스케줄 목록
     */
    List<MedicationSchedules> getByUserId(Long userId);
    
    /**
     * 특정 사용자 알약의 스케줄 조회
     * @param userMedicationId 사용자 알약 ID
     * @return 해당 사용자 알약의 스케줄 목록
     */
    List<MedicationSchedules> getByUserMedicationId(Long userMedicationId);
    
    /**
     * 특정 기간의 알약 스케줄 조회
     * @param start 시작 시간
     * @param end 종료 시간
     * @return 해당 기간의 알약 스케줄 목록
     */
    List<MedicationSchedules> getByScheduledTimeBetween(LocalDateTime start, LocalDateTime end);
    
    /**
     * 특정 알약 스케줄 조회
     * @param id 알약 스케줄 ID
     * @return 알약 스케줄 정보
     */
    Optional<MedicationSchedules> getById(Long id);
    
    /**
     * 알약 스케줄 생성
     * @param medicationSchedule 생성할 알약 스케줄 정보
     * @return 생성된 알약 스케줄
     */
    MedicationSchedules create(MedicationSchedules medicationSchedule);
    
    /**
     * 알약 스케줄 수정
     * @param medicationSchedule 수정할 알약 스케줄 정보
     * @return 수정된 알약 스케줄
     */
    MedicationSchedules update(MedicationSchedules medicationSchedule);
    
    /**
     * 알약 스케줄 삭제
     * @param id 삭제할 알약 스케줄 ID
     */
    void delete(Long id);
} 