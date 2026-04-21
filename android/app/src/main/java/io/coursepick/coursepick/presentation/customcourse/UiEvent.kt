package io.coursepick.coursepick.presentation.customcourse

sealed interface UiEvent {
    data object NavigateToCreateCourse : UiEvent
}
