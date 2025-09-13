package io.coursepick.coursepick.data.favorites

import io.coursepick.coursepick.domain.favorites.FavoritesRepository
import io.coursepick.coursepick.presentation.preference.CoursePickPreferences

class DefaultFavoritesRepository : FavoritesRepository {
    override fun favoritedCourseIds(): Set<String> = CoursePickPreferences.favoritedCourseIds()

    override fun addFavorite(courseId: String) {
        CoursePickPreferences.addFavorite(courseId)
    }

    override fun removeFavorite(courseId: String) {
        CoursePickPreferences.removeFavorite(courseId)
    }
}
