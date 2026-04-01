package io.coursepick.coursepick.presentation.map.google

import android.animation.ValueAnimator
import android.content.Context
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.Circle
import com.google.android.gms.maps.model.CircleOptions
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.Polyline
import com.google.android.gms.maps.model.PolylineOptions
import io.coursepick.coursepick.R
import io.coursepick.coursepick.domain.course.Coordinate
import io.coursepick.coursepick.domain.course.Scope
import io.coursepick.coursepick.domain.location.Location
import io.coursepick.coursepick.presentation.course.CourseItem
import io.coursepick.coursepick.presentation.map.MapManager

class GoogleMapManager(
    private val map: GoogleMap,
    private val context: Context,
) : MapManager {
    override val cameraCoordinate: Coordinate get() = map.cameraPosition.target.toCoordinate()
    override val scope: Scope get() = Scope(1000)

    private val polylines = mutableListOf<Polyline>()
    private var fineUserLocationMarker: Marker? = null
    private var coarseUserLocationMarker: Circle? = null

    override fun startMap(onMapReady: () -> Unit) {
        map.moveCamera(
            CameraUpdateFactory.newCameraPosition(
                CameraPosition
                    .builder()
                    .target(DEFAULT_LATLNG)
                    .zoom(15f)
                    .build(),
            ),
        )

        onMapReady()
    }

    private fun drawCourse(course: CourseItem) {
        map
            .addPolyline(
                PolylineOptions()
                    .add(*course.coordinates.map(Coordinate::toLatLng).toTypedArray())
                    .clickable(true),
            ).apply { tag = course }
            .also(polylines::add)
    }

    override fun draw(course: CourseItem) {
        removeAllRouteLines()
        drawCourse(course)
    }

    override fun draw(courses: List<CourseItem>) {
        removeAllRouteLines()
        courses.forEach(::drawCourse)
    }

    override fun drawRouteToCourse(
        route: List<Coordinate>,
        course: CourseItem,
    ) {
        removeAllRouteLines()
        map
            .addPolyline(
                PolylineOptions()
                    .add(*course.coordinates.map(Coordinate::toLatLng).toTypedArray()),
            ).also(polylines::add)
        draw(course)
    }

    override fun removeAllRouteLines() {
        polylines.forEach(Polyline::remove)
    }

    override fun drawSearchCoordinate(coordinate: Coordinate) {
        map.addMarker(
            MarkerOptions()
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.image_search_location))
                .position(map.cameraPosition.target),
        )
    }

    override fun drawUserLocation(location: Location) {
        when (location) {
            is Location.Fine -> drawFineUserLocation(location)
            is Location.Coarse -> drawCoarseUserLocation(location)
        }
    }

    private fun animateLatLng(
        start: LatLng,
        end: LatLng,
        duration: Long,
        onChange: (latLng: LatLng) -> Unit,
    ) {
        val valueAnimator = ValueAnimator.ofFloat(0F, 1F).setDuration(duration)
        valueAnimator.addUpdateListener { animator: ValueAnimator ->
            val latitude =
                (end.latitude - start.latitude) * animator.animatedFraction + start.latitude
            val longitude =
                (end.longitude - start.longitude) * animator.animatedFraction + start.longitude
            onChange(LatLng(latitude, longitude))
        }
        valueAnimator.start()
    }

    private fun drawFineUserLocation(location: Location.Fine) {
        hideCoarseUserLocation()

        fineUserLocationMarker?.let { marker: Marker ->
            animateLatLng(
                start = marker.position,
                end = location.coordinate.toLatLng(),
                duration = MOVE_ANIMATION_DURATION_MS,
            ) { latLng: LatLng -> marker.position = latLng }
        } ?: run {
            fineUserLocationMarker =
                map.addMarker(
                    MarkerOptions()
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.image_current_location))
                        .position(location.coordinate.toLatLng()),
                )
        }
    }

    private fun drawCoarseUserLocation(location: Location.Coarse) {
        hideFineUserLocation()

        coarseUserLocationMarker?.let { marker: Circle ->
            animateLatLng(
                start = marker.center,
                end = location.coordinate.toLatLng(),
                duration = MOVE_ANIMATION_DURATION_MS,
            ) { latLng: LatLng -> marker.center = latLng }
        } ?: run {
            coarseUserLocationMarker =
                map.addCircle(
                    CircleOptions()
                        .center(location.coordinate.toLatLng())
                        .radius(location.accuracy.meter.value)
                        .fillColor(context.getColor(R.color.coarse_location_area))
                        .strokeWidth(0F),
                )
        }
    }

    override fun hideUserLocation() {
        hideFineUserLocation()
        hideCoarseUserLocation()
    }

    private fun hideFineUserLocation() {
        fineUserLocationMarker?.remove()
    }

    private fun hideCoarseUserLocation() {
        coarseUserLocationMarker?.remove()
    }

    override fun fitTo(coordinates: List<Coordinate>) {
        map.moveCamera(
            CameraUpdateFactory.newLatLngBounds(
                LatLngBounds
                    .builder()
                    .apply { coordinates.map(Coordinate::toLatLng).forEach(::include) }
                    .build(),
                100,
            ),
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
        map.moveCamera(CameraUpdateFactory.newLatLng(coordinate.toLatLng()))
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
        private const val MOVE_ANIMATION_DURATION_MS = 750L
        private const val DEFAULT_LATITUDE = 37.5100226
        private const val DEFAULT_LONGITUDE = 127.1026170
        private val DEFAULT_LATLNG = LatLng(DEFAULT_LATITUDE, DEFAULT_LONGITUDE)
    }
}
