package io.coursepick.coursepick.presentation.course

import io.coursepick.coursepick.presentation.filter.CourseFilter

data class CoursesUiState(
    private val originalCourses: List<CourseItem>,
    val query: String = "",
    val status: UiStatus = UiStatus.Loading,
    val courseFilter: CourseFilter = CourseFilter.Default,
) {
    val isQueryBlank: Boolean = query.isBlank()
    val isFilterDefault: Boolean = courseFilter == CourseFilter.Default
    val courses: List<CourseItem> = originalCourses.filter(courseFilter::matches)
}
