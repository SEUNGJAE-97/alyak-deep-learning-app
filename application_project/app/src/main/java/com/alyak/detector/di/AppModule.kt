package com.alyak.detector.di

import android.content.Context
import androidx.room.Room
import coil3.ImageLoader
import coil3.request.crossfade
import com.alyak.detector.feature.family.data.api.FamilyService
import com.alyak.detector.feature.family.data.repository.FamilyRepo
import com.alyak.detector.feature.map.data.api.MapApi
import com.alyak.detector.feature.pill.data.api.PillApi
import com.alyak.detector.feature.pill.data.model.local.dao.RecentSearchDao
import com.alyak.detector.feature.pill.data.model.local.database.PillDatabase
import com.alyak.detector.feature.pill.data.repository.PillRepository
import com.alyak.detector.feature.pill.data.repository.PillRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideFamilyService(@AppServerRetrofit retrofit: Retrofit): FamilyService =
        retrofit.create(FamilyService::class.java)

    @Provides
    @Singleton
    fun provideFamilyRepository(familyService: FamilyService): FamilyRepo {
        return FamilyRepo(familyService)
    }

    @Provides
    @Singleton
    fun provideMapApi(@AppServerRetrofit retrofit: Retrofit): MapApi =
        retrofit.create(MapApi::class.java)

    @Provides
    @Singleton
    fun providePillApi(@AppServerRetrofit retrofit: Retrofit): PillApi =
        retrofit.create(PillApi::class.java)

    @Provides
    @Singleton
    fun providePillDatabase(@ApplicationContext context: Context): PillDatabase {
        return Room.databaseBuilder(
            context,
            PillDatabase::class.java,
            "pill_database"
        ).build()
    }

    @Provides
    @Singleton
    fun provideRecentSearchDao(database: PillDatabase): RecentSearchDao {
        return database.recentSearchDao()
    }

    @Provides
    @Singleton
    fun providePillRepository(
        recentSearchDao: RecentSearchDao,
        pillApi: PillApi
    ): PillRepository {
        return PillRepositoryImpl(recentSearchDao, pillApi)
    }

    @Provides
    @Singleton
    fun provideImageLoader(@ApplicationContext context: Context): ImageLoader {
        return ImageLoader.Builder(context)
            .crossfade(true)
            .build()
    }
}