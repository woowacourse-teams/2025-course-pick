package io.coursepick.coursepick.presentation.customcourse

import io.coursepick.coursepick.presentation.course.UiStatus

data class CustomCourseUiState(
    val status: UiStatus = UiStatus.Loading,
    val customCourses: List<CustomCourseItem>,
    val selectedCustomCourse: CustomCourseItem? = null,
)
