package com.alyak.detector.di

import com.alyak.detector.feature.family.data.api.FamilyService
import com.alyak.detector.feature.family.data.repository.FamilyRepo
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideFamilyService(@AppServerRetrofit retrofit: retrofit2.Retrofit): FamilyService =
        retrofit.create(FamilyService::class.java)

    @Provides
    @Singleton
    fun provideFamilyRepository(familyService: FamilyService): FamilyRepo {
        return FamilyRepo(familyService)
    }
}