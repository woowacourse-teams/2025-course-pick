package io.coursepick.coursepick.presentation.map

import io.coursepick.coursepick.domain.course.Coordinate
import io.coursepick.coursepick.domain.course.Scope
import io.coursepick.coursepick.domain.customcourse.DraftSegment
import io.coursepick.coursepick.domain.location.Location
import io.coursepick.coursepick.presentation.course.CourseItem

interface MapManager {
    val cameraCoordinate: Coordinate?

    val scope: Scope?

    fun startMap(onMapReady: () -> Unit)

    fun draw(course: CourseItem)

    fun draw(courses: List<CourseItem>)

    fun drawRouteToCourse(
        route: List<Coordinate>,
        course: CourseItem,
    )

    fun removeAllRouteLines()

    fun drawSearchCoordinate(coordinate: Coordinate)

    fun drawUserLocation(location: Location)

    fun hideUserLocation()

    fun drawWaypoint(coordinate: Coordinate)

    fun removeLastWaypoint()

    fun clearWaypoints()

    fun drawDraftSegment(segment: DraftSegment)

    fun clearDraftSegments()

    fun fitTo(coordinates: List<Coordinate>)

    fun fitTo(course: CourseItem)

    fun setOnCourseClickListener(onClick: (CourseItem) -> Unit)

    fun setOnCameraMoveListener(onCameraMove: (coordinate: Coordinate?, reason: CameraMoveReason) -> Unit)

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
