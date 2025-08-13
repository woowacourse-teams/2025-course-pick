package io.coursepick.coursepick.presentation

data class MainUiState(
    val courses: List<CourseItem>,
    val isLoading: Boolean = false,
    val isFailure: Boolean = false,
) {
    val areCoursesEmpty: Boolean = courses.isEmpty()
}
