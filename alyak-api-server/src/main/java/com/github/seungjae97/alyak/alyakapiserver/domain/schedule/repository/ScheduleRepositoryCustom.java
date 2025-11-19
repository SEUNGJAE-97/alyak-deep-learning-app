package com.github.seungjae97.alyak.alyakapiserver.domain.schedule.repository;

import com.github.seungjae97.alyak.alyakapiserver.domain.schedule.entity.Schedule;

import java.util.List;

public interface ScheduleRepositoryCustom {
    List<Schedule> findSchedulesByFamilyId(Long familyId);
}
