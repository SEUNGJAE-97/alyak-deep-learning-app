package com.github.seungjae97.alyak.alyakapiserver.domain.pill.service;

import com.github.seungjae97.alyak.alyakapiserver.domain.pill.dto.request.PillSearchRequest;
import com.github.seungjae97.alyak.alyakapiserver.domain.pill.dto.request.RecognizeBoxRequest;
import com.github.seungjae97.alyak.alyakapiserver.domain.pill.dto.response.*;
import com.github.seungjae97.alyak.alyakapiserver.domain.labeling.entity.DataStatus;
import com.github.seungjae97.alyak.alyakapiserver.domain.labeling.entity.PillImageBox;
import com.github.seungjae97.alyak.alyakapiserver.domain.labeling.entity.PillImageData;
import com.github.seungjae97.alyak.alyakapiserver.domain.labeling.repository.PillImageDataRepository;
import com.github.seungjae97.alyak.alyakapiserver.domain.pill.entity.Pill;
import com.github.seungjae97.alyak.alyakapiserver.domain.pill.entity.PillAppearance;
import com.github.seungjae97.alyak.alyakapiserver.domain.pill.entity.PillColor;
import com.github.seungjae97.alyak.alyakapiserver.domain.pill.entity.PillShape;
import com.github.seungjae97.alyak.alyakapiserver.domain.pill.repository.PillAppearanceRepository;
import com.github.seungjae97.alyak.alyakapiserver.domain.pill.repository.PillColorRepository;
import com.github.seungjae97.alyak.alyakapiserver.domain.pill.repository.PillRepository;
import com.github.seungjae97.alyak.alyakapiserver.domain.pill.repository.PillShapeRepository;
import com.github.seungjae97.alyak.alyakapiserver.global.common.exception.BusinessError;
import com.github.seungjae97.alyak.alyakapiserver.global.common.exception.BusinessException;
import com.github.seungjae97.alyak.alyakapiserver.global.http.service.RestTemplateService;
import com.github.seungjae97.alyak.alyakapiserver.global.util.HangulUtils;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;
import redis.clients.jedis.JedisPooled;
import redis.clients.jedis.search.FTSearchParams;
import redis.clients.jedis.search.SearchResult;

import java.time.Duration;
import java.time.Instant;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Slf4j
public class PillServiceImpl implements PillService {

    private final PillRepository pillRepository;
    private final PillAppearanceRepository pillAppearanceRepository;
    private final PillColorRepository pillColorRepository;
    private final PillShapeRepository pillShapeRepository;
    private final RestTemplateService restTemplateService;
    private final PillImageDataRepository pillImageDataRepository;

    @Value("${OCR_SERVER_URL}")
    private String ocrServerUrl;

    @Qualifier("pillIdentifyTaskScheduler")
    private final TaskScheduler pillIdentifyTaskScheduler;

    private final StringRedisTemplate redisTemplate;
    private final JedisPooled jedis;
    private static final String AUTOCOMPLETE_KEY = "autocomplete";
    private static final Duration IDENTIFY_API_DELAY = Duration.ofSeconds(5);
    private static final Pattern ALERT_SPLIT_PATTERN = Pattern.compile("[.!?]\\s*\\n*");
    private static final WebClient webClient = WebClient.builder().baseUrl("").build();
    private static final Map<String, String> CAUTION_KEYWORD_MAP;

    static {
        Map<String, String> map = new HashMap<>();
        map.put("임부 또는 수유부", "임산부/수유부");
        map.put("고령자\\(노인\\)", "노인"); // 정규식 패턴을 위해 괄호 이스케이프
        map.put("만 \\d+세 미만 소아", "어린이/소아"); // '만 2세 미만 소아' 등
        map.put("간장애", "간장애 환자");
        map.put("신장\\(콩팥\\)장애", "신장장애 환자");
        map.put("심장기능", "심장질환 환자");
        map.put("혈액이상", "혈액이상 환자");
        map.put("천식", "천식 환자"); // 아스피린 천식, 기관지 천식 포함
        map.put("글루타치온", "글루타치온 부족");
        CAUTION_KEYWORD_MAP = Collections.unmodifiableMap(map);
    }

