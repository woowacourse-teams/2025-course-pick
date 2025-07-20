package io.coursepick.coursepick.view

sealed interface MainUiEvent {
    class FetchCourseSuccess(
        val course: CourseItem,
    ) : MainUiEvent

    object FetchCourseFailure : MainUiEvent

    class SelectNewCourse(
        val course: CourseItem,
    ) : MainUiEvent
}
