package io.coursepick.coursepick.presentation.fixtures

import io.coursepick.coursepick.domain.favorites.FavoritesRepository

class FakeFavoritesRepository : FavoritesRepository {
    private val favoriteCourseIds: MutableSet<String> = mutableSetOf()

    override fun favoriteCourseIds(): Set<String> = favoriteCourseIds

    override fun addFavoriteCourse(courseId: String) {
        favoriteCourseIds.add(courseId)
    }

    override fun removeFavoriteCourse(courseId: String) {
        favoriteCourseIds.remove(courseId)
    }
}
