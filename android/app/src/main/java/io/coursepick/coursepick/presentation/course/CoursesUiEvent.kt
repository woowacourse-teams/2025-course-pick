package io.coursepick.coursepick.presentation.course

import io.coursepick.coursepick.domain.course.Coordinate

sealed interface CoursesUiEvent {
    object FetchCourseFailure : CoursesUiEvent

    class SelectNewCourse(
        val course: CourseItem,
    ) : CoursesUiEvent

    class FetchRouteToCourseSuccess(
        val origin: Coordinate,
        val route: List<Coordinate>,
    ) : CoursesUiEvent

    object FetchRouteToCourseFailure : CoursesUiEvent

    class FetchNearestCoordinateSuccess(
        val origin: Coordinate,
        val destination: Coordinate,
        val destinationName: String,
    ) : CoursesUiEvent

    object FetchNearestCoordinateFailure : CoursesUiEvent
}
