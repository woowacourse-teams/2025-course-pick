package io.coursepick.coursepick.presentation.view.course

import io.coursepick.coursepick.domain.Coordinate
import io.coursepick.coursepick.presentation.model.course.CourseItem

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
