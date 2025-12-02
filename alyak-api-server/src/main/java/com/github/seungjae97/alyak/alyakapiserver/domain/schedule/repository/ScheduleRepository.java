package com.github.seungjae97.alyak.alyakapiserver.domain.schedule.repository;

import com.github.seungjae97.alyak.alyakapiserver.domain.schedule.entity.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ScheduleRepository extends JpaRepository<Schedule, Integer>, ScheduleRepositoryCustom {
    List<Schedule> findByUser_userId(Long userId);
    
    /**
     * 사용자 ID와 날짜 범위로 스케줄 조회
     * @param userId 사용자 ID
     * @param start 시작 시간
     * @param end 종료 시간
     * @return 해당 기간의 스케줄 목록
     */
    List<Schedule> findByUser_userIdAndScheduleTimeBetween(
        Long userId, 
        LocalDateTime start, 
        LocalDateTime end
    );
}
