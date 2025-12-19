package io.coursepick.coursepick.domain.course

data class CoursesPage(
    val courses: List<Course>,
    val hasNext: Boolean,
)
