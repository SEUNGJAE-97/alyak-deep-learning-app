package com.github.seungjae97.alyak.alyakapiserver.domain.pill.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class PillAppearanceResponse {

    /** API 응답 헤더 정보 */
    @JsonProperty("header")
    private Header header;

    /** API 응답 바디 정보 */
    @JsonProperty("body")
    private Body body;

    @Data
    public static class Header {
        /** 결과 코드 */
        @JsonProperty("resultCode")
        private String resultCode;

        /** 결과 메시지 */
        @JsonProperty("resultMsg")
        private String resultMsg;
    }

    @Data
    public static class Body {
        /** 현재 페이지 번호 */
        @JsonProperty("pageNo")
        private int pageNo;

        /** 전체 데이터 개수 */
        @JsonProperty("totalCount")
        private int totalCount;

        /** 한 페이지 결과 수 */
        @JsonProperty("numOfRows")
        private int numOfRows;

        /** 약품 상세 정보 리스트 */
        @JsonProperty("items")
        private List<Item> items;
    }

    @Data
    public static class Item {
        /** 품목일련번호 */
        @JsonProperty("ITEM_SEQ")
        private Long itemSeq;

        /** 품목명 */
        @JsonProperty("ITEM_NAME")
        private String itemName;

        /** 약의 외형(색상, 모양 등) */
        @JsonProperty("CHART")
        private String chart;

        /** 약 이미지 URL */
        @JsonProperty("ITEM_IMAGE")
        private String itemImage;

        /** 앞면 인쇄 내용 */
        @JsonProperty("PRINT_FRONT")
        private String printFront;

        /** 뒷면 인쇄 내용 */
        @JsonProperty("PRINT_BACK")
        private String printBack;

        /** 약 모양 */
        @JsonProperty("DRUG_SHAPE")
        private String drugShape;

        /** 주 색상 */
        @JsonProperty("COLOR_CLASS1")
        private String colorClass1;

        /** 보조 색상 */
        @JsonProperty("COLOR_CLASS2")
        private String colorClass2;

        /** 앞면 분할선 */
        @JsonProperty("LINE_FRONT")
        private String lineFront;

        /** 뒷면 분할선 */
        @JsonProperty("LINE_BACK")
        private String lineBack;

        /** 분류 번호 */
        @JsonProperty("CLASS_NO")
        private String classNo;

        /** 분류명 */
        @JsonProperty("CLASS_NAME")
        private String className;

        /** 전문/일반 약 구분 */
        @JsonProperty("ETC_OTC_NAME")
        private String etcOtcName;

        /** 제형 코드명 */
        @JsonProperty("FORM_CODE_NAME")
        private String formCodeName;

        /** 앞면 마크 코드 분석 */
        @JsonProperty("MARK_CODE_FRONT_ANAL")
        private String markCodeFrontAnal;

        /** 뒷면 마크 코드 분석 */
        @JsonProperty("MARK_CODE_BACK_ANAL")
        private String markCodeBackAnal;

        /** 앞면 마크 코드 이미지 */
        @JsonProperty("MARK_CODE_FRONT_IMG")
        private String markCodeFrontImg;

        /** 뒷면 마크 코드 이미지 */
        @JsonProperty("MARK_CODE_BACK_IMG")
        private String markCodeBackImg;

        /** 품목 영어명 */
        @JsonProperty("ITEM_ENG_NAME")
        private String itemEngName;

        /** 앞면 마크 코드 */
        @JsonProperty("MARK_CODE_FRONT")
        private String markCodeFront;

        /** 뒷면 마크 코드 */
        @JsonProperty("MARK_CODE_BACK")
        private String markCodeBack;

    }
}

