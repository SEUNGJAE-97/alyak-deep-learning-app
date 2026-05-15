package com.github.seungjae97.alyak.alyakapiserver.repository;

import com.github.seungjae97.alyak.alyakapiserver.domain.family.entity.Family;
import com.github.seungjae97.alyak.alyakapiserver.domain.family.entity.FamilyMember;
import com.github.seungjae97.alyak.alyakapiserver.domain.family.repository.FamilyRepository;
import com.github.seungjae97.alyak.alyakapiserver.domain.schedule.entity.ScheduleBackup;
import com.github.seungjae97.alyak.alyakapiserver.domain.schedule.repository.ScheduleBackupRepository;
import com.github.seungjae97.alyak.alyakapiserver.domain.user.entity.User;
import com.github.seungjae97.alyak.alyakapiserver.domain.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import support.QueryCounter;
import support.RepositoryTestBase;
import support.RepositoryTestFixtures;

import static org.assertj.core.api.Assertions.assertThat;

class ScheduleBackupRepositoryTest extends RepositoryTestBase {

    @Autowired private ScheduleBackupRepository scheduleBackupRepository;
    @Autowired private UserRepository userRepository;
    @Autowired private FamilyRepository familyRepository;

    private Long userId;
    private Long familyId;
    private Long scheduleId;

    @BeforeEach
    void setUp() {
        User user = userRepository.save(RepositoryTestFixtures.user("schedule"));
        userId = user.getUserId();

        Family family = familyRepository.save(Family.builder().build());
        familyId = family.getId();
        entityManager.persist(FamilyMember.builder().user(user).family(family).build());

        ScheduleBackup backup = scheduleBackupRepository.save(
                RepositoryTestFixtures.scheduleBackup(user, "타이레놀"));
        scheduleId = backup.getScheduleId();
        flushAndClear();
    }

    @Test
    @DisplayName("저장 후 ID가 생성되고 조회 시 데이터가 일치한다")
    void save_and_findById() {
        ScheduleBackup found = scheduleBackupRepository.findById(scheduleId).orElseThrow();
        assertThat(found.getPillName()).isEqualTo("타이레놀");
        assertThat(found.getUser().getUserId()).isEqualTo(userId);
    }

    @Test
    @DisplayName("존재하지 않는 ID 조회 시 empty를 반환한다")
    void findById_notFound() {
        assertNotFound(scheduleBackupRepository.findById(999_999L));
    }

    @Test
    @DisplayName("사용자별 최신 순으로 백업 목록을 조회한다")
    void findByUser_UserIdOrderByCreatedAtDesc() {
        assertThat(scheduleBackupRepository.findByUser_UserIdOrderByCreatedAtDesc(userId)).hasSize(1);
    }

    @Test
    @DisplayName("scheduleId와 userId로 백업을 조회한다")
    void findByScheduleIdAndUser_UserId() {
        assertThat(scheduleBackupRepository.findByScheduleIdAndUser_UserId(scheduleId, userId))
                .isPresent();
    }

    @Test
    @DisplayName("QueryDSL - 가족 ID로 백업 목록을 조회한다")
    void findBackupsByFamilyId() {
        assertThat(scheduleBackupRepository.findBackupsByFamilyId(familyId)).hasSize(1);
    }

    @Test
    @DisplayName("findByUser_UserIdOrderByCreatedAtDesc 후 User 접근 시 N+1이 발생할 수 있다")
    void findByUser_accessingUser_mayCauseNPlusOne() {
        var backups = scheduleBackupRepository.findByUser_UserIdOrderByCreatedAtDesc(userId);
        backups.forEach(b -> b.getUser().getName());

        assertThat(QueryCounter.getCount()).isGreaterThan(1);
    }

    @Test
    @DisplayName("pill_name 누락 시 저장에 실패한다")
    void save_withoutPillName_fails() {
        User user = userRepository.findById(userId).orElseThrow();
        assertDataIntegrityViolation(() -> scheduleBackupRepository.saveAndFlush(
                ScheduleBackup.builder()
                        .user(user)
                        .dosage(1)
                        .scheduledTime(java.time.LocalTime.of(9, 0))
                        .startDate(java.time.LocalDate.of(2026, 1, 1))
                        .endDate(java.time.LocalDate.of(2026, 12, 31))
                        .build()
        ));
    }
}
