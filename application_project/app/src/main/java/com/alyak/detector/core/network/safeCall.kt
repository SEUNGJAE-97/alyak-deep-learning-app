package com.alyak.detector.core.network

import com.google.android.gms.tasks.Tasks.call
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
suspend fun <T> safeCall(apiCall: suspend () -> retrofit2.Response<T>): ApiResult<T> {
        return try {
            val response = apiCall()
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null) {
                    ApiResult.Success(body)
                } else {
                    ApiResult.Error(response.code(), "Empty body")
                }
            } else {
                ApiResult.Error(response.code(), response.errorBody()?.string())
            }
        } catch (t: Throwable) {
            ApiResult.Exception(t)
        }
}