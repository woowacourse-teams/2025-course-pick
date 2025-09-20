package io.coursepick.coursepick.presentation.course

import io.coursepick.coursepick.presentation.filter.FilterCondition

data class CoursesUiState(
    val courses: List<CourseItem>,
    val query: String = "",
    val isLoading: Boolean = false,
    val isFailure: Boolean = false,
    val isNoInternet: Boolean = false,
    val filterCondition: FilterCondition = FilterCondition(),
) {
    val isQueryBlank: Boolean get() = query.isBlank()
    val areCoursesEmpty: Boolean get() = courses.isEmpty()
}
