package io.coursepick.coursepick.presentation.course

data class CoursesUiState(
    val courses: List<CourseItem>,
    val query: String = "",
    val status: UiStatus = UiStatus.Initial,
) {
    val isQueryBlank: Boolean get() = query.isBlank()
}