    private static final Map<String, String> EFFICACY_KEYWORD_MAP;

    static {
        Map<String, String> map = new HashMap<>();
        map.put("발열", "해열");
        map.put("동통", "진통");
        map.put("두통", "두통");
        map.put("근육통", "근육통");
        map.put("관절통", "관절/류마티스 통증");
        map.put("월경통", "생리통");
        map.put("월경 전 긴장증", "월경전증후군");
        map.put("감기의 제증상", "감기 완화");
        map.put("콧물", "코감기 증상");
        map.put("코막힘", "코감기 증상");
        map.put("인후", "목 통증"); // 인후(목구멍)통
        map.put("오한", "오한");
        EFFICACY_KEYWORD_MAP = Collections.unmodifiableMap(map);
    }

    @Value("${OPEN_DATA_KEY}")
    private String serviceKey;

    @Value("${app.upload.root-path:./uploads}")
    private String uploadRootPath;

    @Override
    public List<SimplePillInfo> findPill(String pillName) {
        String url = UriComponentsBuilder.fromUriString("https://apis.data.go.kr/1471000/DrbEasyDrugInfoService/getDrbEasyDrugList")
                .queryParam("serviceKey", serviceKey)
                .queryParam("pageNo", 1)
                .queryParam("numOfRows", 10)
                .queryParam("itemName", pillName)
                .queryParam("type", "json")
                .build().toUriString();

        List<Pill> pillList = pillRepository.findByPillName(pillName);
        if (pillList.isEmpty()) {
            // 0. 외부 API를 조회한다.
            PillInfoResponse pillInfoResponse = restTemplateService.getForObject(url, PillInfoResponse.class, Map.of("param", pillName));

            // 1. 만약 외부 API 조회결과가 없다면?
            if (pillInfoResponse == null || pillInfoResponse.getBody() == null || pillInfoResponse.getBody().getItems() == null
                    || pillInfoResponse.getBody().getItems().isEmpty()) {
                throw new BusinessException(BusinessError.DONT_EXIST_PILL);
            }

            // 1.5 식별 정보 조회 api 호출
            List<PillInfoResponse.Item> items = pillInfoResponse.getBody().getItems();
            List<Long> itemSeqs = items.stream()
                    .map(PillInfoResponse.Item::getItemSeq)
                    .toList();
            Set<Long> existingIds = new HashSet<>(
                    pillAppearanceRepository.findAllById(itemSeqs).stream()
                            .map(PillAppearance::getPillId)
                            .toList()
            );
            List<Long> missingIds = itemSeqs.stream()
                    .filter(id -> !existingIds.contains(id))
                    .toList();
            scheduleIdentifyApiCalls(missingIds);

            // 2. 만약 외부 API 조회결과가 존재한다면? 새 알약을 DB에 저장
            List<Pill> pills = new ArrayList<>();
            for (PillInfoResponse.Item item : pillInfoResponse.getBody().getItems()) {
                List<String> efficacyTags = extractEfficacyTags(item.getEfcyQesitm());
                List<String> specialCautionTags = extractSpecialCautionTags(item.getAtpnQesitm());
                List<String> alertItems = parseAlertItems(item.getAtpnWarnQesitm(), item.getIntrcQesitm());

                Pill pill = Pill.builder()
                        .id(item.getItemSeq())
                        .pillName(item.getItemName())
                        .pillAdverseReaction(item.getSeQesitm())
                        .pillCaution(item.getAtpnQesitm())
                        .pillDescription(item.getEfcyQesitm())
                        .pillEfficacy(item.getEfcyQesitm())
                        .pillImg(item.getItemImage())
                        .pillInteractive(item.getIntrcQesitm())
                        .pillManufacturer(item.getEntpName())
                        .pillWarn(item.getAtpnWarnQesitm())
                        .userMethod(item.getUseMethodQesitm())
                        .build();
                pills.add(pill);
            }

            pillRepository.saveAll(pills);
        }
        // 2.1 사용자에게 알약 정보 전달
        return pillRepository.findByPillNameWithType(pillName);
    }

