package io.coursepick.coursepick.domain.favorites

interface FavoritesRepository {
    fun likedCourseIds(): Set<String>

    fun likeCourse(courseId: String)

    fun unlikeCourse(courseId: String)
}
