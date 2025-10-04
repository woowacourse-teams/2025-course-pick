package io.coursepick.coursepick.presentation.course

import io.coursepick.coursepick.presentation.filter.CourseFilter

data class CoursesUiState(
    val courses: List<CourseItem>,
    val query: String = "",
    val isLoading: Boolean = false,
    val isFailure: Boolean = false,
    val isNoInternet: Boolean = false,
    val courseFilter: CourseFilter = CourseFilter(),
) {
    val isQueryBlank: Boolean get() = query.isBlank()
    val areCoursesEmpty: Boolean get() = courses.isEmpty() && courseFilter == CourseFilter()
    val isFilteredAndEmpty: Boolean get() = courses.isEmpty() && courseFilter != CourseFilter()
    val filteredCount: Int get() = courses.size
}
