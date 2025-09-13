package io.coursepick.coursepick.data.favorites

import io.coursepick.coursepick.domain.favorites.FavoritesRepository
import io.coursepick.coursepick.presentation.preference.CoursePickPreferences

class DefaultFavoritesRepository : FavoritesRepository {
    override fun likedCourseIds(): Set<String> = CoursePickPreferences.likedCourseIds()

    override fun likeCourse(courseId: String) {
        CoursePickPreferences.likeCourse(courseId)
    }

    override fun unlikeCourse(courseId: String) {
        CoursePickPreferences.unlikeCourse(courseId)
    }
}
