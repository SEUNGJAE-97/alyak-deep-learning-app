package com.alyak.detector.core.network

import kotlin.annotation.AnnotationRetention
import kotlin.annotation.AnnotationTarget

/**
 * Retrofit 메서드에 붙이면 [AuthInterceptor]가 Bearer 토큰을 붙이지 않습니다.
 */
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class NoAuth
