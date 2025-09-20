package io.coursepick.coursepick.presentation.fixtures

import io.coursepick.coursepick.domain.favorites.FavoritesRepository

class FakeFavoritesRepository : FavoritesRepository {
    private val favoritedCourseIds: MutableSet<String> = mutableSetOf()

    override fun favoritedCourseIds(): Set<String> = favoritedCourseIds

    override fun addFavorite(courseId: String) {
        favoritedCourseIds.add(courseId)
    }

    override fun removeFavorite(courseId: String) {
        favoritedCourseIds.remove(courseId)
    }
}
