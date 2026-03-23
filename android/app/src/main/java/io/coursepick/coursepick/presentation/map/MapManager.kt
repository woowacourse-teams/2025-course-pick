package io.coursepick.coursepick.presentation.map

import io.coursepick.coursepick.domain.course.Coordinate
import io.coursepick.coursepick.domain.course.Scope
import io.coursepick.coursepick.domain.location.Location
import io.coursepick.coursepick.presentation.course.CourseItem

interface MapManager {
    val cameraPosition: Coordinate?

    val scope: Scope?

    fun startMap(onMapReady: () -> Unit)

    fun draw(course: CourseItem)

    fun draw(courses: List<CourseItem>)

    fun drawRouteToCourse(
        route: List<Coordinate>,
        course: CourseItem,
    )

    fun removeAllRouteLines()

    fun drawSearchPosition(coordinate: Coordinate)

    fun drawUserPosition(location: Location)

    fun hideUserPosition()

    fun fitTo(coordinates: List<Coordinate>)

    fun fitTo(course: CourseItem)

    fun setOnCourseClickListener(onClick: (CourseItem) -> Unit)

    fun setOnCameraMoveListener(onCameraMove: () -> Unit)

    fun moveTo(coordinate: Coordinate)

    fun resetZoom()

    fun setPadding(
        left: Int,
        top: Int,
        right: Int,
        bottom: Int,
    )
}
