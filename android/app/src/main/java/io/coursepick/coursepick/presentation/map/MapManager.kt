package io.coursepick.coursepick.presentation.map

import io.coursepick.coursepick.domain.course.Coordinate
import io.coursepick.coursepick.domain.course.Scope
import io.coursepick.coursepick.domain.customcourse.DraftSegment
import io.coursepick.coursepick.domain.location.Location
import io.coursepick.coursepick.presentation.course.CourseUiModel

interface MapManager {
    val cameraCoordinate: Coordinate?

    val scope: Scope?

    fun startMap(onMapReady: () -> Unit)

    fun updateCourses(courses: List<CourseUiModel>)

    fun drawRoute(route: List<Coordinate>)

    fun clearRoute()

    fun drawSearchCoordinate(coordinate: Coordinate)

    fun drawUserLocation(location: Location)

    fun hideUserLocation()

    fun drawWaypoint(coordinate: Coordinate)

    fun removeLastWaypoint()

    fun clearWaypoints()

    fun drawDraftSegment(segment: DraftSegment)

    fun clearDraftSegments()

    fun fitTo(coordinates: List<Coordinate>)

    fun fitTo(course: CourseUiModel)

    fun setOnCourseClickListener(onClick: (CourseUiModel) -> Unit)

    fun setOnCameraMoveListener(onCameraMove: (coordinate: Coordinate, reason: CameraMoveReason) -> Unit)

    fun moveTo(
        coordinate: Coordinate,
        animate: Boolean,
    )

    fun resetZoom()

    fun setPadding(
        left: Int = 0,
        top: Int = 0,
        right: Int = 0,
        bottom: Int = 0,
    )
}
