package io.coursepick.coursepick.view

data class MainUiState(
    val courses: List<CourseItem>,
    val isLoading: Boolean = false,
    val isFailure: Boolean = false,
)
