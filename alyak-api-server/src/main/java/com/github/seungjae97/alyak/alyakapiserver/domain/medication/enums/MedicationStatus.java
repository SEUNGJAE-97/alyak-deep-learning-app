package com.github.seungjae97.alyak.alyakapiserver.domain.medication.enums;

/**
 * 복용 기록 상태 (medication_log.status).
 * 서버에는 복용/미복용 처리 결과만 저장합니다. 예정 건수는 클라이언트(Room 등)에서 관리합니다.
 */
public enum MedicationStatus {
    /** 복용 완료 (예정 시각 기준 30분 이내) */
    TAKEN,
    /** 지연 복용 */
    DELAYED,
    /** 미복용 */
    SKIPPED
}
