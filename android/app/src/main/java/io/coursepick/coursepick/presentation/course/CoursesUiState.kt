package io.coursepick.coursepick.presentation.course

data class CoursesUiState(
    val courses: List<CourseItem>,
    val query: String = "",
    val status: UiStatus = UiStatus.Loading,
) {
    val isQueryBlank: Boolean get() = query.isBlank()
}
