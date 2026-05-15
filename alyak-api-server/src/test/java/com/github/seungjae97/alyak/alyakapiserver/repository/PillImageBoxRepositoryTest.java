package com.github.seungjae97.alyak.alyakapiserver.repository;

import com.github.seungjae97.alyak.alyakapiserver.domain.labeling.entity.PillImageData;
import com.github.seungjae97.alyak.alyakapiserver.domain.labeling.repository.PillImageBoxRepository;
import com.github.seungjae97.alyak.alyakapiserver.domain.labeling.repository.PillImageDataRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import support.RepositoryTestBase;
import support.RepositoryTestFixtures;

import static org.assertj.core.api.Assertions.assertThat;

class PillImageBoxRepositoryTest extends RepositoryTestBase {

    @Autowired private PillImageBoxRepository pillImageBoxRepository;
    @Autowired private PillImageDataRepository pillImageDataRepository;

    private Long imageId;

    @BeforeEach
    void setUp() {
        PillImageData data = RepositoryTestFixtures.pillImageData("/images/box-test.jpg");
        data.addBox(RepositoryTestFixtures.pillImageBox(0));
        data.addBox(RepositoryTestFixtures.pillImageBox(1));
        imageId = pillImageDataRepository.save(data).getId();
        flushAndClear();
    }

    @Test
    @DisplayName("저장 후 ID가 생성되고 조회 시 데이터가 일치한다")
    void save_and_findById() {
        var boxes = pillImageBoxRepository.findAll();
        assertThat(boxes).hasSize(2);
        assertThat(boxes.get(0).getId()).isNotNull();
    }

    @Test
    @DisplayName("존재하지 않는 ID 조회 시 empty를 반환한다")
    void findById_notFound() {
        assertNotFound(pillImageBoxRepository.findById(999_999L));
    }

    @Test
    @DisplayName("image_id 기준으로 Box를 일괄 삭제한다")
    void deleteAllByImageDataId() {
        pillImageBoxRepository.deleteAllByImageDataId(imageId);
        flushAndClear();

        assertThat(pillImageBoxRepository.findAll()).isEmpty();
        assertThat(pillImageDataRepository.findById(imageId)).isPresent();
    }

    @Test
    @DisplayName("imageData 누락 시 저장에 실패한다")
    void save_withoutImageData_fails() {
        assertDataIntegrityViolation(() -> pillImageBoxRepository.saveAndFlush(
                RepositoryTestFixtures.pillImageBox(2)
        ));
    }
}
