package com.alyak.detector.di

import javax.inject.Qualifier

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class KakaoRetrofit

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class AppServerRetrofit

@Qualifier
@Retention(AnnotationRetention.RUNTIME)
annotation class ApplicationScope

