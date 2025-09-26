package io.coursepick.coursepick.data.favorites

import io.coursepick.coursepick.domain.favorites.FavoritesRepository
import io.coursepick.coursepick.presentation.preference.CoursePickPreferences

class DefaultFavoritesRepository : FavoritesRepository {
    override fun favoriteCourseIds(): Set<String> = CoursePickPreferences.favoritedCourseIds()

    override fun addFavoriteCourse(courseId: String) {
        CoursePickPreferences.addFavorite(courseId)
    }

    override fun removeFavoriteCourse(courseId: String) {
        CoursePickPreferences.removeFavorite(courseId)
    }
}