    @Override
    public List<SimplePillInfo> searchPill(PillSearchRequest pillSearchRequest) {
        return pillRepository.searchAppearance(pillSearchRequest);
    }

    @Override
    public PillDetailResponse detailPill(Long pillId) {
        PillDetailResponse response = pillRepository.detailPill(pillId)
                .orElseThrow(() -> new BusinessException(BusinessError.DONT_EXIST_PILL));
        List<String> efficacyTags = extractEfficacyTags(response.getPillEfficacy());
        List<String> specialCautionTags = extractSpecialCautionTags(response.getPillCaution());
        List<String> alertItems = parseAlertItems(response.getPillWarn(), response.getPillInteractive());

        response.setEfficacyTags(efficacyTags);
        response.setSpecialCautionTags(specialCautionTags);
        response.setAlertItems(alertItems);
        return response;
    }

    private Boolean callIdentifyAPI(Long itemSeq) {
        if (pillAppearanceRepository.findById(itemSeq).isEmpty()) {
            String url = UriComponentsBuilder.fromUriString("https://apis.data.go.kr/1471000/MdcinGrnIdntfcInfoService03/getMdcinGrnIdntfcInfoList03")
                    .queryParam("serviceKey", serviceKey)
                    .queryParam("item_seq", itemSeq)
                    .queryParam("type", "json")
                    .build()
                    .toUriString();
            log.info("URL: {}", url);
            PillAppearanceResponse response = restTemplateService.getForObject(url, PillAppearanceResponse.class);

            if (response == null || response.getBody() == null || response.getBody().getItems() == null) {
                return false;
            }

            List<PillAppearance> appearances = new ArrayList<>();
            for (PillAppearanceResponse.Item item : response.getBody().getItems()) {
                // 색상 문자열을 PillColor ID로 변환
                Long colorClass1Id = convertColorNameToId(item.getColorClass1());
                Long colorClass2Id = convertColorNameToId(item.getColorClass2());

                // 모양 문자열을 PillShape ID로 변환
                Long shapeId = convertShapeNameToId(item.getDrugShape());

                PillAppearance appearance = PillAppearance.builder()
                        .pillId(item.getItemSeq())
                        .pillFront(item.getPrintFront())
                        .pillBack(item.getPrintBack())
                        .pillClassification(item.getClassName())
                        .pillForm(item.getFormCodeName())
                        .lineFront(item.getLineFront())
                        .lineBack(item.getLineBack())
                        .markCodeFrontAnal(item.getMarkCodeFrontAnal())
                        .markCodeBackAnal(item.getMarkCodeBackAnal())
                        .colorClass1Id(colorClass1Id)
                        .colorClass2Id(colorClass2Id)
                        .pillType(item.getEtcOtcName())
                        .shapeId(shapeId)
                        .build();
                appearances.add(appearance);
            }
            pillAppearanceRepository.saveAll(appearances);
        }
        return true;
    }

    private void scheduleIdentifyApiCalls(List<Long> missingIds) {
        if (missingIds.isEmpty()) {
            return;
        }
        AtomicInteger sequence = new AtomicInteger(0);
        for (Long id : missingIds) {
            Duration delay = IDENTIFY_API_DELAY.multipliedBy(sequence.incrementAndGet());
            pillIdentifyTaskScheduler.schedule(() -> {
                try {
                    boolean result = callIdentifyAPI(id);
                    log.debug("Identify API 호출 완료 - pillId: {}, result: {}", id, result);
                } catch (Exception ex) {
                    log.error("Identify API 호출 실패 - pillId: {}", id, ex);
                }
            }, Instant.now().plus(delay));
            log.debug("Identify API 호출 예약 - pillId: {}, delaySeconds: {}", id, delay.toSeconds());
        }
    }

