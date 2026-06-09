package io.coursepick.coursepick.presentation.customcourse

sealed interface CustomCourseUiEvent {
    data object AuthenticationSuccess : CustomCourseUiEvent

    data object AuthenticationCancelled : CustomCourseUiEvent

    data object AuthenticationFailure : CustomCourseUiEvent

    data object NavigateToCreateCourse : CustomCourseUiEvent

    object FetchCustomCourseFailure : CustomCourseUiEvent

    data object UnauthorizedUser : CustomCourseUiEvent

    data class SelectCustomCourse(
        val customCourse: CustomCourseItem,
    ) : CustomCourseUiEvent
}
