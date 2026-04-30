package io.coursepick.coursepick.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import io.coursepick.coursepick.presentation.InstallationId
import javax.inject.Qualifier
import javax.inject.Singleton

@Qualifier
annotation class Auth

@Qualifier
annotation class Settings

private val Context.authDataStore: DataStore<Preferences> by preferencesDataStore(name = "auth")

private val Context.settingsDataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

@Module
@InstallIn(SingletonComponent::class)
object LocalDataModule {
    @Provides
    fun provideInstallationId(
        @ApplicationContext applicationContext: Context,
    ): InstallationId = InstallationId(applicationContext)

    @Provides
    @Singleton
    @Auth
    fun provideAuthDataStore(
        @ApplicationContext context: Context,
    ): DataStore<Preferences> = context.authDataStore

    @Provides
    @Singleton
    @Settings
    fun providePreferencesDataStore(
        @ApplicationContext context: Context,
    ): DataStore<Preferences> = context.settingsDataStore
}
