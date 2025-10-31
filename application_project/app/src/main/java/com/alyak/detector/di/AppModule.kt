package com.alyak.detector.di

import com.alyak.detector.data.family.api.FamilyService
import com.alyak.detector.data.family.repository.FamilyRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    fun provideFamilyService(): FamilyService {
        return Retrofit.Builder()
            .baseUrl("https://your.api.base.url/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(FamilyService::class.java)
    }

    @Provides
    fun provideFamilyRepository(familyService: FamilyService): FamilyRepository {
        return FamilyRepository(familyService)
    }
}