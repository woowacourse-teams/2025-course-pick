package io.coursepick.coursepick.presentation.filter

data class FilterUiState(
    val courseFilter: CourseFilter = CourseFilter(),
    val filteredCourseCount: Int = 0,
)
