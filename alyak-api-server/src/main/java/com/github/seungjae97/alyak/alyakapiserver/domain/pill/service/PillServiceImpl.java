package com.github.seungjae97.alyak.alyakapiserver.domain.pill.service;

import com.github.seungjae97.alyak.alyakapiserver.domain.pill.dto.request.PillSearchRequest;
import com.github.seungjae97.alyak.alyakapiserver.domain.pill.dto.response.PillInfoResponse;
import com.github.seungjae97.alyak.alyakapiserver.domain.pill.dto.response.SimplePillInfo;
import com.github.seungjae97.alyak.alyakapiserver.domain.pill.entity.Pill;
import com.github.seungjae97.alyak.alyakapiserver.domain.pill.repository.PillRepository;
import com.github.seungjae97.alyak.alyakapiserver.global.common.exception.BusinessError;
import com.github.seungjae97.alyak.alyakapiserver.global.common.exception.BusinessException;
import com.github.seungjae97.alyak.alyakapiserver.global.http.service.RestTemplateService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class PillServiceImpl implements PillService {

    private final PillRepository pillRepository;
    private final RestTemplateService restTemplateService;

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

            // 2. 만약 외부 API 조회결과가 존재한다면? 새 알약을 DB에 저장
            List<Pill> pills = new ArrayList<>();
            for (PillInfoResponse.Item item : pillInfoResponse.getBody().getItems()) {
                Pill pill = Pill.builder()
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
            pillList = pillRepository.findByPillName(pillName);
        }
        // 2.1 사용자에게 알약 정보 전달
        List<SimplePillInfo> result = new ArrayList<>();
        for (Pill pill : pillList) {
            result.add(SimplePillInfo.builder()
                    .pillId(pill.getId())
                    .pillName(pill.getPillName())
                    .ingredient(pill.getPillIngredient())
                    .manufacturer(pill.getPillManufacturer())
                    .build());

        }
        return result;
    }

    @Override
    public List<SimplePillInfo> searchPill(PillSearchRequest pillSearchRequest) {
        return pillRepository.searchAppearance(pillSearchRequest);
    }
}
