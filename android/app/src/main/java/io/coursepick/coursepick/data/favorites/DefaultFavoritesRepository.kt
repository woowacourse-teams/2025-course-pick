package io.coursepick.coursepick.data.favorites

import io.coursepick.coursepick.data.preference.FavoritesDataSource
import io.coursepick.coursepick.domain.favorites.FavoritesRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class DefaultFavoritesRepository
    @Inject
    constructor(
        private val favoritesDataSource: FavoritesDataSource,
    ) : FavoritesRepository {
        override val favoriteCourseIds: Flow<Set<String>> = favoritesDataSource.courseIds

        override suspend fun addFavorite(courseId: String) {
            favoritesDataSource.addFavorite(courseId)
        }

        override suspend fun removeFavorite(courseId: String) {
            favoritesDataSource.removeFavorite(courseId)
        }
    }
