package io.coursepick.coursepick.domain.favorites

interface FavoritesRepository {
    fun favoritedCourseIds(): Set<String>

    fun addFavorite(courseId: String)

    fun removeFavorite(courseId: String)
}
