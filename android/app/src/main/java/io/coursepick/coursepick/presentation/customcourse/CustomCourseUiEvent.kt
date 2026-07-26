package io.coursepick.coursepick.presentation.customcourse

sealed interface CustomCourseUiEvent {
    data object NoNetwork : CustomCourseUiEvent

    data object UnauthorizedUser : CustomCourseUiEvent

    data object UnknownFailure : CustomCourseUiEvent

    data object AuthenticationSuccess : CustomCourseUiEvent

    data object AuthenticationCancelled : CustomCourseUiEvent

    data object AuthenticationFailure : CustomCourseUiEvent

    data object NavigateToCreateCourse : CustomCourseUiEvent

    object FetchCustomCourseFailure : CustomCourseUiEvent

    data object DeleteCourseSuccess : CustomCourseUiEvent

    data class SelectCustomCourse(
        val customCourse: CustomCourseUiModel,
    ) : CustomCourseUiEvent
}
