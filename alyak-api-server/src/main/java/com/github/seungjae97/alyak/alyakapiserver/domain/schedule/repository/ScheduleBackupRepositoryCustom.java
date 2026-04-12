package com.github.seungjae97.alyak.alyakapiserver.domain.schedule.repository;

import com.github.seungjae97.alyak.alyakapiserver.domain.schedule.entity.ScheduleBackup;

import java.util.List;

public interface ScheduleBackupRepositoryCustom {

    List<ScheduleBackup> findBackupsByFamilyId(Long familyId);
}