    /**
     * 색상 이름(String)을 PillColor ID(Long)로 변환
     *
     * @param colorName 색상 이름 (예: "연두")
     * @return PillColor ID, 없으면 null
     */
    private Long convertColorNameToId(String colorName) {
        if (colorName == null || colorName.isBlank()) {
            return null;
        }
        return pillColorRepository.findByColorName(colorName)
                .map(PillColor::getId)
                .orElse(null);
    }

    /**
     * 모양 이름(String)을 PillShape ID(Long)로 변환
     *
     * @param shapeName 모양 이름
     * @return PillShape ID, 없으면 null
     */
    private Long convertShapeNameToId(String shapeName) {
        if (shapeName == null || shapeName.isBlank()) {
            return null;
        }
        return pillShapeRepository.findByShapeName(shapeName)
                .map(PillShape::getId)
                .orElse(null);
    }

    /**
     * 약의 효능/효과 텍스트에서 주요 효능 태그를 추출합니다.
     *
     * @param efficacyText pillEfficacy (or pillDescription)
     * @return 추출된 태그 목록
     */
    private List<String> extractEfficacyTags(String efficacyText) {
        if (efficacyText == null || efficacyText.isBlank()) {
            return Collections.emptyList();
        }
        Set<String> extractedTags = new HashSet<>();
        for (Map.Entry<String, String> entry : EFFICACY_KEYWORD_MAP.entrySet()) {
            // 정규식이 아닌 단순 contains를 사용해 매칭 정확도 향상
            if (efficacyText.contains(entry.getKey())) {
                extractedTags.add(entry.getValue());
            }
        }
        return new ArrayList<>(extractedTags);
    }

    /**
     * 주의사항 텍스트에서 임산부, 노인 등 특별 주의 대상 태그를 추출합니다.
     *
     * @param cautionText pillCaution
     * @return 추출된 특별 주의 태그 목록
     */
    private List<String> extractSpecialCautionTags(String cautionText) {
        if (cautionText == null || cautionText.isBlank()) {
            return Collections.emptyList();
        }
        Set<String> extractedTags = new HashSet<>();

        for (Map.Entry<String, String> entry : CAUTION_KEYWORD_MAP.entrySet()) {
            // 정규식을 사용하여 유연하게 '소아' 또는 '노인' 패턴 매칭
            Pattern pattern = Pattern.compile(entry.getKey());
            Matcher matcher = pattern.matcher(cautionText);

            if (matcher.find()) {
                extractedTags.add(entry.getValue());
            }
        }
        return new ArrayList<>(extractedTags);
    }

    /**
     * 경고 및 상호작용 텍스트를 하나의 리스트 항목으로 분리합니다.
     * 주요 구분자는 문장 종료(`.`, `!`, `?`)와 줄 바꿈입니다.
     *
     * @param warnText        pillWarn (일반 경고)
     * @param interactiveText pillInteractive (약물 상호작용)
     * @return 분리된 주의사항 항목 목록
     */
    private List<String> parseAlertItems(String warnText, String interactiveText) {
        String combinedText = "";
        if (warnText != null) {
            combinedText += warnText.trim();
        }
        if (interactiveText != null) {
            // 두 텍스트가 명확히 구분되도록 사이에 줄 바꿈 추가
            if (!combinedText.isEmpty()) {
                combinedText += "\n";
            }
            combinedText += interactiveText.trim();
        }

        if (combinedText.isEmpty()) {
            return Collections.emptyList();
        }

        String[] rawItems = ALERT_SPLIT_PATTERN.split(combinedText);

        return Arrays.stream(rawItems)
                .map(String::trim)
                .filter(s -> !s.isBlank()) // 빈 문자열 제거
                .collect(Collectors.toList());
    }


