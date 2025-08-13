package io.coursepick.coursepick.presentation.course

import io.coursepick.coursepick.domain.course.Coordinate

sealed interface CoursesUiEvent {
    class FetchCourseSuccess(
        val nearestCourse: CourseItem?,
    ) : CoursesUiEvent

    object FetchCourseFailure : CoursesUiEvent

    class SelectNewCourse(
        val course: CourseItem,
    ) : CoursesUiEvent

    class FetchNearestCoordinateSuccess(
        val origin: Coordinate,
        val destination: Coordinate,
        val destinationName: String,
    ) : CoursesUiEvent

    object FetchNearestCoordinateFailure : CoursesUiEvent
}
