package com.github.seungjae97.alyak.alyakapiserver.repository;

import com.github.seungjae97.alyak.alyakapiserver.domain.pill.entity.PillShape;
import com.github.seungjae97.alyak.alyakapiserver.domain.pill.repository.PillShapeRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import support.QueryCounter;
import support.RepositoryTestBase;

import static org.assertj.core.api.Assertions.assertThat;

class PillShapeRepositoryTest extends RepositoryTestBase {

    @Autowired private PillShapeRepository pillShapeRepository;

    @Test
    @DisplayName("저장 후 ID가 생성되고 조회 시 데이터가 일치한다")
    void save_and_findById() {
        PillShape saved = pillShapeRepository.save(PillShape.builder().shapeName("타원형").build());
        flushAndClear();
        QueryCounter.reset();

        PillShape found = pillShapeRepository.findById(saved.getId()).orElseThrow();

        assertThat(found.getId()).isNotNull();
        assertThat(found.getShapeName()).isEqualTo("타원형");
        assertQueryCount(1);
    }

    @Test
    @DisplayName("존재하지 않는 ID 조회 시 empty를 반환한다")
    void findById_notFound() {
        assertNotFound(pillShapeRepository.findById(999_999L));
    }

    @Test
    @DisplayName("shape_name으로 조회한다")
    void findByShapeName() {
        pillShapeRepository.save(PillShape.builder().shapeName("삼각형").build());
        flushAndClear();

        assertThat(pillShapeRepository.findByShapeName("삼각형")).isPresent();
    }
}
