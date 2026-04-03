package io.coursepick.coursepick.presentation.map.google

import android.content.Context
import android.graphics.Point
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.Polyline
import io.coursepick.coursepick.R
import io.coursepick.coursepick.domain.course.Coordinate
import io.coursepick.coursepick.domain.course.Scope
import io.coursepick.coursepick.domain.location.Location
import io.coursepick.coursepick.presentation.course.CourseItem
import io.coursepick.coursepick.presentation.map.DistanceCalculator
import io.coursepick.coursepick.presentation.map.MapManager

class GoogleMapManager(
    private val map: GoogleMap,
    private val context: Context,
) : MapManager {
    override val cameraCoordinate: Coordinate get() = map.cameraPosition.target.toCoordinate()
    override val scope: Scope?
        get() {
            map.projection.fromScreenLocation(Point(0, 0))
            val center =
                map.projection.visibleRegion.latLngBounds.center
                    .toCoordinate()
            val topLeft =
                map.projection.visibleRegion.farLeft
                    .toCoordinate()
            val distance = DistanceCalculator.distance(center, topLeft) ?: return null
            return Scope(distance)
        }

    private val drawer = GoogleMapDrawer(map, context)

    override fun startMap(onMapReady: () -> Unit) {
        map.setMapStyle(MapStyleOptions.loadRawResourceStyle(context, R.raw.google_map_style))
        map.uiSettings.isCompassEnabled = false
        map.moveCamera(
            CameraUpdateFactory.newCameraPosition(
                CameraPosition
                    .builder()
                    .target(DEFAULT_LATLNG)
                    .zoom(DEFAULT_ZOOM_LEVEL)
                    .build(),
            ),
        )

        onMapReady()
    }

    override fun draw(course: CourseItem) {
        drawer.drawCourse(course)
    }

    override fun draw(courses: List<CourseItem>) {
        courses.forEach(drawer::drawCourse)
    }

    override fun drawRouteToCourse(
        route: List<Coordinate>,
        course: CourseItem,
    ) {
        drawer.drawRouteToCourse(route, course)
    }

    override fun removeAllRouteLines() {
        drawer.removeAllRouteLines()
    }

    override fun drawSearchCoordinate(coordinate: Coordinate) {
        drawer.drawSearchCoordinate(coordinate)
    }

    override fun drawUserLocation(location: Location) {
        drawer.drawUserLocation(location)
    }

    override fun hideUserLocation() {
        drawer.hideUserLocation()
    }

    override fun fitTo(coordinates: List<Coordinate>) {
        map.animateCamera(
            CameraUpdateFactory.newLatLngBounds(
                LatLngBounds
                    .builder()
                    .apply { coordinates.forEach { coordinate: Coordinate -> this.include(coordinate.toLatLng()) } }
                    .build(),
                context.resources.getDimensionPixelSize(R.dimen.course_route_padding),
            ),
            MOVE_ANIMATION_DURATION_MS.toInt(),
            null,
        )
    }

    override fun fitTo(course: CourseItem) {
        fitTo(course.coordinates)
    }

    override fun setOnCourseClickListener(onClick: (CourseItem) -> Unit) {
        map.setOnPolylineClickListener { polyline: Polyline ->
            (polyline.tag as? CourseItem)?.let(onClick)
        }
    }

    override fun setOnCameraMoveListener(onCameraMove: () -> Unit) {
        map.setOnCameraMoveListener { onCameraMove() }
    }

    override fun moveTo(coordinate: Coordinate) {
        map.animateCamera(
            CameraUpdateFactory.newLatLng(coordinate.toLatLng()),
            MOVE_ANIMATION_DURATION_MS.toInt(),
            null,
        )
    }

    override fun resetZoom() {
        map.moveCamera(CameraUpdateFactory.zoomTo(DEFAULT_ZOOM_LEVEL))
    }

    override fun setPadding(
        left: Int,
        top: Int,
        right: Int,
        bottom: Int,
    ) {
        map.setPadding(left, top, right, bottom)
    }

    companion object {
        private const val MOVE_ANIMATION_DURATION_MS = 750L
        private const val DEFAULT_LATITUDE = 37.5100226
        private const val DEFAULT_LONGITUDE = 127.1026170
        private val DEFAULT_LATLNG = LatLng(DEFAULT_LATITUDE, DEFAULT_LONGITUDE)
        private const val DEFAULT_ZOOM_LEVEL = 15F
    }
}
