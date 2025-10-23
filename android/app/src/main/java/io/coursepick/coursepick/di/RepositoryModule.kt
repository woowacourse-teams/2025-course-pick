package io.coursepick.coursepick.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.coursepick.coursepick.data.course.DefaultCourseRepository
import io.coursepick.coursepick.data.favorites.DefaultFavoritesRepository
import io.coursepick.coursepick.data.search.DefaultSearchRepository
import io.coursepick.coursepick.domain.course.CourseRepository
import io.coursepick.coursepick.domain.favorites.FavoritesRepository
import io.coursepick.coursepick.domain.search.SearchRepository

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds
    abstract fun bindCourseRepository(implementation: DefaultCourseRepository): CourseRepository

    @Binds
    abstract fun bindFavoritesRepository(implementation: DefaultFavoritesRepository): FavoritesRepository

    @Binds
    abstract fun bindSearchRepository(implementation: DefaultSearchRepository): SearchRepository
}