    public List<SimplePillInfo> recognizeAndFindDetails(List<MultipartFile> images, List<RecognizeBoxRequest> boxes) {
        List<SimplePillInfo> results = new ArrayList<>();
        if (images == null || images.isEmpty()) {
            return List.of();
        }

        saveOriginalImage(images.get(0), boxes);

        List<MultipartFile> croppedImages = images.size() > 1
                ? images.subList(1, images.size())
                : List.of();
        if (croppedImages.isEmpty()) {
            log.info("[VLM] 크롭 이미지가 없어 FastAPI 호출을 건너뜁니다.");
            return List.of();
        }

        for (MultipartFile image : croppedImages) {
            try {
                OcrResponse ocrResponse = callFastApi(image);
                log.info("[VLM] FastAPI 응답: shape={}, texts={}, color={}",
                        ocrResponse != null ? ocrResponse.shape() : null,
                        ocrResponse != null ? ocrResponse.texts() : null,
                        ocrResponse != null ? ocrResponse.color() : null);

                if (ocrResponse == null || ocrResponse.texts() == null) continue;

                List<SimplePillInfo> found = searchByTextAndShape(
                        ocrResponse.texts(),
                        ocrResponse.shape(),
                        ocrResponse.color()
                );

                found.stream()
                        .map(pill -> SimplePillInfo.builder()
                                .pillId(pill.getPillId())
                                .pillName(pill.getPillName())
                                .manufacturer(pill.getManufacturer())
                                .classification(pill.getClassification())
                                .pillType(pill.getPillType())
                                .pillImg(pill.getPillImg())
                                .build()
                        )
                        .forEach(results::add);

            } catch (Exception e) {
                log.error("[VLM] 이미지 처리 중 오류: {}", e.getMessage(), e);
                throw new RuntimeException(e);
            }
        }

        log.info("[VLM] 최종 결과: {}건", results.size());
        return results.isEmpty() ? List.of() : results;
    }

    private void saveOriginalImage(MultipartFile originalImage, List<RecognizeBoxRequest> boxes) {
        if (originalImage == null || originalImage.isEmpty()) {
            return;
        }
        try {
            Path originalDir = Path.of(uploadRootPath, "originals");
            Files.createDirectories(originalDir);
            String savedName = "original_" + UUID.randomUUID() + ".jpg";
            Path target = originalDir.resolve(savedName);
            Files.copy(originalImage.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);
            String webImagePath = "/uploads/originals/" + savedName;
            PillImageData imageData = PillImageData.builder()
                    .imagePath(webImagePath)
                    .status(DataStatus.INBOX)
                    .build();
            if (boxes != null) {
                for (int i = 0; i < boxes.size(); i++) {
                    RecognizeBoxRequest box = boxes.get(i);
                    if (box == null || box.getXMin() == null || box.getYMin() == null
                            || box.getXMax() == null || box.getYMax() == null) {
                        continue;
                    }
                    imageData.addBox(PillImageBox.builder()
                            .boxIndex(box.getBoxIndex() == null ? i : box.getBoxIndex())
                            .xMin(box.getXMin())
                            .yMin(box.getYMin())
                            .xMax(box.getXMax())
                            .yMax(box.getYMax())
                            .build());
                }
            }
            pillImageDataRepository.save(imageData);
            log.info("[VLM] 원본 이미지 저장 및 DB기록 완료: file={}, imagePath={}", target, webImagePath);
        } catch (Exception e) {
            log.warn("[VLM] 원본 이미지 저장 실패: {}", e.getMessage(), e);
        }
    }

