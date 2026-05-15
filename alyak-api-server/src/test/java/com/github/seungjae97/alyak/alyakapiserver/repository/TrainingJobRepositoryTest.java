package com.github.seungjae97.alyak.alyakapiserver.repository;

import com.github.seungjae97.alyak.alyakapiserver.domain.training.entity.TrainingJob;
import com.github.seungjae97.alyak.alyakapiserver.domain.training.entity.TrainingJobStatus;
import com.github.seungjae97.alyak.alyakapiserver.domain.training.repository.TrainingJobRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import support.QueryCounter;
import support.RepositoryTestBase;
import support.RepositoryTestFixtures;

import static org.assertj.core.api.Assertions.assertThat;

class TrainingJobRepositoryTest extends RepositoryTestBase {

    @Autowired private TrainingJobRepository trainingJobRepository;

    private Long jobId;
    private String externalJobId;

    @BeforeEach
    void setUp() {
        externalJobId = "ext-job-1";
        TrainingJob job = trainingJobRepository.save(TrainingJob.builder()
                .datasetFilter("all")
                .paramsJson("{}")
                .externalJobId(externalJobId)
                .status(TrainingJobStatus.RUNNING)
                .build());
        jobId = job.getId();
        flushAndClear();
    }

    @Test
    @DisplayName("저장 후 ID가 생성되고 조회 시 데이터가 일치한다")
    void save_and_findById() {
        TrainingJob found = trainingJobRepository.findById(jobId).orElseThrow();
        assertThat(found.getExternalJobId()).isEqualTo(externalJobId);
        assertThat(found.getStatus()).isEqualTo(TrainingJobStatus.RUNNING);
    }

    @Test
    @DisplayName("존재하지 않는 ID 조회 시 empty를 반환한다")
    void findById_notFound() {
        assertNotFound(trainingJobRepository.findById(999_999L));
    }

    @Test
    @DisplayName("상태로 작업 목록을 조회한다")
    void findByStatus() {
        assertThat(trainingJobRepository.findByStatus(TrainingJobStatus.RUNNING)).hasSize(1);
    }

    @Test
    @DisplayName("외부 작업 ID로 조회한다")
    void findByExternalJobId() {
        assertThat(trainingJobRepository.findByExternalJobId(externalJobId)).isPresent();
    }

    @Test
    @DisplayName("findById는 단일 쿼리로 조회한다")
    void findById_singleQuery() {
        trainingJobRepository.findById(jobId);
        assertQueryCount(1);
    }

    @Test
    @DisplayName("dataset_filter 누락 시 저장에 실패한다")
    void save_withoutRequiredField_fails() {
        assertDataIntegrityViolation(() -> trainingJobRepository.saveAndFlush(
                TrainingJob.builder().paramsJson("{}").build()
        ));
    }
}
