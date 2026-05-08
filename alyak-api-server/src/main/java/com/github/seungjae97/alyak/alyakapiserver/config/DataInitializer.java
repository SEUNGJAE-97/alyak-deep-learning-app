package com.github.seungjae97.alyak.alyakapiserver.config;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;

import com.github.seungjae97.alyak.alyakapiserver.domain.pill.entity.Pill;
import com.github.seungjae97.alyak.alyakapiserver.domain.pill.entity.PillAppearance;
import com.github.seungjae97.alyak.alyakapiserver.domain.pill.entity.PillColor;
import com.github.seungjae97.alyak.alyakapiserver.domain.pill.entity.PillShape;
import com.github.seungjae97.alyak.alyakapiserver.domain.pill.repository.*;

import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataInitializer {

    private final PillRepository pillRepository;
    private final PillRepositoryImpl pillRepositoryImpl;
    private final PillAppearanceRepositoryImpl pillAppearanceRepositoryImpl;
    private final PillColorRepository pillColorRepository;
    private final PillShapeRepository pillShapeRepository;

    @PostConstruct
    @Transactional
    public void init() {
        if (pillRepository.count() > 0) return;

        try {
            loadData();
        } catch (Exception e) {
            throw new RuntimeException("데이터 초기화 실패", e);
        }
    }

    private void loadData() throws Exception {
        Resource resource = new ClassPathResource("data/pill_data.csv"); // 정제된 파일명 확인

        Map<String, PillColor> colorCache = new HashMap<>();
        Map<String, PillShape> shapeCache = new HashMap<>();

        List<Pill> pills = new ArrayList<>();
        List<PillAppearance> appearances = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8))) {

            String line;
            boolean isFirstLine = true;

            while ((line = reader.readLine()) != null) {
                if (isFirstLine) { isFirstLine = false; continue; }

                String[] cols = line.split(",", -1);
                if (cols.length < 24) continue;

                Long currentPillId = Long.parseLong(cols[0].trim());

                // 1. Pill 생성 (중복 체크 없이 바로 추가)
                pills.add(Pill.builder()
                        .id(currentPillId)
                        .pillName(cols[1].trim())
                        .pillManufacturer(cols[3].trim())
                        .pillImg(cols[5].trim())
                        .pillDescription(cols[4].trim())
                        .build());

                // 2. 관련 캐시 및 외형 정보 생성
                PillColor color1 = getOrCreateColor(colorCache, nullIfDash(cols[9]));
                PillColor color2 = getOrCreateColor(colorCache, nullIfDash(cols[10]));
                PillShape shape = getOrCreateShape(shapeCache, cols[8].trim());

                appearances.add(PillAppearance.builder()
                        .pillId(currentPillId)
                        .pillFront(nullIfDash(cols[6]))
                        .pillBack(nullIfDash(cols[7]))
                        .pillClassification(cols[4].trim())
                        .pillType(cols[8].trim())
                        .shapeId(shape != null ? shape.getId() : null)
                        .colorClass1Id(color1 != null ? color1.getId() : null)
                        .colorClass2Id(color2 != null ? color2.getId() : null)
                        .lineFront(nullIfDash(cols[11]))
                        .lineBack(nullIfDash(cols[12]))
                        .markCodeFrontAnal(nullIfDash(cols[22]))
                        .markCodeBackAnal(nullIfDash(cols[23]))
                        .build());

                // 3. 1,000건 단위 배치 저장
                if (pills.size() >= 1000) {
                    flushData(colorCache, shapeCache, pills, appearances);
                }
            }

            if (!pills.isEmpty()) {
                flushData(colorCache, shapeCache, pills, appearances);
            }
        }
    }

    private void flushData(Map<String, PillColor> colors, Map<String, PillShape> shapes,
                           List<Pill> pills, List<PillAppearance> apps) {
        pillColorRepository.saveAll(colors.values());
        pillShapeRepository.saveAll(shapes.values());

        pillRepositoryImpl.saveAll(pills);
        pills.clear();

        pillAppearanceRepositoryImpl.saveAll(apps);
        apps.clear();
    }

    private PillColor getOrCreateColor(Map<String, PillColor> cache, String colorName) {
        if (colorName == null) return null;
        return cache.computeIfAbsent(colorName, name -> PillColor.builder()
                .colorName(name)
                .build());
    }

    private PillShape getOrCreateShape(Map<String, PillShape> cache, String shapeName) {
        if (shapeName == null) return null;
        return cache.computeIfAbsent(shapeName, name -> PillShape.builder()
                .shapeName(name)
                .build());
    }

    private String nullIfDash(String value) {
        if (value == null) return null;
        String trimmed = value.trim();
        return trimmed.equals("-") || trimmed.isEmpty() ? null : trimmed;
    }
}