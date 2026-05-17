package io.coursepick.coursepick.presentation.customcourse

sealed interface CustomCourseUiEvent {
    data object NavigateToCreateCourse : CustomCourseUiEvent

    object FetchCustomCourseFailure : CustomCourseUiEvent

    object RequestFetch : CustomCourseUiEvent

    data object UnauthorizedUser : CustomCourseUiEvent

    data class SelectCustomCourse(
        val customCourse: CustomCourseItem,
    ) : CustomCourseUiEvent
}
