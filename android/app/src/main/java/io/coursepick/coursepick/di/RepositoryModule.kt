package io.coursepick.coursepick.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.coursepick.coursepick.data.auth.DefaultAuthRepository
import io.coursepick.coursepick.data.course.DefaultCourseRepository
import io.coursepick.coursepick.data.customcourse.DefaultCustomCourseRepository
import io.coursepick.coursepick.data.favorites.DefaultFavoriteCourseRepository
import io.coursepick.coursepick.data.notice.DefaultNoticeRepository
import io.coursepick.coursepick.data.preference.DefaultPreferencesRepository
import io.coursepick.coursepick.data.search.DefaultSearchRepository
import io.coursepick.coursepick.domain.auth.AuthRepository
import io.coursepick.coursepick.domain.course.CourseRepository
import io.coursepick.coursepick.domain.customcourse.CustomCourseRepository
import io.coursepick.coursepick.domain.favorites.FavoriteCourseRepository
import io.coursepick.coursepick.domain.notice.NoticeRepository
import io.coursepick.coursepick.domain.preferences.PreferencesRepository
import io.coursepick.coursepick.domain.search.SearchRepository

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds
    abstract fun bindCourseRepository(implementation: DefaultCourseRepository): CourseRepository

    @Binds
    abstract fun bindFavoriteCourseRepository(implementation: DefaultFavoriteCourseRepository): FavoriteCourseRepository

    @Binds
    abstract fun bindSearchRepository(implementation: DefaultSearchRepository): SearchRepository

    @Binds
    abstract fun bindNoticeRepository(implementation: DefaultNoticeRepository): NoticeRepository

    @Binds
    abstract fun bindAuthRepository(implementation: DefaultAuthRepository): AuthRepository

    @Binds
    abstract fun bindCustomCourseRepository(implementation: DefaultCustomCourseRepository): CustomCourseRepository

    @Binds
    abstract fun bindPreferencesRepository(implementation: DefaultPreferencesRepository): PreferencesRepository
}
