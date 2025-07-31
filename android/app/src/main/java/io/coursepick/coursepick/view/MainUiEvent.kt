package io.coursepick.coursepick.view

import io.coursepick.coursepick.domain.Coordinate

sealed interface MainUiEvent {
    class FetchCourseSuccess(
        val nearestCourse: CourseItem?,
    ) : MainUiEvent

    object FetchCourseFailure : MainUiEvent

    class SelectNewCourse(
        val course: CourseItem,
    ) : MainUiEvent

    class FetchNearestCoordinateSuccess(
        val origin: Coordinate,
        val destination: Coordinate,
        val destinationName: String,
    ) : MainUiEvent

    object FetchNearestCoordinateFailure : MainUiEvent

    object Search : MainUiEvent
}
