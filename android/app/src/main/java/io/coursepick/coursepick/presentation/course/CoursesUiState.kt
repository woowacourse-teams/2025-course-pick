package io.coursepick.coursepick.presentation.course

data class CoursesUiState(
    val courses: List<CourseItem>,
    val query: String = "",
    val isLoading: Boolean = false,
    val isFailure: Boolean = false,
) {
    val isQueryBlank: Boolean get() = query.isBlank()
    val areCoursesEmpty: Boolean get() = courses.isEmpty()
}
