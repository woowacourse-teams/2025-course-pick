package io.coursepick.coursepick.presentation.customcourse

import io.coursepick.coursepick.domain.course.Course

data class CustomCourseUiModel(
    val course: Course,
    val selected: Boolean,
)
