package com.alyak.detector.data.dto.pill.PillDetail

import com.alyak.detector.data.dto.MealTime
import com.alyak.detector.data.dto.common.SpecialCautionType

/**
 * 약 상세 정보를 담는 DTO 클래스.
 *
 * @property medicineInfo 약의 기본 정보 데이터
 * @property dosageInfo 복용 용법 및 시간 정보
 * @property effectsInfo 약의 효능 및 효과 정보
 * @property alertInfo 주의사항 및 부작용 정보
 * @property specialCaution 특별 주의 대상 정보
 * @property sideEffects 주요 부작용 설명
 * @property additionalInfo 보관 방법, 유효기간 등의 추가 정보
 * @property memo 사용자 메모 정보
 */
data class MedicineDetailDTO(
    val medicineInfo: MedicineInfoDTO,
    val dosageInfo: DosageInfoDTO,
    val effectsInfo: EffectsInfoDTO,
    val alertInfo: AlertInfoDTO,
    val specialCaution: SpecialCautionDTO,
    val sideEffects: SideEffectsDTO,
    val additionalInfo: AdditionalInfoDTO,
    val memo: MemoDTO
)

/**
 * 약 기본 정보를 담는 데이터 클래스.
 *
 * @property name 약품명 (예: 타이레놀 500mg)
 * @property subName 부가적인 보조명칭 (예: 아세트 아미노펜)
 * @property manufacturer 제조사 이름
 * @property code 식별코드
 * @property category 약 분류 (예: 일반의약품)
 */
data class MedicineInfoDTO(
    val name: String,
    val subName: String,
    val manufacturer: String,
    val code: String,
    val category: String,
    val img: Int
)

/**
 * 복용 정보를 담는 데이터 클래스.
 *
 * @property dosageText 복용 관련 설명 텍스트
 * @property dosageTimes 복용 시간대나 특이사항 리스트
 */
data class DosageInfoDTO(
    val dosageText: String,
    val dosageTimes: List<MealTime>
)

/**
 * 약의 효능 및 효과 정보를 담는 데이터 클래스.
 *
 * @property tags 효능 태그 목록 (예: 해열, 진통)
 * @property description 효능 상세 설명
 */
data class EffectsInfoDTO(
    val tags: List<String>,
    val description: String
)

/**
 * 주의사항 및 부작용 정보를 담는 데이터 클래스.
 * @property title 주의사항 제목
 * @property items 주의사항 목록
 */
data class AlertInfoDTO(
    val title: String,
    val items: List<String>
)

/**
 * 특별 주의 대상 정보를 담는 데이터 클래스.
 *
 * @property title 특별 주의 대상 제목
 * @property tags 주의 대상 태그 리스트 (예: 임산부, 어린이)
 * @property extraText 추가 안내문 (예: 운전자)
 */
data class SpecialCautionDTO(
    val title: String,
    val tags: List<SpecialCautionType>,
    val extraText: String? = null
)

/**
 * 주요 부작용 정보를 담는 데이터 클래스.
 *
 * @property title 부작용 제목
 * @property description 부작용 상세 설명
 */
data class SideEffectsDTO(
    val title: String,
    val description: String
)

/**
 * 추가적인 약 정보를 담는 데이터 클래스.
 *
 * @property storageMethod 보관 방법 (예: 실온 보관 1~30도)
 * @property expiration 유효기간 날짜
 * @property formulation 제형 설명 (예: 타원형 정제)
 * @property packaging 포장 단위 (예: 10정, 20정, 100정)
 */
data class AdditionalInfoDTO(
    val storageMethod: String,
    val expiration: String,
    val formulation: String,
    val packaging: String
)

/**
 * 사용자가 남기는 메모 정보를 담는 데이터 클래스.
 *
 * @property content 메모 내용 텍스트
 */
data class MemoDTO(
    val content: String
)
