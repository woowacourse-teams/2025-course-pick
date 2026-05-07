package io.coursepick.coursepick.presentation.map.google

import android.animation.ValueAnimator
import android.content.Context
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.Circle
import com.google.android.gms.maps.model.CircleOptions
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.Polyline
import com.google.android.gms.maps.model.PolylineOptions
import com.google.android.gms.maps.model.StrokeStyle
import com.google.android.gms.maps.model.StyleSpan
import com.google.android.gms.maps.model.TextureStyle
import io.coursepick.coursepick.R
import io.coursepick.coursepick.domain.course.Coordinate
import io.coursepick.coursepick.domain.customcourse.DraftSegment
import io.coursepick.coursepick.domain.location.Location
import io.coursepick.coursepick.presentation.course.CourseItem
import io.coursepick.coursepick.presentation.map.BitmapScaler
import io.coursepick.coursepick.presentation.map.CoordinateAnimator
import io.coursepick.coursepick.presentation.map.CourseDiffHandler

class GoogleMapDrawer(
    private val context: Context,
    private val map: GoogleMap,
) {
    private val courseDiffHandler = CourseDiffHandler(onItemAdded = ::addCoursePolyline, onItemRemoved = ::removeCoursePolyline)
    private val courseIdToPolyline = mutableMapOf<String, Polyline>()
    private var routePolyline: Polyline? = null

    private val waypoints = mutableListOf<Marker>()
    private val segments = mutableListOf<Polyline>()

    private var searchCoordinateMarker: Marker? = null
    private var fineUserLocationMarker: Marker? = null
    private var coarseUserLocationCircle: Circle? = null

    private val bitmapScaler = BitmapScaler(context)
    private val selectedCoursePattern: BitmapDescriptor =
        BitmapDescriptorFactory.fromBitmap(bitmapScaler.scaleDrawable(R.drawable.image_arrow, -1.0))
    private val searchCoordinateImage: BitmapDescriptor =
        BitmapDescriptorFactory.fromBitmap(
            bitmapScaler.scaleDrawableToHeight(
                R.drawable.image_search_location,
                context.resources.getDimension(R.dimen.search_coordinate_marker_height),
            ),
        )
    private val fineUserLocationImage: BitmapDescriptor =
        BitmapDescriptorFactory.fromBitmap(
            bitmapScaler.scaleDrawableToSize(
                R.drawable.image_current_location,
                context.resources.getDimension(R.dimen.fine_user_location_size),
                context.resources.getDimension(R.dimen.fine_user_location_size),
            ),
        )
    private val waypointImage: BitmapDescriptor =
        BitmapDescriptorFactory.fromBitmap(
            bitmapScaler.scaleDrawableToSize(
                R.drawable.icon_waypoint,
                context.resources.getDimension(R.dimen.waypoint_marker_size),
                context.resources.getDimension(R.dimen.waypoint_marker_size),
            ),
        )

    private var fineUserLocationAnimator: ValueAnimator? = null
    private var coarseUserLocationAnimator: ValueAnimator? = null

    fun updateCourses(courses: List<CourseItem>) {
        courseDiffHandler.updateCourses(courses.toSet())
    }

    private fun addCoursePolyline(course: CourseItem) {
        if (course.selected) {
            addSelectedCoursePolyline(course)
        } else {
            addUnselectedCoursePolyline(course)
        }
    }

    private fun addUnselectedCoursePolyline(course: CourseItem) {
        val options =
            PolylineOptions()
                .addAll(course.coordinates.map(Coordinate::toLatLng))
                .color(context.getColor(R.color.course_unselected))
                .width(context.resources.getDimension(R.dimen.unselected_course_width))
                .zIndex(UNSELECTED_COURSE_Z_INDEX)
                .clickable(true)

        courseIdToPolyline[course.id] = map.addPolyline(options).apply { tag = course }
    }

    private fun addSelectedCoursePolyline(course: CourseItem) {
        val courseStrokeStyle =
            StrokeStyle
                .colorBuilder(context.getColor(R.color.course_selected))
                .stamp(
                    TextureStyle
                        .newBuilder(selectedCoursePattern)
                        .build(),
                ).build()
        val courseOptions =
            PolylineOptions()
                .addAll(course.coordinates.map(Coordinate::toLatLng))
                .width(context.resources.getDimension(R.dimen.selected_course_width))
                .addSpan(StyleSpan(courseStrokeStyle, 0.1))
                .zIndex(SELECTED_COURSE_Z_INDEX)
                .clickable(true)

        courseIdToPolyline[course.id] = map.addPolyline(courseOptions).apply { tag = course }
    }

    private fun removeCoursePolyline(course: CourseItem) {
        courseIdToPolyline.remove(course.id)?.remove()
    }

    fun drawRoute(route: List<Coordinate>) {
        val options =
            PolylineOptions()
                .addAll(route.map(Coordinate::toLatLng))
                .width(context.resources.getDimension(R.dimen.course_route_width))
                .color(context.getColor(R.color.course_route))
        routePolyline = map.addPolyline(options)
    }

    fun clearRoute() {
        routePolyline?.remove()
        routePolyline = null
    }

    fun drawSearchCoordinate(coordinate: Coordinate) {
        searchCoordinateMarker?.let { marker: Marker ->
            marker.position = coordinate.toLatLng()
        } ?: run {
            searchCoordinateMarker =
                map.addMarker(
                    MarkerOptions()
                        .icon(searchCoordinateImage)
                        .position(coordinate.toLatLng())
                        .anchor(0.5F, 0.5F),
                )
        }
    }

    fun drawUserLocation(location: Location) {
        when (location) {
            is Location.Fine -> drawFineUserLocation(location)
            is Location.Coarse -> drawCoarseUserLocation(location)
        }
    }

    private fun drawFineUserLocation(location: Location.Fine) {
        hideCoarseUserLocation()

        fineUserLocationMarker?.let { marker: Marker ->
            fineUserLocationAnimator?.cancel()
            fineUserLocationAnimator =
                CoordinateAnimator.animator(
                    start = marker.position.toCoordinate(),
                    end = location.coordinate,
                ) { coordinate: Coordinate -> marker.position = coordinate.toLatLng() }
            fineUserLocationAnimator?.start()
        } ?: run {
            fineUserLocationMarker =
                map.addMarker(
                    MarkerOptions()
                        .icon(fineUserLocationImage)
                        .position(location.coordinate.toLatLng())
                        .anchor(0.5F, 0.5F),
                )
        }
    }

    private fun drawCoarseUserLocation(location: Location.Coarse) {
        hideFineUserLocation()

        coarseUserLocationCircle?.let { circle: Circle ->
            circle.radius = location.accuracy.meter.value
            coarseUserLocationAnimator?.cancel()
            coarseUserLocationAnimator =
                CoordinateAnimator.animator(
                    start = circle.center.toCoordinate(),
                    end = location.coordinate,
                ) { coordinate: Coordinate -> circle.center = coordinate.toLatLng() }
            coarseUserLocationAnimator?.start()
        } ?: run {
            coarseUserLocationCircle =
                map.addCircle(
                    CircleOptions()
                        .center(location.coordinate.toLatLng())
                        .radius(location.accuracy.meter.value)
                        .fillColor(context.getColor(R.color.coarse_location_area))
                        .strokeWidth(0F),
                )
        }
    }

    fun hideUserLocation() {
        hideFineUserLocation()
        hideCoarseUserLocation()
    }

    private fun hideFineUserLocation() {
        fineUserLocationMarker?.remove()
        fineUserLocationMarker = null

        fineUserLocationAnimator?.cancel()
        fineUserLocationAnimator = null
    }

    private fun hideCoarseUserLocation() {
        coarseUserLocationCircle?.remove()
        coarseUserLocationCircle = null

        coarseUserLocationAnimator?.cancel()
        coarseUserLocationAnimator = null
    }

    fun drawWaypoint(coordinate: Coordinate) {
        map
            .addMarker(
                MarkerOptions()
                    .icon(waypointImage)
                    .position(coordinate.toLatLng())
                    .anchor(0.5F, 0.5F),
            )?.also(waypoints::add)
    }

    fun removeLastWaypoint() {
        waypoints.removeLastOrNull()?.remove()
        segments.removeLastOrNull()?.remove()
    }

    fun clearWaypoints() {
        waypoints.forEach(Marker::remove)
        waypoints.clear()
    }

    fun drawDraftSegment(segment: DraftSegment) {
        val options =
            PolylineOptions()
                .addAll(segment.coordinates.map(Coordinate::toLatLng))
                .width(context.resources.getDimension(R.dimen.draft_segment_width))
                .color(context.getColor(R.color.course_draft))

        map.addPolyline(options).also(segments::add)
    }

    fun clearDraftSegments() {
        segments.forEach(Polyline::remove)
        segments.clear()
    }

    companion object {
        private const val UNSELECTED_COURSE_Z_INDEX = 0F
        private const val SELECTED_COURSE_Z_INDEX = 1F
    }
}
