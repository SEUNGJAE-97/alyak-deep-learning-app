package com.github.seungjae97.alyak.alyakapiserver.domain.schedule.repository;

import com.github.seungjae97.alyak.alyakapiserver.domain.schedule.entity.ScheduleBackup;

import java.util.List;

public interface ScheduleBackupRepositoryCustom {

    /**
     * @param familyId 가족고유아이디
     * @return List<ScheduleBackup> 소속된 가족의 모든 스케쥴 정보를 반환한다.
     * */
    List<ScheduleBackup> findBackupsByFamilyId(Long familyId);
}
