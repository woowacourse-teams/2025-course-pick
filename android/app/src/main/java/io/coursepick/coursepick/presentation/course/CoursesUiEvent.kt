package io.coursepick.coursepick.presentation.course

import io.coursepick.coursepick.domain.course.Coordinate
import io.coursepick.coursepick.domain.notice.Notice
import io.coursepick.coursepick.presentation.routefinder.RouteFinderApplication

sealed interface CoursesUiEvent {
    object FetchCourseFailure : CoursesUiEvent

    class SelectCourseManually(
        val course: CourseItem,
    ) : CoursesUiEvent

    class FetchRouteToCourseSuccess(
        val route: List<Coordinate>,
        val course: CourseItem,
    ) : CoursesUiEvent

    object FetchRouteToCourseFailure : CoursesUiEvent

    class FetchNearestCoordinateSuccess(
        val origin: Coordinate,
        val destination: Coordinate,
        val destinationName: String,
        val routeFinder: RouteFinderApplication.ThirdParty,
    ) : CoursesUiEvent

    object FetchNearestCoordinateFailure : CoursesUiEvent

    class ShowNotice(
        val notice: Notice,
    ) : CoursesUiEvent
}
