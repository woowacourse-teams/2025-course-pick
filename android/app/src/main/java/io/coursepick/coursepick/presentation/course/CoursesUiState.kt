package io.coursepick.coursepick.presentation.course

data class CoursesUiState(
    val courses: List<CourseItem>,
    val query: String = "",
    val isLoading: Boolean = false,
    val isFailure: Boolean = false,
) {
    val areCoursesEmpty: Boolean = courses.isEmpty()
    val isQueryBlank: Boolean = query.isBlank()
}
