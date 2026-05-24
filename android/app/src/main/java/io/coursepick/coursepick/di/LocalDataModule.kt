package io.coursepick.coursepick.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.SharedPreferencesMigration
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
annotation class AuthDataStore

@Qualifier
annotation class PreferencesDataStore

@Qualifier
annotation class FavoriteCourseDataStore

@Qualifier
annotation class NoticeDataStore

private val Context.authDataStore: DataStore<Preferences> by preferencesDataStore(name = "auth")

private val Context.preferencesDataStore: DataStore<Preferences> by preferencesDataStore(name = "preferences")

private val Context.favoriteCourseDataStore: DataStore<Preferences> by preferencesDataStore(
    name = "favorite_course",
    produceMigrations = { context: Context ->
        listOf(
            SharedPreferencesMigration(
                context = context,
                sharedPreferencesName = "${context.packageName}_preferences",
                keysToMigrate = setOf("favorited_courses_key"),
            ),
        )
    },
)

private val Context.noticeDataStore: DataStore<Preferences> by preferencesDataStore(name = "notice")

@Module
@InstallIn(SingletonComponent::class)
object LocalDataModule {
    @Provides
    fun provideInstallationId(
        @ApplicationContext applicationContext: Context,
    ): InstallationId = InstallationId(applicationContext)

    @Provides
    @Singleton
    @AuthDataStore
    fun provideAuthDataStore(
        @ApplicationContext context: Context,
    ): DataStore<Preferences> = context.authDataStore

    @Provides
    @Singleton
    @PreferencesDataStore
    fun providePreferencesDataStore(
        @ApplicationContext context: Context,
    ): DataStore<Preferences> = context.preferencesDataStore

    @Provides
    @Singleton
    @FavoriteCourseDataStore
    fun provideFavoriteCourseDataStore(
        @ApplicationContext context: Context,
    ): DataStore<Preferences> = context.favoriteCourseDataStore

    @Provides
    @Singleton
    @NoticeDataStore
    fun provideNoticeDataStore(
        @ApplicationContext context: Context,
    ): DataStore<Preferences> = context.noticeDataStore
}
