package io.coursepick.coursepick.view

data class MainUiState(
    val courses: List<CourseItem>,
    val isLoading: Boolean = false,
    val isFailure: Boolean = false,
    val isNewPosition: Boolean = false,
) {
    val areCoursesEmpty: Boolean = courses.isEmpty()
}
