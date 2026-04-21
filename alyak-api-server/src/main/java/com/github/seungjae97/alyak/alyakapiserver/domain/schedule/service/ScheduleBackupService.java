package com.github.seungjae97.alyak.alyakapiserver.domain.schedule.service;

import com.github.seungjae97.alyak.alyakapiserver.domain.schedule.dto.request.ScheduleBackupRequest;
import com.github.seungjae97.alyak.alyakapiserver.domain.schedule.dto.response.ScheduleBackupResponse;
import com.github.seungjae97.alyak.alyakapiserver.domain.schedule.entity.ScheduleBackup;
import com.github.seungjae97.alyak.alyakapiserver.domain.schedule.repository.ScheduleBackupRepository;
import com.github.seungjae97.alyak.alyakapiserver.domain.user.entity.User;
import com.github.seungjae97.alyak.alyakapiserver.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ScheduleBackupService {

    private final ScheduleBackupRepository scheduleBackupRepository;
    private final UserRepository userRepository;

    @Transactional
    public List<ScheduleBackupResponse> saveBackups(Long userId, List<ScheduleBackupRequest> requests) {
        if (requests == null || requests.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "백업할 스케줄 목록이 비어 있습니다.");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "사용자를 찾을 수 없습니다."));

        List<ScheduleBackup> entities = requests.stream()
                .map(req -> {
                    validateRequest(req);
                    return ScheduleBackup.builder()
                            .user(user)
                            .pillId(req.getPillId())
                            .pillName(req.getPillName().trim())
                            .dosage(req.getDosage())
                            .scheduledTime(req.getScheduledTime())
                            .startDate(req.getStartDate())
                            .endDate(req.getEndDate())
                            .build();
                })
                .toList();

        return scheduleBackupRepository.saveAll(entities).stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ScheduleBackupResponse> findAllForUser(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "사용자를 찾을 수 없습니다.");
        }
        return scheduleBackupRepository.findByUser_UserIdOrderByCreatedAtDesc(userId).stream()
                .map(this::toResponse)
                .toList();
    }

    /**
     * 요청 사용자와 같은 가족 구성원들의 스케줄 백업을 조회합니다.
     */
    @Transactional(readOnly = true)
    public List<ScheduleBackupResponse> findAllForFamily(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "사용자를 찾을 수 없습니다."));

        List<Long> familyIds = user.getFamilyMembers().stream()
                .map(fm -> fm.getFamily().getId())
                .toList();

        if (familyIds.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "가족 그룹에 속해 있지 않습니다.");
        }

        return familyIds.stream()
                        .flatMap(familyId -> scheduleBackupRepository.findBackupsByFamilyId(familyId).stream())
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public void deleteBackup(Long userId, Long scheduleId) {
        ScheduleBackup backup = scheduleBackupRepository.findByScheduleIdAndUser_UserId(scheduleId, userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "스케줄을 찾을 수 없거나 삭제 권한이 없습니다."));
        scheduleBackupRepository.delete(backup);
    }

    private void validateRequest(ScheduleBackupRequest req) {
        if (!StringUtils.hasText(req.getPillName())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "알약명은 필수입니다.");
        }
        if (req.getDosage() == null || req.getDosage() < 1) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "복용량은 1 이상이어야 합니다.");
        }
        if (req.getScheduledTime() == null || req.getStartDate() == null || req.getEndDate() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "복용일자는 필수입니다.");
        }
        if (req.getEndDate().isBefore(req.getStartDate())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "endDate는 startDate 이후여야 합니다.");
        }
    }

    private ScheduleBackupResponse toResponse(ScheduleBackup entity) {
        return ScheduleBackupResponse.builder()
                .scheduleId(entity.getScheduleId())
                .pillId(entity.getPillId())
                .pillName(entity.getPillName())
                .dosage(entity.getDosage())
                .scheduledTime(entity.getScheduledTime())
                .startDate(entity.getStartDate())
                .endDate(entity.getEndDate())
                .createdAt(entity.getCreatedAt())
                .build();
    }
}
