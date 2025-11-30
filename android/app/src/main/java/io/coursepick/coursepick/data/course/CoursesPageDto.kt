package io.coursepick.coursepick.data.course

import kotlinx.serialization.Serializable

@Serializable
data class CoursesPageDto(
    val hasNext: Boolean,
    val courses: List<CourseDto>,
)
