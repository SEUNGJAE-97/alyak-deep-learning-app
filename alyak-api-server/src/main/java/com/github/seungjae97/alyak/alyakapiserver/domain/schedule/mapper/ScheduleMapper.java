package com.github.seungjae97.alyak.alyakapiserver.domain.schedule.mapper;

import com.github.seungjae97.alyak.alyakapiserver.domain.schedule.dto.response.UserScheduleResponse;
import com.github.seungjae97.alyak.alyakapiserver.domain.schedule.entity.Schedule;
import com.github.seungjae97.alyak.alyakapiserver.global.common.mapper.BaseMapper;
import org.springframework.stereotype.Component;

@Component
public class ScheduleMapper implements BaseMapper<Schedule, UserScheduleResponse> {
    @Override
    public UserScheduleResponse toDto(Schedule schedule) {
        return UserScheduleResponse.builder()
                .scheduleId(schedule.getScheduleId())
                .pillName(schedule.getPill().getPillName())
                .userMethod(schedule.getPill().getUserMethod())
                .scheduleTime(schedule.getScheduleTime())
                .scheduleStartTime(schedule.getScheduleStartTime())
                .scheduleEndTime(schedule.getScheduleEndTime())
                .pillDosage(schedule.getScheduleDosage())
                .build();
    }

}

