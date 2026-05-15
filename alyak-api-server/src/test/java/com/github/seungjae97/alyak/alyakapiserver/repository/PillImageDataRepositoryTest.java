package com.github.seungjae97.alyak.alyakapiserver.repository;

import com.github.seungjae97.alyak.alyakapiserver.domain.labeling.entity.DataStatus;
import com.github.seungjae97.alyak.alyakapiserver.domain.labeling.entity.PillImageBox;
import com.github.seungjae97.alyak.alyakapiserver.domain.labeling.entity.PillImageData;
import com.github.seungjae97.alyak.alyakapiserver.domain.labeling.repository.PillImageBoxRepository;
import com.github.seungjae97.alyak.alyakapiserver.domain.labeling.repository.PillImageDataRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import support.RepositoryTestBase;
import support.RepositoryTestFixtures;

import static org.assertj.core.api.Assertions.assertThat;

class PillImageDataRepositoryTest extends RepositoryTestBase {

    @Autowired private PillImageDataRepository pillImageDataRepository;
    @Autowired private PillImageBoxRepository pillImageBoxRepository;

    private Long imageId;

    @BeforeEach
    void setUp() {
        PillImageData data = RepositoryTestFixtures.pillImageData("/images/sample.jpg");
        data.addBox(RepositoryTestFixtures.pillImageBox(0));
        PillImageData saved = pillImageDataRepository.save(data);
        imageId = saved.getId();
        flushAndClear();
    }

    @Test
    @DisplayName("저장 후 ID가 생성되고 조회 시 데이터가 일치한다")
    void save_and_findById() {
        PillImageData found = pillImageDataRepository.findById(imageId).orElseThrow();
        assertThat(found.getImagePath()).isEqualTo("/images/sample.jpg");
        assertThat(found.getStatus()).isEqualTo(DataStatus.INBOX);
    }

    @Test
    @DisplayName("존재하지 않는 ID 조회 시 empty를 반환한다")
    void findById_notFound() {
        assertNotFound(pillImageDataRepository.findById(999_999L));
    }

    @Test
    @DisplayName("상태별 페이징 조회한다")
    void findByStatus() {
        var page = pillImageDataRepository.findByStatus(DataStatus.INBOX, PageRequest.of(0, 10));
        assertThat(page.getContent()).hasSize(1);
    }

    @Test
    @DisplayName("image_path 존재 여부를 확인한다")
    void existsByImagePath() {
        assertThat(pillImageDataRepository.existsByImagePath("/images/sample.jpg")).isTrue();
        assertThat(pillImageDataRepository.existsByImagePath("/images/other.jpg")).isFalse();
    }

    @Test
    @DisplayName("부모 삭제 시 CASCADE로 Box가 함께 삭제된다")
    void delete_cascadesToBoxes() {
        pillImageDataRepository.deleteById(imageId);
        flushAndClear();

        assertThat(pillImageBoxRepository.findAll()).isEmpty();
    }

    @Test
    @DisplayName("image_path 누락 시 저장에 실패한다")
    void save_withoutImagePath_fails() {
        assertDataIntegrityViolation(() -> pillImageDataRepository.saveAndFlush(
                PillImageData.builder().status(DataStatus.INBOX).build()
        ));
    }
}
