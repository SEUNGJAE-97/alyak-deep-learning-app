package com.alyak.detector.data.api

import com.alyak.detector.core.network.AuthInterceptor
import com.alyak.detector.di.AppServerRetrofit
import com.alyak.detector.di.KakaoRetrofit
import com.alyak.detector.feature.auth.data.api.AuthApi
import com.alyak.detector.feature.map.data.api.KakaoLocalApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    private const val BASE_URL = "https://dapi.kakao.com/"
    private const val SERVER_URL = "http://10.0.2.2:8080/"

    @Provides
    @Singleton
    @KakaoRetrofit
    fun provideRetrofit(): Retrofit =
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

    @Provides
    @Singleton
    @AppServerRetrofit
    fun provideServerRetrofit(
        authInterceptor: AuthInterceptor
    ): Retrofit {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
        val client = OkHttpClient.Builder()
            .addInterceptor(authInterceptor)  // 토큰 추가 (먼저 실행)
            .addInterceptor(logging)          // 로깅 (나중에 실행)
            .build()
        
        return Retrofit.Builder()
            .baseUrl(SERVER_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideAuthApi(@AppServerRetrofit retrofit: Retrofit): AuthApi =
        retrofit.create(AuthApi::class.java)

    @Provides
    @Singleton
    fun provideKakaoLocalApi(@KakaoRetrofit retrofit: Retrofit): KakaoLocalApi =
        retrofit.create(KakaoLocalApi::class.java)
}
