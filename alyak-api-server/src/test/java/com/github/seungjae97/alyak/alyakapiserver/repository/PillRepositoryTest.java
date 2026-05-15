package com.github.seungjae97.alyak.alyakapiserver.repository;

import com.github.seungjae97.alyak.alyakapiserver.domain.pill.dto.request.PillSearchRequest;
import com.github.seungjae97.alyak.alyakapiserver.domain.pill.entity.Pill;
import com.github.seungjae97.alyak.alyakapiserver.domain.pill.entity.PillAppearance;
import com.github.seungjae97.alyak.alyakapiserver.domain.pill.entity.PillColor;
import com.github.seungjae97.alyak.alyakapiserver.domain.pill.entity.PillShape;
import com.github.seungjae97.alyak.alyakapiserver.domain.pill.repository.PillAppearanceRepository;
import com.github.seungjae97.alyak.alyakapiserver.domain.pill.repository.PillColorRepository;
import com.github.seungjae97.alyak.alyakapiserver.domain.pill.repository.PillRepository;
import com.github.seungjae97.alyak.alyakapiserver.domain.pill.repository.PillShapeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import support.QueryCounter;
import support.RepositoryTestBase;
import support.RepositoryTestFixtures;

import static org.assertj.core.api.Assertions.assertThat;

class PillRepositoryTest extends RepositoryTestBase {

    @Autowired private PillRepository pillRepository;
    @Autowired private PillAppearanceRepository pillAppearanceRepository;
    @Autowired private PillShapeRepository pillShapeRepository;
    @Autowired private PillColorRepository pillColorRepository;

    private static final long PILL_ID = 1L;

    @BeforeEach
    void setUp() {
        pillRepository.save(RepositoryTestFixtures.pill(PILL_ID, "타이레놀"));
        flushAndClear();
    }

    private void seedAppearanceForQueryTests() {
        if (pillAppearanceRepository.findById(PILL_ID).isPresent()) {
            return;
        }
        PillShape shape = pillShapeRepository.save(PillShape.builder().shapeName("원형").build());
        PillColor color = pillColorRepository.save(PillColor.builder().colorName("하양").build());
        Pill pill = pillRepository.findById(PILL_ID).orElseThrow();

        PillAppearance appearance = PillAppearance.builder()
                .pillId(PILL_ID)
                .shapeId(shape.getId())
                .colorClass1Id(color.getId())
                .pillType("TABLET")
                .pillClassification("해열")
                .build();
        entityManager.persist(appearance);
        entityManager.flush();
        entityManager.refresh(pill);
    }

    @Test
    @DisplayName("저장 후 ID로 조회 시 데이터가 일치한다")
    void save_and_findById() {
        Pill found = pillRepository.findById(PILL_ID).orElseThrow();
        assertThat(found.getPillName()).isEqualTo("타이레놀");
    }

    @Test
    @DisplayName("존재하지 않는 ID 조회 시 empty를 반환한다")
    void findById_notFound() {
        assertNotFound(pillRepository.findById(999_999L));
    }

    @Test
    @DisplayName("약 이름으로 조회한다")
    void findByPillName() {
        assertThat(pillRepository.findByPillName("타이레놀")).hasSize(1);
    }

    @Test
    @DisplayName("약 이름 포함 검색(페이징)한다")
    void findByPillNameContaining() {
        var page = pillRepository.findByPillNameContaining("타이", PageRequest.of(0, 10));
        assertThat(page.getContent()).hasSize(1);
    }

    @Test
    @DisplayName("QueryDSL - 약 이름으로 타입 포함 검색한다")
    void findByPillNameWithType() {
        seedAppearanceForQueryTests();
        flushAndClear();
        QueryCounter.reset();

        assertThat(pillRepository.findByPillNameWithType("타이")).isNotEmpty();
    }

    @Test
    @DisplayName("QueryDSL - 외형 조건으로 검색한다")
    void searchAppearance() {
        seedAppearanceForQueryTests();
        flushAndClear();
        QueryCounter.reset();

        assertThat(pillAppearanceRepository.findById(PILL_ID)).isPresent();

        PillSearchRequest request = new PillSearchRequest();
        request.setShape("원형");
        var result = pillRepository.searchAppearance(request);
        assertThat(result).isNotEmpty();
    }

    @Test
    @DisplayName("QueryDSL - 상세 조회한다")
    void detailPill() {
        seedAppearanceForQueryTests();
        flushAndClear();
        QueryCounter.reset();

        assertThat(pillRepository.detailPill(PILL_ID)).isPresent();
    }

    @Test
    @DisplayName("findByPillName은 단일 쿼리로 조회한다")
    void findByPillName_singleQuery() {
        pillRepository.findByPillName("타이레놀");
        assertQueryCount(1);
    }
}
