package com.alyak.detector.core.network

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.io.IOException

/**
 * 네트워크 API 호출을 안전하게 실행하고 그 결과를 ApiResult로 래핑하는 고차 함수.
 * 코루틴의 IO 디스패처에서 실행되며, 발생할 수 있는 예외를 처리한다.
 *
 * @param T API가 성공 시 반환하는 데이터의 타입.
 * @param apiCall 실행할 suspend 함수 (Retrofit 서비스의 API 호출 메서드).
 * @return ApiResult<T>로 래핑된 호출 결과.
 */
suspend fun <T> safeCall(apiCall: suspend () -> T): ApiResult<T> {
    // withContext를 사용해 IO 스레드에서 네트워크 작업을 수행하도록 보장한다.
    return withContext(Dispatchers.IO) {
        try {
            // API 호출을 실행하고 성공하면 결과를 Success 객체로 감싼다.
            ApiResult.Success(apiCall())
        } catch (e: HttpException) {
            // HTTP 관련 예외 처리 (4xx, 5xx 에러 등)
            ApiResult.Error(code = e.code(), message = e.message())
        } catch (e: IOException) {
            // 네트워크 연결 문제 등 IO 관련 예외 처리
            ApiResult.Exception(e)
        } catch (e: Exception) {
            // 그 외 모든 예외 처리
            ApiResult.Exception(e)
        }
    }
}