package com.github.seungjae97.alyak.alyakapiserver.domain.schedule.service;

import com.github.seungjae97.alyak.alyakapiserver.domain.schedule.dto.response.UserScheduleResponse;
import com.github.seungjae97.alyak.alyakapiserver.domain.schedule.entity.Schedule;
import com.github.seungjae97.alyak.alyakapiserver.domain.schedule.repository.ScheduleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ScheduleService {

    private final ScheduleRepository scheduleRepository;

    /**
     * UserId 값으로 모든 복약정보 가져온다.
     * @param userId 유저 ID 값
     * */
    public List<UserScheduleResponse> findAllByUserId(Long userId) {
        List<Schedule> schedules = scheduleRepository.findByUser_userId(userId);
        List<UserScheduleResponse> userScheduleResponseList = new ArrayList<>();

        for(Schedule schedule : schedules){
            userScheduleResponseList.add(
                    UserScheduleResponse.builder()
                            .scheduleId(schedule.getScheduleId())
                            .pillName(schedule.getPill().getPillName())
                            .scheduleTime(schedule.getScheduleTime())
                            .pillDosage(0)
                            .scheduleStartTime(schedule.getScheduleStartTime())
                            .scheduleEndTime(schedule.getScheduleEndTime())
                            .build()
            );
        }
        return userScheduleResponseList;
    }
}
