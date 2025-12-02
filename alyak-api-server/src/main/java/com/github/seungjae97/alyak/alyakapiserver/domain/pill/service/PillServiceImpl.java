package com.github.seungjae97.alyak.alyakapiserver.domain.pill.service;

import com.github.seungjae97.alyak.alyakapiserver.domain.pill.dto.request.PillSearchRequest;
import com.github.seungjae97.alyak.alyakapiserver.domain.pill.dto.response.PillAppearanceResponse;
import com.github.seungjae97.alyak.alyakapiserver.domain.pill.dto.response.PillInfoResponse;
import com.github.seungjae97.alyak.alyakapiserver.domain.pill.dto.response.SimplePillInfo;
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
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

@Service
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Slf4j
public class PillServiceImpl implements PillService {

    private final PillRepository pillRepository;
    private final PillAppearanceRepository pillAppearanceRepository;
    private final PillColorRepository pillColorRepository;
    private final PillShapeRepository pillShapeRepository;
    private final RestTemplateService restTemplateService;

    @Qualifier("pillIdentifyTaskScheduler")
    private final TaskScheduler pillIdentifyTaskScheduler;

    private static final Duration IDENTIFY_API_DELAY = Duration.ofSeconds(5);

    @Value("${OPEN_DATA_KEY}")
    private String serviceKey;

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

    private Boolean callIdentifyAPI(Long itemSeq) {
        if(pillAppearanceRepository.findById(itemSeq).isEmpty()) {
            String url = UriComponentsBuilder.fromUriString("https://apis.data.go.kr/1471000/MdcinGrnIdntfcInfoService03/getMdcinGrnIdntfcInfoList03")
                    .queryParam("serviceKey", serviceKey)
                    .queryParam("item_seq", itemSeq)
                    .queryParam("type", "json")
                    .build()
                    .toUriString();
            log.info("URL: {}" , url);
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
}
