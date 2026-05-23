package io.coursepick.coursepick.domain.favorites

import kotlinx.coroutines.flow.Flow

interface FavoritesRepository {
    val favoriteCourseIds: Flow<Set<String>>

    suspend fun addFavorite(courseId: String)

    suspend fun removeFavorite(courseId: String)
}
