package com.github.seungjae97.alyak.alyakapiserver.repository;

import com.github.seungjae97.alyak.alyakapiserver.domain.pill.entity.Pill;
import com.github.seungjae97.alyak.alyakapiserver.domain.pill.entity.PillAppearance;
import com.github.seungjae97.alyak.alyakapiserver.domain.pill.repository.PillAppearanceRepository;
import com.github.seungjae97.alyak.alyakapiserver.domain.pill.repository.PillRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import support.QueryCounter;
import support.RepositoryTestBase;
import support.RepositoryTestFixtures;

import static org.assertj.core.api.Assertions.assertThat;

class PillAppearanceRepositoryTest extends RepositoryTestBase {

    @Autowired private PillAppearanceRepository pillAppearanceRepository;
    @Autowired private PillRepository pillRepository;

    private Long pillId;

    @BeforeEach
    void setUp() {
        pillId = 100L;
        pillRepository.save(RepositoryTestFixtures.pill(pillId, "게보린"));
        entityManager.persist(PillAppearance.builder()
                .pillId(pillId)
                .pillType("TABLET")
                .build());
        entityManager.flush();
        flushAndClear();
    }

    @Test
    @DisplayName("저장 후 pill_id로 조회 시 데이터가 일치한다")
    void save_and_findById() {
        PillAppearance found = pillAppearanceRepository.findById(pillId).orElseThrow();
        assertThat(found.getPillId()).isEqualTo(pillId);
        assertThat(found.getPillType()).isEqualTo("TABLET");
    }

    @Test
    @DisplayName("존재하지 않는 ID 조회 시 empty를 반환한다")
    void findById_notFound() {
        assertNotFound(pillAppearanceRepository.findById(999_999L));
    }

    @Test
    @DisplayName("findAll 후 연관 Pill 접근 시 N+1이 발생할 수 있다")
    void findAll_accessingPill_mayCauseNPlusOne() {
        var appearances = pillAppearanceRepository.findAll();
        appearances.forEach(a -> a.getPill().getPillName());

        assertThat(QueryCounter.getCount()).isGreaterThan(1);
    }

    @Test
    @DisplayName("Appearance 삭제 후 조회되지 않는다")
    void deleteAppearance() {
        pillAppearanceRepository.deleteById(pillId);
        flushAndClear();

        assertThat(pillAppearanceRepository.findById(pillId)).isEmpty();
    }
}
