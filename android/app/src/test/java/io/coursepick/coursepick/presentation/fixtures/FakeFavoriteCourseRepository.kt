package io.coursepick.coursepick.presentation.fixtures

import io.coursepick.coursepick.domain.favorites.FavoriteCourseRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class FakeFavoriteCourseRepository : FavoriteCourseRepository {
    private val _favoriteCourseIds = MutableStateFlow<Set<String>>(emptySet())
    override val favoriteCourseIds: Flow<Set<String>> = _favoriteCourseIds.asStateFlow()

    override suspend fun addFavorite(courseId: String) {
        _favoriteCourseIds.value += courseId
    }

    override suspend fun removeFavorite(courseId: String) {
        _favoriteCourseIds.value -= courseId
    }
}
