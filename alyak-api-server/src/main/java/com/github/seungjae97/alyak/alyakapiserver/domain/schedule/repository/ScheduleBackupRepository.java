package com.github.seungjae97.alyak.alyakapiserver.domain.schedule.repository;

import com.github.seungjae97.alyak.alyakapiserver.domain.schedule.entity.ScheduleBackup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ScheduleBackupRepository extends JpaRepository<ScheduleBackup, Long>, ScheduleBackupRepositoryCustom {

    List<ScheduleBackup> findByUser_UserIdOrderByCreatedAtDesc(Long userId);

    Optional<ScheduleBackup> findByScheduleIdAndUser_UserId(Long scheduleId, Long userId);
}
