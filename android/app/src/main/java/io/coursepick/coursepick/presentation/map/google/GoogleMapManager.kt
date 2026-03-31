package io.coursepick.coursepick.presentation.map.google

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import io.coursepick.coursepick.domain.course.Coordinate
import io.coursepick.coursepick.domain.course.Latitude
import io.coursepick.coursepick.domain.course.Longitude
import io.coursepick.coursepick.domain.course.Scope
import io.coursepick.coursepick.domain.location.Location
import io.coursepick.coursepick.presentation.course.CourseItem
import io.coursepick.coursepick.presentation.map.MapManager

class GoogleMapManager(
    private val map: GoogleMap,
) : MapManager {
    override val cameraCoordinate: Coordinate
        get() = Coordinate(Latitude(0.0), Longitude(0.0))
    override val scope: Scope
        get() = Scope(0)

    override fun startMap(onMapReady: () -> Unit) {
        map.moveCamera(
            CameraUpdateFactory.newCameraPosition(
                CameraPosition.builder().target(DEFAULT_LATLNG).build(),
            ),
        )
    }

    override fun draw(course: CourseItem) {
    }

    override fun draw(courses: List<CourseItem>) {
    }

    override fun drawRouteToCourse(
        route: List<Coordinate>,
        course: CourseItem,
    ) {
    }

    override fun removeAllRouteLines() {
    }

    override fun drawSearchCoordinate(coordinate: Coordinate) {
    }

    override fun drawUserLocation(location: Location) {
    }

    override fun hideUserLocation() {
    }

    override fun fitTo(coordinates: List<Coordinate>) {
    }

    override fun fitTo(course: CourseItem) {
    }

    override fun setOnCourseClickListener(onClick: (CourseItem) -> Unit) {
    }

    override fun setOnCameraMoveListener(onCameraMove: () -> Unit) {
    }

    override fun moveTo(coordinate: Coordinate) {
    }

    override fun resetZoom() {
    }

    override fun setPadding(
        left: Int,
        top: Int,
        right: Int,
        bottom: Int,
    ) {
    }

    companion object {
        private const val DEFAULT_LATITUDE = 37.5100226
        private const val DEFAULT_LONGITUDE = 127.1026170
        private val DEFAULT_LATLNG = LatLng(DEFAULT_LATITUDE, DEFAULT_LONGITUDE)
    }
}
