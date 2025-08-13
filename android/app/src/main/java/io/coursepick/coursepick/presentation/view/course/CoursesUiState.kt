package io.coursepick.coursepick.presentation.view.course

import io.coursepick.coursepick.presentation.model.course.CourseItem

data class CoursesUiState(
    val courses: List<CourseItem>,
    val isLoading: Boolean = false,
    val isFailure: Boolean = false,
) {
    val areCoursesEmpty: Boolean = courses.isEmpty()
}
