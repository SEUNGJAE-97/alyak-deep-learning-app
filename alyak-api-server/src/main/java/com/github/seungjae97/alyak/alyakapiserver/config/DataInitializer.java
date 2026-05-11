package com.github.seungjae97.alyak.alyakapiserver.config;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

import com.github.seungjae97.alyak.alyakapiserver.domain.pill.entity.*;
import com.github.seungjae97.alyak.alyakapiserver.domain.pill.repository.*;

import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

@Slf4j
@Component
//@RequiredArgsConstructor
public class DataInitializer {
//    private final AtomicLong totalWaitTime = new AtomicLong(0);
//    private final AtomicLong totalCpuTime = new AtomicLong(0);
    private final Executor dbExecutor;
    private final PillRepository pillRepository;
    private final PillRepositoryImpl pillRepositoryImpl;
    private final PillAppearanceRepositoryImpl pillAppearanceRepositoryImpl;
    private final PillColorRepository pillColorRepository;
    private final PillShapeRepository pillShapeRepository;
    private final AtomicInteger batchCounter = new AtomicInteger(0);
    public DataInitializer(@Qualifier("dbExecutor") Executor dbExecutor,
                           PillRepository pillRepository,
                           PillRepositoryImpl pillRepositoryImpl,
                           PillAppearanceRepositoryImpl pillAppearanceRepositoryImpl,
                           PillColorRepository pillColorRepository,
                           PillShapeRepository pillShapeRepository) {
        this.dbExecutor = dbExecutor;
        this.pillRepository = pillRepository;
        this.pillRepositoryImpl = pillRepositoryImpl;
        this.pillAppearanceRepositoryImpl = pillAppearanceRepositoryImpl;
        this.pillColorRepository = pillColorRepository;
        this.pillShapeRepository = pillShapeRepository;
    }

    @PostConstruct
    @Transactional
    public void init() {
        if (pillRepository.count() > 0) return;
        Map<String, PillColor> colorCache = pillColorRepository.findAll()
                .stream()
                .collect(Collectors.toMap(PillColor::getColorName, c -> c));

        Map<String, PillShape> shapeCache = pillShapeRepository.findAll()
                .stream()
                .collect(Collectors.toMap(PillShape::getShapeName, s -> s));
        try {
            loadData(colorCache, shapeCache);
        } catch (Exception e) {
            throw new RuntimeException("데이터 초기화 실패", e);
        }
    }

    private void loadData(Map<String, PillColor> colorCache, Map<String, PillShape> shapeCache) throws Exception {
        Resource resource = new ClassPathResource("data/pill_data.csv");
//        long start = System.currentTimeMillis();
        List<Pill> pills = new ArrayList<>();
        List<PillAppearance> appearances = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8))) {

            String line;
            boolean isFirstLine = true;

            while ((line = reader.readLine()) != null) {
                if (isFirstLine) {
                    isFirstLine = false;
                    continue;
                }

                String[] cols = line.split(",", -1);
                if (cols.length < 24) continue;

                Long currentPillId = Long.parseLong(cols[0].trim());

                // 1. Pill 생성
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
                    flushData(pills, appearances);
                }
            }

            if (!pills.isEmpty()) {
                flushData(pills, appearances);
            }
//            CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
//            log.info("모든 배치 DB 저장 완료");
//            log.info("누적 큐 대기 시간: {}ms", totalWaitTime.get());
//            log.info("누적 실행 시간: {}ms", totalCpuTime.get());
//            log.info("전체 완료: {}ms", System.currentTimeMillis() - start);
        }
    }

    private final List<CompletableFuture<Void>> futures = new ArrayList<>();

    private void flushData(List<Pill> pills, List<PillAppearance> apps) {
        List<Pill> pillsCopy = new ArrayList<>(pills);
        List<PillAppearance> appsCopy = new ArrayList<>(apps);
        pills.clear();
        apps.clear();

        long submitTime = System.currentTimeMillis();
        int batchNum = batchCounter.incrementAndGet();

        CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
//            long waitTime = System.currentTimeMillis() - submitTime;
//            totalWaitTime.addAndGet(waitTime);
//            long startTime = System.currentTimeMillis();
//            log.info("[Batch-{}] 큐 대기 시간: {}ms | 스레드: {}", batchNum, waitTime, Thread.currentThread().getName());

            try {
//                long t1 = System.currentTimeMillis();
                pillRepositoryImpl.saveAll(pillsCopy);
//                log.info("[Batch-{}] pill saveAll: {}ms", batchNum, System.currentTimeMillis() - t1);

//                long t2 = System.currentTimeMillis();
                pillAppearanceRepositoryImpl.saveAll(appsCopy);
//                log.info("[Batch-{}] appearance saveAll: {}ms", batchNum, System.currentTimeMillis() - t2);
//                long execTime = System.currentTimeMillis() - startTime;
//                totalCpuTime.addAndGet(execTime);
//                log.info("[Batch-{}] 총 실행 시간: {}ms", batchNum, execTime);
            } catch (Exception e) {
                log.error("[Batch-{}] 실패", batchNum, e);
            }
        }, dbExecutor);

        futures.add(future);
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