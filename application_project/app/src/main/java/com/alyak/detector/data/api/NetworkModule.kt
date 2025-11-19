package com.alyak.detector.data.api

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    private const val BASE_URL = "https://dapi.kakao.com/"
    private const val SERVER_URL = "https://localhost:8080"

    @Provides
    @Singleton
    fun provideRetrofit(): Retrofit =
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

    @Provides
    @Singleton
    fun provideServerRetrofit(): Retrofit =
        Retrofit.Builder()
            .baseUrl(SERVER_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    @Provides
    @Singleton
    fun provideAppApi(retrofit: Retrofit): AuthApi =
        provideServerRetrofit().create(AuthApi::class.java)

    @Provides
    @Singleton
    fun provideKakaoLocalApi(retrofit: Retrofit): KakaoLocalApi =
        retrofit.create(KakaoLocalApi::class.java)
}
