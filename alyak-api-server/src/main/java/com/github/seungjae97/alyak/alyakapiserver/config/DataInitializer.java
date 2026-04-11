package com.github.seungjae97.alyak.alyakapiserver.config;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.github.seungjae97.alyak.alyakapiserver.domain.pill.entity.Pill;
import com.github.seungjae97.alyak.alyakapiserver.domain.pill.entity.PillAppearance;
import com.github.seungjae97.alyak.alyakapiserver.domain.pill.entity.PillColor;
import com.github.seungjae97.alyak.alyakapiserver.domain.pill.entity.PillShape;
import com.github.seungjae97.alyak.alyakapiserver.domain.pill.repository.PillAppearanceRepository;
import com.github.seungjae97.alyak.alyakapiserver.domain.pill.repository.PillColorRepository;
import com.github.seungjae97.alyak.alyakapiserver.domain.pill.repository.PillRepository;
import com.github.seungjae97.alyak.alyakapiserver.domain.pill.repository.PillShapeRepository;

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
    private final PillAppearanceRepository pillAppearanceRepository;
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
        Resource resource = new ClassPathResource("data/pill_data.csv");

        Map<String, PillColor> colorCache = new HashMap<>();
        Map<String, PillShape> shapeCache = new HashMap<>();

        List<Pill> pills = new ArrayList<>();
        List<PillAppearance> appearances = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8))) { // 인코딩 확인 필요

            String line;
            boolean isFirstLine = true;

            while ((line = reader.readLine()) != null) {
                if (isFirstLine) { isFirstLine = false; continue; } // 헤더 스킵

                String[] cols = line.split(",", -1);
                if (cols.length < 10) continue;

                String pillId = cols[0].trim();
                String pillName = cols[1].trim();
                String manufacturer = cols[3].trim();
                String description = cols[4].trim();
                String imgUrl = cols[5].trim();
                String pillFront = nullIfDash(cols[6]);
                String pillBack = nullIfDash(cols[7]);
                String pillType = cols[8].trim();
                String colorFront = nullIfDash(cols[9]);
                String colorBack = nullIfDash(cols[10]);
                String lineFront = nullIfDash(cols[11]);
                String lineBack = nullIfDash(cols[12]);
                String markFront = nullIfDash(cols[22]);
                String markBack = nullIfDash(cols[23]);

                PillColor color1 = getOrCreateColor(colorCache, colorFront);
                PillColor color2 = getOrCreateColor(colorCache, colorBack);

                // PillShape 캐시에서 조회 or 생성
                PillShape shape = getOrCreateShape(shapeCache, pillType);

                // Pill 생성
                Pill pill = Pill.builder()
                        .id(Long.parseLong(pillId))
                        .pillName(pillName)
                        .pillManufacturer(manufacturer)
                        .pillImg(imgUrl)
                        .pillDescription(description)
                        .build();
                pills.add(pill);

                // PillAppearance 생성
                PillAppearance appearance = PillAppearance.builder()
                        .pillId(Long.parseLong(pillId))
                        .pillFront(pillFront)
                        .pillBack(pillBack)
                        .pillClassification(description)
                        .pillType(pillType)
                        .shapeId(shape != null ? shape.getId() : null)
                        .colorClass1Id(color1 != null ? color1.getId() : null)
                        .colorClass2Id(color2 != null ? color2.getId() : null)
                        .lineFront(lineFront)
                        .lineBack(lineBack)
                        .markCodeFrontAnal(markFront)
                        .markCodeBackAnal(markBack)
                        .build();
                appearances.add(appearance);

                if (pills.size() >= 1000) {
                    pillColorRepository.saveAll(colorCache.values());
                    pillShapeRepository.saveAll(shapeCache.values());
                    pillRepository.saveAll(pills);
                    pillAppearanceRepository.saveAll(appearances);
                    pills.clear();
                    appearances.clear();
                }
            }

            if (!pills.isEmpty()) {
                pillColorRepository.saveAll(colorCache.values());
                pillShapeRepository.saveAll(shapeCache.values());
                pillRepository.saveAll(pills);
                pillAppearanceRepository.saveAll(appearances);
            }
        }
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