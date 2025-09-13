package io.coursepick.coursepick.presentation.fixtures

import io.coursepick.coursepick.domain.favorites.FavoritesRepository

class FakeFavoritesRepository : FavoritesRepository {
    private val likedCourseIds: MutableSet<String> = mutableSetOf()

    override fun likedCourseIds(): Set<String> = likedCourseIds

    override fun likeCourse(courseId: String) {
        likedCourseIds.add(courseId)
    }

    override fun unlikeCourse(courseId: String) {
        likedCourseIds.remove(courseId)
    }
}
