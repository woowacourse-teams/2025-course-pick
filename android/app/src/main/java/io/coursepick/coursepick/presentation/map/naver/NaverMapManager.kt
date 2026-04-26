package io.coursepick.coursepick.presentation.map.naver

import io.coursepick.coursepick.domain.course.Coordinate
import io.coursepick.coursepick.domain.course.Scope
import io.coursepick.coursepick.domain.location.Location
import io.coursepick.coursepick.presentation.course.CourseItem
import io.coursepick.coursepick.presentation.map.MapManager

class NaverMapManager : MapManager {
    override val cameraCoordinate: Coordinate
        get() = TODO("Not yet implemented")
    override val scope: Scope
        get() = TODO("Not yet implemented")

    override fun startMap(onMapReady: () -> Unit) {
        TODO("Not yet implemented")
    }

    override fun draw(course: CourseItem) {
        TODO("Not yet implemented")
    }

    override fun draw(courses: List<CourseItem>) {
        TODO("Not yet implemented")
    }

    override fun drawRouteToCourse(
        route: List<Coordinate>,
        course: CourseItem,
    ) {
        TODO("Not yet implemented")
    }

    override fun removeAllRouteLines() {
        TODO("Not yet implemented")
    }

    override fun drawSearchCoordinate(coordinate: Coordinate) {
        TODO("Not yet implemented")
    }

    override fun drawUserLocation(location: Location) {
        TODO("Not yet implemented")
    }

    override fun hideUserLocation() {
        TODO("Not yet implemented")
    }

    override fun fitTo(coordinates: List<Coordinate>) {
        TODO("Not yet implemented")
    }

    override fun fitTo(course: CourseItem) {
        TODO("Not yet implemented")
    }

    override fun setOnCourseClickListener(onClick: (CourseItem) -> Unit) {
        TODO("Not yet implemented")
    }

    override fun setOnCameraMoveListener(onCameraMove: () -> Unit) {
        TODO("Not yet implemented")
    }

    override fun moveTo(coordinate: Coordinate) {
        TODO("Not yet implemented")
    }

    override fun resetZoom() {
        TODO("Not yet implemented")
    }

    override fun setPadding(
        left: Int,
        top: Int,
        right: Int,
        bottom: Int,
    ) {
        TODO("Not yet implemented")
    }
}
