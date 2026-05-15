package com.github.seungjae97.alyak.alyakapiserver.repository;

import com.github.seungjae97.alyak.alyakapiserver.domain.family.entity.Family;
import com.github.seungjae97.alyak.alyakapiserver.domain.family.entity.FamilyMember;
import com.github.seungjae97.alyak.alyakapiserver.domain.family.repository.FamilyRepository;
import com.github.seungjae97.alyak.alyakapiserver.domain.medication.entity.MedicationLog;
import com.github.seungjae97.alyak.alyakapiserver.domain.medication.repository.MedicationLogRepository;
import com.github.seungjae97.alyak.alyakapiserver.domain.user.entity.User;
import com.github.seungjae97.alyak.alyakapiserver.domain.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import support.QueryCounter;
import support.RepositoryTestBase;
import support.RepositoryTestFixtures;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class MedicationLogRepositoryTest extends RepositoryTestBase {

    @Autowired private MedicationLogRepository medicationLogRepository;
    @Autowired private UserRepository userRepository;
    @Autowired private FamilyRepository familyRepository;

    private Long userId;
    private Long familyId;
    private Long logId;

    @BeforeEach
    void setUp() {
        User user = userRepository.save(RepositoryTestFixtures.user("med"));
        userId = user.getUserId();

        Family family = familyRepository.save(Family.builder().build());
        familyId = family.getId();
        entityManager.persist(FamilyMember.builder().user(user).family(family).build());

        MedicationLog log = medicationLogRepository.save(RepositoryTestFixtures.medicationLog(user));
        logId = log.getLogId();
        flushAndClear();
    }

    @Test
    @DisplayName("저장 후 ID가 생성되고 조회 시 데이터가 일치한다")
    void save_and_findById() {
        MedicationLog found = medicationLogRepository.findById(logId).orElseThrow();
        assertThat(found.getPillName()).isEqualTo("타이레놀");
        assertThat(found.getUser().getUserId()).isEqualTo(userId);
    }

    @Test
    @DisplayName("존재하지 않는 ID 조회 시 empty를 반환한다")
    void findById_notFound() {
        assertNotFound(medicationLogRepository.findById(999_999L));
    }

    @Test
    @DisplayName("사용자별 복용 로그를 조회한다")
    void findByUser_UserId() {
        assertThat(medicationLogRepository.findByUser_UserId(userId)).hasSize(1);
    }

    @Test
    @DisplayName("사용자별 기간 복용 로그를 조회한다")
    void findByUser_UserIdAndScheduledTimeBetween() {
        LocalDateTime start = LocalDateTime.of(2026, 5, 1, 0, 0);
        LocalDateTime end = LocalDateTime.of(2026, 5, 31, 23, 59);
        assertThat(medicationLogRepository.findByUser_UserIdAndScheduledTimeBetween(userId, start, end))
                .hasSize(1);
    }

    @Test
    @DisplayName("가족 ID로 복용 로그를 조회한다")
    void findByFamilyId() {
        assertThat(medicationLogRepository.findByFamilyId(familyId)).hasSize(1);
    }

    @Test
    @DisplayName("findByUser_UserId 후 User 접근 시 N+1이 발생할 수 있다")
    void findByUser_accessingUser_mayCauseNPlusOne() {
        var logs = medicationLogRepository.findByUser_UserId(userId);
        logs.forEach(log -> log.getUser().getName());

        assertThat(QueryCounter.getCount()).isGreaterThan(1);
    }

    @Test
    @DisplayName("findById는 단일 쿼리로 조회한다")
    void findById_singleQuery() {
        medicationLogRepository.findById(logId);
        assertQueryCount(1);
    }

    @Test
    @DisplayName("로그 삭제 후 조회되지 않는다")
    void deleteLog() {
        medicationLogRepository.deleteById(logId);
        flushAndClear();

        assertThat(medicationLogRepository.findById(logId)).isEmpty();
    }

    @Test
    @DisplayName("pill_name 누락 시 저장에 실패한다")
    void save_withoutPillName_fails() {
        User user = userRepository.findById(userId).orElseThrow();
        assertDataIntegrityViolation(() -> medicationLogRepository.saveAndFlush(
                MedicationLog.builder()
                        .user(user)
                        .dosage(1)
                        .scheduledTime(LocalDateTime.now())
                        .status(com.github.seungjae97.alyak.alyakapiserver.domain.medication.enums.MedicationStatus.TAKEN)
                        .build()
        ));
    }
}
