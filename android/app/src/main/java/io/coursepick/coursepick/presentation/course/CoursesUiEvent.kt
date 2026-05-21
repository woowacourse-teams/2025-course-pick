package io.coursepick.coursepick.presentation.course

import io.coursepick.coursepick.domain.course.Coordinate
import io.coursepick.coursepick.domain.preferences.RouteFinder

sealed interface CoursesUiEvent {
    object FetchCourseFailure : CoursesUiEvent

    class SelectCourseManually(
        val course: CourseItem,
    ) : CoursesUiEvent

    class FetchRouteToCourseSuccess(
        val route: List<Coordinate>,
        val course: CourseItem,
    ) : CoursesUiEvent

    class LaunchThirdPartyRouteFinder(
        val course: CourseItem,
        val origin: Coordinate,
        val destination: Coordinate,
        val routeFinder: RouteFinder.ThirdParty,
    ) : CoursesUiEvent

    object FetchRouteToCourseFailure : CoursesUiEvent

    object NoNetworkConnection : CoursesUiEvent

    object FetchNextCoursesFailure : CoursesUiEvent

    object RequireFineLocationPermission : CoursesUiEvent

    object FetchCurrentLocationFailure : CoursesUiEvent
}
