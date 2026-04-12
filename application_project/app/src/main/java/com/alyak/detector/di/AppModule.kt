package com.alyak.detector.di

import android.content.Context
import androidx.room.Room
import coil3.ImageLoader
import coil3.request.crossfade
import com.alyak.detector.feature.auth.data.api.AuthApi
import com.alyak.detector.feature.auth.repository.AuthRepository
import com.alyak.detector.feature.camera.data.api.PillOCRApi
import com.alyak.detector.feature.camera.data.repository.CameraRepo
import com.alyak.detector.feature.camera.data.repository.CameraRepoImpl
import com.alyak.detector.feature.family.data.api.FamilyService
import com.alyak.detector.feature.family.data.repository.FamilyRepo
import com.alyak.detector.feature.map.data.api.MapApi
import com.alyak.detector.feature.pill.data.api.PillApi
import com.alyak.detector.feature.pill.data.model.local.dao.RecentSearchDao
import com.alyak.detector.feature.pill.data.model.local.database.PillDatabase
import com.alyak.detector.feature.pill.data.repository.PillRepository
import com.alyak.detector.feature.pill.data.repository.PillRepositoryImpl
import com.alyak.detector.feature.user.data.api.UserService
import com.alyak.detector.feature.user.repository.UserRepository
import com.alyak.detector.feature.notification.data.api.ScheduleApi
import com.alyak.detector.feature.notification.data.repository.ScheduleRepository
import com.alyak.detector.push.dao.NotificationDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
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
    fun providePillOCRApi(@AppServerRetrofit retrofit: Retrofit): PillOCRApi =
        retrofit.create(PillOCRApi::class.java)

    @Provides
    @Singleton
    fun provideCameraRepository(pillOCRApi: PillOCRApi): CameraRepo =
        CameraRepoImpl(pillOCRApi)

    @Provides
    @Singleton
    fun providePillDatabase(@ApplicationContext context: Context): PillDatabase {
        return Room.databaseBuilder(
            context,
            PillDatabase::class.java,
            "pill_database"
        ).fallbackToDestructiveMigration().build()
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
    fun provideAuthRepository(authApi: AuthApi): AuthRepository {
        return AuthRepository(authApi)
    }

    @Provides
    @Singleton
    fun provideImageLoader(@ApplicationContext context: Context): ImageLoader {
        return ImageLoader.Builder(context)
            .crossfade(true)
            .build()
    }

    @Provides
    @Singleton
    fun provideScheduleApi(@AppServerRetrofit retrofit: Retrofit): ScheduleApi =
        retrofit.create(ScheduleApi::class.java)

    @Provides
    @Singleton
    fun provideScheduleRepository(scheduleApi: ScheduleApi): ScheduleRepository =
        ScheduleRepository(scheduleApi)

    @Provides
    @Singleton
    fun provideUserService(@AppServerRetrofit retrofit: Retrofit): UserService =
        retrofit.create(UserService::class.java)

    @Provides
    @Singleton
    fun provideUserRepository(userService: UserService): UserRepository {
        return UserRepository(userService)
    }

    @Provides
    @Singleton
    @ApplicationScope
    fun provideApplicationScope(): CoroutineScope =
        CoroutineScope(SupervisorJob() + Dispatchers.Default)

    @Provides
    @Singleton
    fun provideNotificationDao(database: PillDatabase): NotificationDao {
        return database.notificationDao()
    }
}