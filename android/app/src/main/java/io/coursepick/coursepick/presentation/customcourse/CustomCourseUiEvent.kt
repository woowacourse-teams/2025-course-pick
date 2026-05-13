package io.coursepick.coursepick.presentation.customcourse

sealed interface CustomCourseUiEvent {
    data object NavigateToCreateCourse : CustomCourseUiEvent

    object FetchCustomCourseFailure : CustomCourseUiEvent

    object NoNetworkConnection : CustomCourseUiEvent
}