    private List<SimplePillInfo> searchByTextAndShape(List<String> texts, String shape, String color) {
        // 1단계: pill_front 또는 pill_back에서 텍스트 검색
        List<String> processedTexts = preprocessTexts(texts);
        if (processedTexts.isEmpty()) return List.of();

        List<PillAppearance> textResults = pillAppearanceRepository
                .findByPillTextsWithPill(processedTexts)
                .stream()
                .distinct()
                .collect(Collectors.toList());

        log.info("[VLM] 텍스트 검색 결과: {}건", textResults.size());

        if (textResults.isEmpty()) return List.of();
        if (textResults.size() == 1) return toSimplePillInfoList(textResults);

        // 2단계: shape로 추가 필터링
        List<PillAppearance> candidates = textResults;
        if (shape != null && !shape.isBlank()) {
            Set<Long> shapeIds = getShapeIds(shape);

            if (!shapeIds.isEmpty()) {
                List<PillAppearance> filtered = textResults.stream()
                        .filter(a -> shapeIds.contains(a.getShapeId()))
                        .collect(Collectors.toList());

                log.info("[VLM] shape 필터 후 결과: {}건 (shape={})", filtered.size(), shape);
                candidates = filtered.isEmpty() ? textResults : filtered;
            }
        }

        if (candidates.size() == 1) return toSimplePillInfoList(candidates);

        // 3단계: color 보조 필터링
        Set<Long> candidateColorIds = getColorCandidateIds(color);
        if (candidateColorIds != null && !candidateColorIds.isEmpty()) {
            List<PillAppearance> colorFiltered = candidates.stream()
                    .filter(a -> (a.getColorClass1Id() != null && candidateColorIds.contains(a.getColorClass1Id()))
                            || (a.getColorClass2Id() != null && candidateColorIds.contains(a.getColorClass2Id())))
                    .collect(Collectors.toList());
            log.info("[VLM] color 필터 후 결과: {}건 (color={})", colorFiltered.size(), color);
            candidates = colorFiltered.isEmpty() ? candidates : colorFiltered;
        }

        return toSimplePillInfoList(candidates);
    }

    private Set<Long> getColorCandidateIds(String color) {
        if (color == null || color.isBlank()) return null;
        return switch (color.toLowerCase()) {
            case "white" -> Set.of(1L, 17L, 31L, 34L, 35L, 38L, 39L, 41L, 51L, 52L,
                    14L, 20L, 36L);
            case "gray"  -> Set.of(14L, 20L, 36L,
                    1L, 17L, 31L, 34L, 35L, 38L, 39L, 41L, 51L, 52L);
            case "light_green" -> Set.of(7L, 27L, 50L,
                    8L, 32L, 35L, 38L, 46L);
            case "green" -> Set.of(8L, 32L, 35L, 38L, 46L,
                    7L, 27L, 50L);
            case "pink"  -> Set.of(4L, 44L, 45L,
                    3L, 23L, 39L, 43L);
            case "orange"-> Set.of(3L, 23L, 39L, 43L,
                    2L, 24L, 28L, 41L, 42L,
                    6L, 33L, 34L,
                    4L, 44L, 45L);
            case "yellow"-> Set.of(2L, 24L, 28L, 41L, 42L,
                    3L, 23L, 39L, 43L);
            case "brown" -> Set.of(6L, 33L, 34L,
                    3L, 23L, 39L, 43L);
            case "blue"  -> Set.of(10L, 18L, 19L, 29L, 51L,
                    1L, 17L, 31L, 34L, 35L, 38L, 39L, 41L, 51L, 52L);
            case "teal"        -> Set.of(9L, 21L, 40L, 52L);
            case "navy"        -> Set.of(11L, 25L);
            case "purple"      -> Set.of(13L, 22L, 26L, 37L, 47L, 48L);
            case "red"         -> Set.of(5L, 30L);
            case "black"       -> Set.of(15L, 49L);
            case "transparent" -> Set.of(16L, 19L, 21L, 24L, 26L, 31L, 38L, 39L, 48L, 49L, 50L);
            default -> null;
        };
    }

    private static Set<Long> getShapeIds(String shape) {
        return switch (shape.toLowerCase()) {
            case "oval"    -> Set.of(2L, 3L);
            case "oblong"  -> Set.of(3L, 2L);
            case "capsule" -> Set.of(3L, 2L);
            case "round"   -> Set.of(1L, 2L);
            case "triangle"   -> Set.of(5L);
            case "rectangle"  -> Set.of(6L);
            case "diamond"    -> Set.of(7L);
            case "pentagon"   -> Set.of(8L);
            case "hexagon"    -> Set.of(9L);
            case "octagon"    -> Set.of(10L);
            default -> Set.of();
        };
    }

