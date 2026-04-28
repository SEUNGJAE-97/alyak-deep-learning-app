package com.github.seungjae97.alyak.alyakapiserver.config;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.seungjae97.alyak.alyakapiserver.domain.labeling.entity.DataStatus;
import com.github.seungjae97.alyak.alyakapiserver.domain.labeling.entity.PillImageBox;
import com.github.seungjae97.alyak.alyakapiserver.domain.labeling.entity.PillImageData;
import com.github.seungjae97.alyak.alyakapiserver.domain.labeling.repository.PillImageDataRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.InputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;

@Slf4j
@Component
@RequiredArgsConstructor
public class LabelingDummyDataInitializer implements ApplicationRunner {

    private final ObjectMapper objectMapper;
    private final PillImageDataRepository pillImageDataRepository;

    @Value("${app.upload.root-path:./uploads}")
    private String uploadRootPath;

    @Value("${app.dummy-labeling.enabled:true}")
    private boolean dummyLabelingEnabled;

    @Override
    @Transactional
    public void run(ApplicationArguments args) throws Exception {
        if (!dummyLabelingEnabled) {
            return;
        }

        Resource annotationResource = new ClassPathResource("dummy-images/_annotations.coco.json");
        if (!annotationResource.exists()) {
            log.info("dummy labeling annotations file not found, skip initializer");
            return;
        }

        Path uploadDir = Paths.get(uploadRootPath).toAbsolutePath().normalize().resolve("pill-images");
        Files.createDirectories(uploadDir);

        CocoRoot coco;
        try (InputStream inputStream = annotationResource.getInputStream()) {
            coco = objectMapper.readValue(inputStream, CocoRoot.class);
        }
        if (coco == null || coco.images == null || coco.annotations == null) {
            log.warn("dummy labeling annotations content is empty, skip initializer");
            return;
        }

        Map<Long, CocoImage> imageMap = new HashMap<>();
        for (CocoImage image : coco.images) {
            if (image != null && image.id != null && image.fileName != null) {
                imageMap.put(image.id, image);
            }
        }

        Map<Long, List<CocoAnnotation>> annotationsByImageId = new HashMap<>();
        for (CocoAnnotation annotation : coco.annotations) {
            if (annotation == null || annotation.imageId == null || annotation.bbox == null || annotation.bbox.size() < 4) {
                continue;
            }
            annotationsByImageId.computeIfAbsent(annotation.imageId, key -> new ArrayList<>()).add(annotation);
        }

        int createdCount = 0;
        int copiedFileCount = 0;

        for (CocoImage image : coco.images) {
            if (image == null || image.id == null || image.fileName == null || image.width == null || image.height == null) {
                continue;
            }

            List<CocoAnnotation> annotations = annotationsByImageId.getOrDefault(image.id, List.of());
            if (annotations.isEmpty()) {
                continue;
            }

            Resource imageResource = new ClassPathResource("dummy-images/" + image.fileName);
            if (!imageResource.exists()) {
                continue;
            }

            String webPath = "/uploads/pill-images/" + image.fileName;
            if (pillImageDataRepository.existsByImagePath(webPath)) {
                continue;
            }

            Path targetPath = uploadDir.resolve(image.fileName);
            try (InputStream imageStream = imageResource.getInputStream()) {
                Files.copy(imageStream, targetPath, StandardCopyOption.REPLACE_EXISTING);
            }
            copiedFileCount++;

            PillImageData imageData = PillImageData.builder()
                    .imagePath(webPath)
                    .status(DataStatus.INBOX)
                    .build();

            BigDecimal width = BigDecimal.valueOf(image.width);
            BigDecimal height = BigDecimal.valueOf(image.height);
            for (int boxIndex = 0; boxIndex < annotations.size(); boxIndex++) {
                List<Double> bbox = annotations.get(boxIndex).bbox;
                BigDecimal xMin = normalize(bbox.get(0), width);
                BigDecimal yMin = normalize(bbox.get(1), height);
                BigDecimal xMax = normalize(bbox.get(0) + bbox.get(2), width);
                BigDecimal yMax = normalize(bbox.get(1) + bbox.get(3), height);

                imageData.addBox(PillImageBox.builder()
                        .boxIndex(boxIndex)
                        .xMin(xMin)
                        .yMin(yMin)
                        .xMax(xMax)
                        .yMax(yMax)
                        .build());
            }

            pillImageDataRepository.save(imageData);
            createdCount++;
        }

        log.info("dummy labeling initializer completed. createdImages={} copiedFiles={} uploadDir={}", createdCount, copiedFileCount, uploadDir);
    }

    private BigDecimal normalize(Double value, BigDecimal base) {
        if (value == null || base == null || base.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }
        BigDecimal ratio = BigDecimal.valueOf(value).divide(base, 6, RoundingMode.HALF_UP);
        if (ratio.compareTo(BigDecimal.ZERO) < 0) return BigDecimal.ZERO;
        if (ratio.compareTo(BigDecimal.ONE) > 0) return BigDecimal.ONE;
        return ratio;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    private static class CocoRoot {
        public List<CocoImage> images;
        public List<CocoAnnotation> annotations;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    private static class CocoImage {
        public Long id;
        @JsonProperty("file_name")
        public String fileName;
        public Integer width;
        public Integer height;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    private static class CocoAnnotation {
        @JsonProperty("image_id")
        public Long imageId;
        public List<Double> bbox;
    }
}
