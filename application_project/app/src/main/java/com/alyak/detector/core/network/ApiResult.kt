package com.alyak.detector.core.network

/**
 * 네트워크 API 호출 결과를 래핑하는 봉인 클래스(Sealed Class).
 * 성공, 비즈니스 에러, 예외 세 가지 상태를 명확하게 처리할 수 있도록 돕는다.
 * @param T 성공했을 때 받아올 데이터의 타입
 */
sealed class ApiResult<out T> {

    /**
     * API 호출이 성공하고 데이터를 성공적으로 받아왔을 때의 상태.
     * @property data 서버로부터 받은 데이터.
     */
    data class Success<out T>(val data: T) : ApiResult<T>()

    /**
     * API 호출은 성공했으나, 서버에서 정의한 비즈니스 로직상 에러가 발생한 상태.
     * @property code 서버에서 보낸 에러 코드 (예: 404, 500 등 HTTP 상태 코드 또는 커스텀 코드).
     * @property message 서버에서 보낸 에러 메시지.
     */
    data class Error(val code: Int, val message: String?) : ApiResult<Nothing>()

    /**
     * 네트워크 오류, 서버 다운 등 통신 과정에서 예외가 발생한 상태.
     * @property throwable 발생한 예외 객체 (예: IOException, HttpException 등).
     */
    data class Exception(val throwable: Throwable) : ApiResult<Nothing>()
}