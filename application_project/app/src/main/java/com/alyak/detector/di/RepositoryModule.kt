package com.alyak.detector.di

import com.alyak.detector.feature.map.data.repository.ApiRepo
import com.alyak.detector.feature.map.data.repository.ApiRepoImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module // 1. 모듈임을 알림
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds // 4. 연결 고리 설정
    @Singleton
    abstract fun bindApiRepo(
        apiRepoImpl: ApiRepoImpl
    ): ApiRepo
}