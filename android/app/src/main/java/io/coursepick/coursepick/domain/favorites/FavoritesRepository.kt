package io.coursepick.coursepick.domain.favorites

interface FavoritesRepository {
    fun favoriteCourseIds(): Set<String>

    fun addFavoriteCourse(courseId: String)

    fun removeFavoriteCourse(courseId: String)
}
