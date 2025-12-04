package com.alyak.detector.feature.pill.data.model

import com.alyak.detector.core.model.SpecialCautionType

/**
 * 약 상세 정보를 담는 DTO 클래스.
 *
 * @property medicineInfo 약의 기본 정보 데이터
 * @property dosageInfo 복용 용법 및 시간 정보
 * @property effectsInfo 약의 효능 및 효과 정보
 * @property alertInfo 주의사항 및 부작용 정보
 * @property specialCaution 특별 주의 대상 정보
 * @property sideEffects 주요 부작용 설명
 */
data class MedicineDetailDto(
    val medicineInfo: MedicineInfoDto,
    val dosageInfo: DosageInfoDto,
    val effectsInfo: EffectsInfoDto,
    val alertInfo: AlertInfoDto,
    val specialCaution: SpecialCautionDto,
    val sideEffects: SideEffectsDto,
)

/**
 * 약 기본 정보를 담는 데이터 클래스.
 *
 * @property name 약품명 (예: 타이레놀 500mg)
 * @property classification 분류(해열, 진통, 소염제)
 * @property manufacturer 제조사 이름
 * @property pillId 식별코드
 * @property category 약 분류 (예: 일반의약품)
 */
data class MedicineInfoDto(
    val name: String,
    val classification: String,
    val manufacturer: String,
    val pillId: Long,
    val category: String,
    val img: String?
)

/**
 * 복용 정보를 담는 데이터 클래스.
 *
 * @property dosageText 복용 관련 설명 텍스트
 */
data class DosageInfoDto(
    val dosageText: String,
)

/**
 * 약의 효능 및 효과 정보를 담는 데이터 클래스.
 *
 * @property tags 효능 태그 목록 (예: 해열, 진통)
 * @property description 효능 상세 설명
 */
data class EffectsInfoDto(
    val tags: List<String>,
    val description: String
)

/**
 * 주의사항 및 부작용 정보를 담는 데이터 클래스.
 * @property title 주의사항 제목
 * @property items 주의사항 목록
 */
data class AlertInfoDto(
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
data class SpecialCautionDto(
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
data class SideEffectsDto(
    val title: String,
    val description: String
)

data class ServerResponsePillDetail(
    val pillId: Long,
    val pillName: String,
    val pillImg: String?,
    val pillDescription: String,
    val userMethod: String,
    val pillEfficacy: String,
    val pillWarn: String,
    val pillCaution: String,
    val pillInteractive: String,
    val pillAdverseReaction: String,
    val manufacturer: String,
    val pillClassification: String?,
    val pillType: String?,
    val efficacyTags: List<String>?,
    val specialCautionTags: List<String>?,
    val alertItems: List<String>?
)

fun ServerResponsePillDetail.toDomain(): MedicineDetailDto {
    return MedicineDetailDto(
        medicineInfo = MedicineInfoDto(
            name = this.pillName,
            classification = this.pillClassification ?: "",
            manufacturer = this.manufacturer,
            pillId = this.pillId,
            category = this.pillType ?: "",
            img = this.pillImg
        ),
        dosageInfo = DosageInfoDto(
            dosageText = this.userMethod,
        ),
        effectsInfo = EffectsInfoDto(
            tags = this.efficacyTags ?: emptyList(),
            description = this.pillDescription
        ),
        alertInfo = AlertInfoDto(
            title = "주의사항",
            items = this.alertItems ?: emptyList()
        ),
        specialCaution = SpecialCautionDto(
            title = "특별 주의 대상",
            tags = this.specialCautionTags?.map { tag ->
                when (tag) {
                    "임산부/수유부" -> SpecialCautionType.PREGNANT
                    else -> SpecialCautionType.PREGNANT
                }
            } ?: emptyList(),
            extraText = null
        ),
        sideEffects = SideEffectsDto(
            title = "주요 부작용",
            description = this.pillAdverseReaction
        )
    )

}