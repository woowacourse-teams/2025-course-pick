package io.coursepick.coursepick.data.favorites

import io.coursepick.coursepick.domain.favorites.FavoriteCourseRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class DefaultFavoriteCourseRepository
    @Inject
    constructor(
        private val favoriteCourseDataSource: FavoriteCourseDataSource,
    ) : FavoriteCourseRepository {
        override val favoriteCourseIds: Flow<Set<String>> = favoriteCourseDataSource.courseIds

        override suspend fun addFavorite(courseId: String) {
            favoriteCourseDataSource.addFavorite(courseId)
        }

        override suspend fun removeFavorite(courseId: String) {
            favoriteCourseDataSource.removeFavorite(courseId)
        }
    }
