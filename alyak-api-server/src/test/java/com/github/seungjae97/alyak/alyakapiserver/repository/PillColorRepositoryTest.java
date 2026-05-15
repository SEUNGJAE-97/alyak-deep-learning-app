package com.github.seungjae97.alyak.alyakapiserver.repository;

import com.github.seungjae97.alyak.alyakapiserver.domain.pill.entity.PillColor;
import com.github.seungjae97.alyak.alyakapiserver.domain.pill.repository.PillColorRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import support.QueryCounter;
import support.RepositoryTestBase;

import static org.assertj.core.api.Assertions.assertThat;

class PillColorRepositoryTest extends RepositoryTestBase {

    @Autowired private PillColorRepository pillColorRepository;

    @Test
    @DisplayName("저장 후 ID가 생성되고 조회 시 데이터가 일치한다")
    void save_and_findById() {
        PillColor saved = pillColorRepository.save(PillColor.builder().colorName("빨강").build());
        flushAndClear();
        QueryCounter.reset();

        PillColor found = pillColorRepository.findById(saved.getId()).orElseThrow();

        assertThat(found.getId()).isNotNull();
        assertThat(found.getColorName()).isEqualTo("빨강");
        assertQueryCount(1);
    }

    @Test
    @DisplayName("존재하지 않는 ID 조회 시 empty를 반환한다")
    void findById_notFound() {
        assertNotFound(pillColorRepository.findById(999_999L));
    }

    @Test
    @DisplayName("color_name으로 조회한다")
    void findByColorName() {
        pillColorRepository.save(PillColor.builder().colorName("파랑").build());
        flushAndClear();

        assertThat(pillColorRepository.findByColorName("파랑")).isPresent();
    }
}
