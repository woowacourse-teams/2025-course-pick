package io.coursepick.coursepick.data.course

import io.coursepick.coursepick.domain.course.CoursesPage
import kotlinx.serialization.Serializable

@Serializable
data class CoursesPageDto(
    val courses: List<CourseDto>,
    val hasNext: Boolean,
) {
    fun toCoursesPage(): CoursesPage =
        CoursesPage(
            courses = courses.mapNotNull(CourseDto::toCourseOrNull),
            hasNext = hasNext,
        )
}
