package com.github.seungjae97.alyak.alyakapiserver.domain.medication.dto.request;

import java.time.LocalDateTime;

public record MedicationScheduleUpdateDto(
        Long statusId,
        LocalDateTime scheduleStartTime,
        Integer scheduleDosage
) {}