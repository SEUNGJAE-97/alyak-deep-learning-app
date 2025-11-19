package com.github.seungjae97.alyak.alyakapiserver.domain.schedule.repository;

import com.github.seungjae97.alyak.alyakapiserver.domain.schedule.entity.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ScheduleRepository extends JpaRepository<Schedule, Integer>, ScheduleRepositoryCustom {
    List<Schedule> findByUser_userId(Long userId);
}
