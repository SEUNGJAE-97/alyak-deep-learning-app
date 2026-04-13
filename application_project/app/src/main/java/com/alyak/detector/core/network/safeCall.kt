package com.alyak.detector.core.network

import org.json.JSONObject

/**
 * Spring [ProblemDetail] 등 JSON 오류 본문에서 사용자용 메시지(`detail`)를 꺼냅니다.
 */
fun parseApiErrorMessage(raw: String?): String? {
    if (raw.isNullOrBlank()) return null
    return try {
        val o = JSONObject(raw)
        o.optString("detail", "").takeIf { it.isNotEmpty() }
    } catch (_: Exception) {
        null
    }
}

/**
 * 네트워크 API 호출을 안전하게 실행하고 그 결과를 ApiResult로 래핑하는 고차 함수.
 * 코루틴의 IO 디스패처에서 실행되며, 발생할 수 있는 예외를 처리한다.
 *
 * @param T API가 성공 시 반환하는 데이터의 타입.
 * @param apiCall 실행할 suspend 함수 (Retrofit 서비스의 API 호출 메서드).
 * @return ApiResult<T>로 래핑된 호출 결과.
 */
@Suppress("UNCHECKED_CAST")
suspend inline fun <reified T> safeCall(
    crossinline apiCall: suspend () -> retrofit2.Response<T>,
): ApiResult<T> {
    return try {
        val response = apiCall()
        if (response.isSuccessful) {
            val body = response.body()
            when {
                body != null -> ApiResult.Success(body)
                response.code() == 204 || response.code() == 205 -> ApiResult.Success(null as T)
                response.code() == 200 && T::class == Unit::class -> ApiResult.Success(Unit as T)
                else -> ApiResult.Error(response.code(), "Empty body")
            }
        } else {
            val raw = response.errorBody()?.string()
            val message = parseApiErrorMessage(raw) ?: raw
            ApiResult.Error(response.code(), message)
        }
    } catch (t: Throwable) {
        ApiResult.Exception(t)
    }
}