    private List<SimplePillInfo> toSimplePillInfoList(List<PillAppearance> appearances) {
        return appearances.stream()
                .map(a -> SimplePillInfo.builder()
                        .pillId(a.getPillId())
                        .pillName(a.getPill().getPillName())
                        .manufacturer(a.getPill().getPillManufacturer())
                        .classification(a.getPillClassification())
                        .pillType(a.getPillType())
                        .pillImg(a.getPill().getPillImg())
                        .build()
                )
                .collect(Collectors.toList());
    }

    @Override
    public List<String> autocomplete(String keyword) {
        if (keyword == null || keyword.isBlank()) {
            return Collections.emptyList();
        }

        String trimmed = keyword.replaceAll("\\s+", "").toLowerCase();
        String cho = HangulUtils.decompose(trimmed);

        // Redis에서 한번에 다 가져오기
        String query = String.format(
                "(@name_cho:{*%s*} | @ingredient_cho:{*%s*} | @name_en:{*%s*})",
                escape(cho), escape(cho), escape(trimmed)
        );
        SearchResult result = jedis.ftSearch(
                "pill_idx", query,
                FTSearchParams.searchParams().limit(0, 50)
        );

        List<String> names = result.getDocuments().stream()
                .map(doc -> doc.getString("name"))
                .filter(name -> name != null && !name.isBlank())
                .distinct()
                .collect(Collectors.toList());

        // Java에서 원본 keyword 기준으로 재정렬
        return names.stream()
                .sorted(Comparator.comparingInt(name -> {
                    String normalized = name.replaceAll("\\s+", "").toLowerCase();
                    if (normalized.startsWith(trimmed)) return 0;       // 1순위: "타이"로 시작
                    if (normalized.contains(trimmed)) return 1;         // 2순위: 중간에 "타이" 포함
                    return 2;                                           // 3순위: 초성만 매칭 (타임 등)
                }))
                .limit(20)
                .collect(Collectors.toList());
    }

    @Override
    public List<String> autocompleteFromRdb(String keyword) {
        if (keyword == null || keyword.isBlank()) {
            return Collections.emptyList();
        }
        return pillRepository.findByPillNameContaining(keyword, PageRequest.of(0, 10))
                .stream()
                .map(Pill::getPillName)
                .collect(Collectors.toList());
    }

    private String escape(String s) {
        return s.replaceAll("([,.<>{}\\[\\]\"':;!@#$%^&*()+~|])", "\\\\$1");
    }

    private OcrResponse callFastApi(MultipartFile image) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("images", image.getResource());

        HttpEntity<MultiValueMap<String, Object>> request = new HttpEntity<>(body, headers);

        ResponseEntity<OcrResponse> response = restTemplateService.postForEntity(
                ocrServerUrl + "/api/v1/process",
                request,
                OcrResponse.class
        );
        return response.getBody();
    }

    private List<String> preprocessTexts(List<String> texts) {
        if (texts == null) return List.of();
        return texts.stream()
                .filter(t -> t != null && !t.isBlank())
                .filter(t -> !t.contains("마크") && !t.contains("로고"))
                .flatMap(t -> expandImprint(t).stream())
                .distinct()
                .collect(Collectors.toList());
    }

    private List<String> expandImprint(String text) {
        List<String> variants = new ArrayList<>();
        variants.add(text);
        String upper = text.toUpperCase();
        if (upper.replaceAll("\\s+", "").length() <= 3) {
            String variant = upper
                    .replace("0", "O")
                    .replace("O", "0")
                    .replace("1", "I")
                    .replace("I", "1")
                    .replace("5", "S")
                    .replace("Λ", "A")
                    .replace("A", "Λ")
                    .replace("α", "A")
                    .replace("S", "5");

            variants.add(variant);
        }
        variants.add(upper.replaceAll("\\s+", ""));
        return variants.stream().distinct().collect(Collectors.toList());
    }
}