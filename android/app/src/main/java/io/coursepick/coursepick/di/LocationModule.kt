package io.coursepick.coursepick.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import io.coursepick.coursepick.data.location.DefaultLocationRepository
import io.coursepick.coursepick.domain.location.LocationRepository
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object LocationModule {
    @Provides
    @Singleton
    fun provideLocationRepository(
        @ApplicationContext applicationContext: Context,
    ): LocationRepository = DefaultLocationRepository(applicationContext)
}
