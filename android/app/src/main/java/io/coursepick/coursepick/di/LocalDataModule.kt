package io.coursepick.coursepick.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import io.coursepick.coursepick.presentation.InstallationId

@Module
@InstallIn(SingletonComponent::class)
object LocalDataModule {
    @Provides
    fun provideInstallationId(
        @ApplicationContext applicationContext: Context,
    ): InstallationId = InstallationId(applicationContext)
}
