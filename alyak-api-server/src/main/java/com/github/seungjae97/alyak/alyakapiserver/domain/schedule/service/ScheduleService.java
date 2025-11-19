package com.github.seungjae97.alyak.alyakapiserver.domain.schedule.service;

import com.github.seungjae97.alyak.alyakapiserver.domain.schedule.dto.response.UserScheduleResponse;
import com.github.seungjae97.alyak.alyakapiserver.domain.schedule.entity.Schedule;
import com.github.seungjae97.alyak.alyakapiserver.domain.schedule.mapper.ScheduleMapper;
import com.github.seungjae97.alyak.alyakapiserver.domain.schedule.repository.ScheduleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ScheduleService {

    private final ScheduleRepository scheduleRepository;
    private final ScheduleMapper scheduleMapper;

    /**
     * UserId 값으로 모든 복약정보 가져온다.
     * @param userId 유저 ID 값
     * */
    public List<UserScheduleResponse> findAllByUserId(Long userId) {
        List<Schedule> schedules = scheduleRepository.findByUser_userId(userId);
        return scheduleMapper.convertToDtoList(schedules);
    }

    /**
     * User가 속해있는 가족이 가지고 있는 스케쥴을 조회한다.
     * @param familyId 유저가 속한 가족 ID 값
     * */
    public List<UserScheduleResponse> findAllByUserIdFromFamily(Long familyId) {
        List<Schedule> schedules = scheduleRepository.findSchedulesByFamilyId(familyId);
        return scheduleMapper.convertToDtoList(schedules);
    }
}
