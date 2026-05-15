package com.github.seungjae97.alyak.alyakapiserver.repository;

import com.github.seungjae97.alyak.alyakapiserver.domain.training.entity.ModelArchive;
import com.github.seungjae97.alyak.alyakapiserver.domain.training.entity.ModelArchiveStatus;
import com.github.seungjae97.alyak.alyakapiserver.domain.training.repository.ModelArchiveRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import support.QueryCounter;
import support.RepositoryTestBase;
import support.RepositoryTestFixtures;

import static org.assertj.core.api.Assertions.assertThat;

class ModelArchiveRepositoryTest extends RepositoryTestBase {

    @Autowired private ModelArchiveRepository modelArchiveRepository;

    private Long archiveId;
    private String version;

    @BeforeEach
    void setUp() {
        version = "v1.0.0";
        ModelArchive archive = modelArchiveRepository.save(RepositoryTestFixtures.modelArchive(version));
        archiveId = archive.getId();
        flushAndClear();
    }

    @Test
    @DisplayName("저장 후 ID가 생성되고 조회 시 데이터가 일치한다")
    void save_and_findById() {
        ModelArchive found = modelArchiveRepository.findById(archiveId).orElseThrow();
        assertThat(found.getVersion()).isEqualTo(version);
    }

    @Test
    @DisplayName("존재하지 않는 ID 조회 시 empty를 반환한다")
    void findById_notFound() {
        assertNotFound(modelArchiveRepository.findById(999_999L));
    }

    @Test
    @DisplayName("run_dir로 조회한다")
    void findByRunDir() {
        assertThat(modelArchiveRepository.findByRunDir("/runs/" + version)).isPresent();
    }

    @Test
    @DisplayName("version으로 조회한다")
    void findByVersion() {
        assertThat(modelArchiveRepository.findByVersion(version)).isPresent();
    }

    @Test
    @DisplayName("상태별 페이징 조회한다")
    void findByStatus() {
        var page = modelArchiveRepository.findByStatus(ModelArchiveStatus.ARCHIVED, PageRequest.of(0, 10));
        assertThat(page.getContent()).hasSize(1);
    }

    @Test
    @DisplayName("findById는 단일 쿼리로 조회한다")
    void findById_singleQuery() {
        modelArchiveRepository.findById(archiveId);
        assertQueryCount(1);
    }

    @Test
    @DisplayName("version 누락 시 저장에 실패한다")
    void save_withoutVersion_fails() {
        assertDataIntegrityViolation(() -> modelArchiveRepository.saveAndFlush(
                ModelArchive.builder()
                        .runDir("/runs/missing-version")
                        .argsPath("/args")
                        .resultsPath("/results")
                        .build()
        ));
    }
}
