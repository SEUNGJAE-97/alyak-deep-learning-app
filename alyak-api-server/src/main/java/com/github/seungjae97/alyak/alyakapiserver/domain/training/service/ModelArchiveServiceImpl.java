package com.github.seungjae97.alyak.alyakapiserver.domain.training.service;

import com.github.seungjae97.alyak.alyakapiserver.domain.training.dto.response.ModelArchiveCompareResponse;
import com.github.seungjae97.alyak.alyakapiserver.domain.training.dto.response.ModelArchiveDetailResponse;
import com.github.seungjae97.alyak.alyakapiserver.domain.training.dto.response.ModelArchiveResponse;
import com.github.seungjae97.alyak.alyakapiserver.domain.training.entity.ModelArchive;
import com.github.seungjae97.alyak.alyakapiserver.domain.training.entity.ModelArchiveStatus;
import com.github.seungjae97.alyak.alyakapiserver.domain.training.repository.ModelArchiveRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class ModelArchiveServiceImpl implements ModelArchiveService {

    private static final String ARGS_FILE = "args.yaml";
    private static final String RESULTS_FILE = "results.csv";

    private final ModelArchiveRepository modelArchiveRepository;

    @Override
    public void importFromRootPath(String rootPath) {
        if (rootPath == null || rootPath.isBlank()) return;

        Path root = Paths.get(rootPath).toAbsolutePath().normalize();
        if (!Files.exists(root) || !Files.isDirectory(root)) {
            log.info("archive import root not found. root={}", root);
            return;
        }

        try (Stream<Path> runDirs = Files.list(root).filter(Files::isDirectory)) {
            runDirs.forEach(this::importSingleRunSafely);
        } catch (IOException e) {
            log.warn("failed to scan archive root. root={}", root, e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ModelArchiveResponse> getArchives(ModelArchiveStatus status, Pageable pageable) {
        Page<ModelArchive> page = status == null
                ? modelArchiveRepository.findAll(pageable)
                : modelArchiveRepository.findByStatus(status, pageable);
        return page.map(ModelArchiveResponse::from);
    }

    @Override
    @Transactional(readOnly = true)
    public ModelArchiveDetailResponse getArchive(Long id) {
        ModelArchive archive = modelArchiveRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Archive not found"));
        return ModelArchiveDetailResponse.from(archive);
    }

    @Override
    @Transactional(readOnly = true)
    public ModelArchiveCompareResponse compare(Long baseId, Long targetId) {
        ModelArchive base = modelArchiveRepository.findById(baseId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Base archive not found"));
        ModelArchive target = modelArchiveRepository.findById(targetId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Target archive not found"));

        List<ModelArchiveCompareResponse.MetricDelta> metrics = List.of(
                metric("Accuracy", base.getBestPrecision(), target.getBestPrecision()),
                metric("mAP", base.getBestMap50(), target.getBestMap50()),
                metric("Recall", base.getBestRecall(), target.getBestRecall()),
                metric("Precision", base.getBestPrecision(), target.getBestPrecision())
        );

        return ModelArchiveCompareResponse.builder()
                .baseVersion(base.getVersion())
                .targetVersion(target.getVersion())
                .metrics(metrics)
                .build();
    }

    private void importSingleRunSafely(Path runDir) {
        try {
            importSingleRun(runDir);
        } catch (Exception e) {
            log.warn("skip archive run due to parse error. runDir={}", runDir, e);
        }
    }

    private void importSingleRun(Path runDir) throws IOException {
        Path argsPath = runDir.resolve(ARGS_FILE);
        Path resultsPath = runDir.resolve(RESULTS_FILE);
        if (!Files.exists(argsPath) || !Files.exists(resultsPath)) {
            return;
        }

        if (modelArchiveRepository.findByRunDir(runDir.toString()).isPresent()) {
            return;
        }

        Map<String, String> args = parseArgsYaml(argsPath);
        ResultsBest best = parseResultsCsv(resultsPath);
        String version = resolveVersion(runDir, args);
        Path modelPath = runDir.resolve("weights").resolve("best.pt");

        ModelArchive archive = ModelArchive.builder()
                .version(version)
                .status(ModelArchiveStatus.ARCHIVED)
                .runDir(runDir.toString())
                .modelPath(Files.exists(modelPath) ? modelPath.toString() : null)
                .argsPath(argsPath.toString())
                .resultsPath(resultsPath.toString())
                .datasetName(args.get("data"))
                .epochs(parseInt(args.get("epochs")))
                .batchSize(parseInt(args.get("batch")))
                .learningRate(parseDecimal(args.get("lr0")))
                .optimizer(args.get("optimizer"))
                .freezeLayers(args.get("freeze"))
                .bestMap50(best.map50)
                .bestMap50_95(best.map50_95)
                .bestPrecision(best.precision)
                .bestRecall(best.recall)
                .bestFitness(best.fitness)
                .bestLoss(best.boxLoss)
                .createdAt(resolveCreatedAt(runDir))
                .build();

        modelArchiveRepository.save(archive);
    }

    private ModelArchiveCompareResponse.MetricDelta metric(String name, BigDecimal base, BigDecimal target) {
        return ModelArchiveCompareResponse.MetricDelta.builder()
                .metric(name)
                .base(base)
                .target(target)
                .build();
    }

    private Map<String, String> parseArgsYaml(Path argsPath) throws IOException {
        Map<String, String> map = new HashMap<>();
        for (String line : Files.readAllLines(argsPath)) {
            String trimmed = line == null ? "" : line.trim();
            if (trimmed.isEmpty() || trimmed.startsWith("#") || !trimmed.contains(":")) continue;
            int idx = trimmed.indexOf(':');
            String key = trimmed.substring(0, idx).trim();
            String val = trimmed.substring(idx + 1).trim();
            if (val.startsWith("'") && val.endsWith("'") && val.length() >= 2) {
                val = val.substring(1, val.length() - 1);
            }
            if ("null".equalsIgnoreCase(val)) {
                val = null;
            }
            map.put(key, val);
        }
        return map;
    }

    private ResultsBest parseResultsCsv(Path resultsPath) throws IOException {
        List<String> lines = Files.readAllLines(resultsPath);
        if (lines.size() < 2) return new ResultsBest();

        String[] headers = splitCsvLine(lines.get(0));
        Map<String, Integer> idx = new HashMap<>();
        for (int i = 0; i < headers.length; i++) {
            idx.put(headers[i].trim(), i);
        }

        ResultsBest best = new ResultsBest();
        for (int i = 1; i < lines.size(); i++) {
            String[] row = splitCsvLine(lines.get(i));
            BigDecimal fitness = decimalFromRow(row, idx, "fitness");
            if (best.fitness == null || (fitness != null && fitness.compareTo(best.fitness) > 0)) {
                best.fitness = fitness;
                best.map50 = decimalFromRow(row, idx, "metrics/mAP50(B)");
                best.map50_95 = decimalFromRow(row, idx, "metrics/mAP50-95(B)");
                best.precision = decimalFromRow(row, idx, "metrics/precision(B)");
                best.recall = decimalFromRow(row, idx, "metrics/recall(B)");
                best.boxLoss = decimalFromRow(row, idx, "val/box_loss");
            }
        }
        return best;
    }

    private String[] splitCsvLine(String line) {
        return line == null ? new String[0] : line.split(",");
    }

    private BigDecimal decimalFromRow(String[] row, Map<String, Integer> idx, String key) {
        Integer i = idx.get(key);
        if (i == null || i < 0 || i >= row.length) return null;
        return parseDecimal(row[i]);
    }

    private BigDecimal parseDecimal(String value) {
        if (value == null || value.isBlank()) return null;
        try {
            return new BigDecimal(value.trim());
        } catch (Exception e) {
            return null;
        }
    }

    private Integer parseInt(String value) {
        if (value == null || value.isBlank()) return null;
        try {
            return Integer.parseInt(value.trim());
        } catch (Exception e) {
            return null;
        }
    }

    private String resolveVersion(Path runDir, Map<String, String> args) {
        String name = args.get("name");
        if (name != null && !name.isBlank()) {
            return name;
        }
        return runDir.getFileName() == null ? "unknown-" + System.currentTimeMillis() : runDir.getFileName().toString();
    }

    private LocalDateTime resolveCreatedAt(Path runDir) {
        try {
            return LocalDateTime.ofInstant(Files.getLastModifiedTime(runDir).toInstant(), TimeZone.getDefault().toZoneId());
        } catch (IOException e) {
            return LocalDateTime.now();
        }
    }

    private static class ResultsBest {
        private BigDecimal map50;
        private BigDecimal map50_95;
        private BigDecimal precision;
        private BigDecimal recall;
        private BigDecimal fitness;
        private BigDecimal boxLoss;
    }
}
