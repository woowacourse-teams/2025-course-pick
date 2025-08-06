package io.coursepick.coursepick.view

sealed interface MainUiEvent {
    class FetchCourseSuccess(
        val nearestCourse: CourseItem?,
    ) : MainUiEvent

    object FetchCourseFailure : MainUiEvent

    class SelectNewCourse(
        val course: CourseItem,
    ) : MainUiEvent
}
