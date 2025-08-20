package io.coursepick.coursepick.presentation.course

data class CoursesUiState(
    val query: String,
    val courses: List<CourseItem>,
    val isLoading: Boolean = false,
    val isFailure: Boolean = false,
) {
    val isQueryBlank: Boolean = query.isBlank()
    val areCoursesEmpty: Boolean = courses.isEmpty()
}
