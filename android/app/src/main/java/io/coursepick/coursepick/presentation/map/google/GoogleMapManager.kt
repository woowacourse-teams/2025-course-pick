package io.coursepick.coursepick.presentation.map.google

import com.google.android.gms.maps.CameraUpdate
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.PointOfInterest
import com.google.android.gms.maps.model.Polyline
import io.coursepick.coursepick.R
import io.coursepick.coursepick.domain.course.Coordinate
import io.coursepick.coursepick.domain.course.Scope
import io.coursepick.coursepick.domain.customcourse.DraftSegment
import io.coursepick.coursepick.domain.location.Location
import io.coursepick.coursepick.presentation.Logger
import io.coursepick.coursepick.presentation.course.CourseItem
import io.coursepick.coursepick.presentation.map.CameraMoveReason
import io.coursepick.coursepick.presentation.map.DistanceCalculator
import io.coursepick.coursepick.presentation.map.MapManager
import timber.log.Timber

class GoogleMapManager(
    private val mapFragment: SupportMapFragment,
) : MapManager {
    private var map: GoogleMap? = null
    private var drawer: GoogleMapDrawer? = null

    override val cameraCoordinate: Coordinate?
        get() =
            map?.let { map: GoogleMap -> map.cameraPosition.target.toCoordinate() } ?: run {
                Timber.w("${GoogleMap::class.simpleName} is null.")
                null
            }

    override val scope: Scope?
        get() =
            map?.let { map: GoogleMap ->
                val center =
                    map.projection.visibleRegion.latLngBounds.center
                        .toCoordinate()
                val topLeft =
                    map.projection.visibleRegion.farLeft
                        .toCoordinate()
                DistanceCalculator.distance(center, topLeft)?.let(Scope::invoke)
            } ?: run {
                Timber.w("${GoogleMap::class.simpleName} is null.")
                null
            }

    override fun startMap(onMapReady: () -> Unit) {
        mapFragment.getMapAsync { map: GoogleMap ->
            this.map = map
            drawer = GoogleMapDrawer(mapFragment.requireContext(), map)

            map.setMapStyle(
                MapStyleOptions.loadRawResourceStyle(
                    mapFragment.requireContext(),
                    R.raw.google_map_style,
                ),
            )
            map.uiSettings.isCompassEnabled = false
            setLogger()

            onMapReady()
        }
    }

    override fun draw(course: CourseItem) {
        drawer?.drawCourse(course)
            ?: run { Timber.w("${GoogleMapDrawer::class.simpleName} is null.") }
    }

    override fun draw(courses: List<CourseItem>) {
        drawer?.let { drawer: GoogleMapDrawer -> courses.forEach(drawer::drawCourse) }
            ?: run { Timber.w("${GoogleMapDrawer::class.simpleName} is null.") }
    }

    override fun drawRouteToCourse(
        route: List<Coordinate>,
        course: CourseItem,
    ) {
        drawer?.drawRouteToCourse(route, course)
            ?: run { Timber.w("${GoogleMapDrawer::class.simpleName} is null.") }
    }

    override fun removeAllRouteLines() {
        drawer?.removeAllRouteLines()
            ?: run { Timber.w("${GoogleMapDrawer::class.simpleName} is null.") }
    }

    override fun drawSearchCoordinate(coordinate: Coordinate) {
        drawer?.drawSearchCoordinate(coordinate)
            ?: run { Timber.w("${GoogleMapDrawer::class.simpleName} is null.") }
    }

    override fun drawUserLocation(location: Location) {
        drawer?.drawUserLocation(location)
            ?: run { Timber.w("${GoogleMapDrawer::class.simpleName} is null.") }
    }

    override fun hideUserLocation() {
        drawer?.hideUserLocation()
            ?: run { Timber.w("${GoogleMapDrawer::class.simpleName} is null.") }
    }

    override fun drawWaypoint(coordinate: Coordinate) {
        TODO("Not yet implemented")
    }

    override fun removeLastWaypoint() {
        TODO("Not yet implemented")
    }

    override fun clearWaypoints() {
        TODO("Not yet implemented")
    }

    override fun drawDraftSegment(segment: DraftSegment) {
        TODO("Not yet implemented")
    }

    override fun clearDraftSegments() {
        TODO("Not yet implemented")
    }

    override fun fitTo(coordinates: List<Coordinate>) {
        map?.let { map: GoogleMap ->
            val bounds =
                LatLngBounds
                    .builder()
                    .apply {
                        coordinates.forEach { coordinate: Coordinate ->
                            include(coordinate.toLatLng())
                        }
                    }.build()

            map.animateCamera(
                CameraUpdateFactory.newLatLngBounds(
                    bounds,
                    mapFragment.requireContext().resources.getDimensionPixelSize(R.dimen.course_route_padding),
                ),
                MOVE_ANIMATION_DURATION_MS.toInt(),
                null,
            )
        } ?: run { Timber.w("${GoogleMap::class.simpleName} is null.") }
    }

    override fun fitTo(course: CourseItem) {
        fitTo(course.coordinates)
    }

    override fun setOnCourseClickListener(onClick: (CourseItem) -> Unit) {
        map?.let { map: GoogleMap ->
            map.setOnPolylineClickListener { polyline: Polyline ->
                (polyline.tag as? CourseItem)?.let { course: CourseItem ->
                    Logger.log(
                        Logger.Event.Click("course_on_map"),
                        "id" to course.id,
                        "name" to course.name,
                    )
                    onClick(course)
                }
            }
        } ?: run { Timber.w("${GoogleMap::class.simpleName} is null.") }
    }

    override fun setOnCameraMoveListener(onCameraMove: (coordinate: Coordinate, reason: CameraMoveReason) -> Unit) {
        map?.let { map: GoogleMap ->
            map.setOnCameraMoveStartedListener { reason: Int ->
                onCameraMove(
                    map.cameraPosition.target.toCoordinate(),
                    if (reason == CAMERA_MOVE_REASON_GESTURE) {
                        CameraMoveReason.GESTURE
                    } else {
                        CameraMoveReason.SYSTEM
                    },
                )
            }
        } ?: run { Timber.w("${GoogleMap::class.simpleName} is null.") }
    }

    override fun moveTo(
        coordinate: Coordinate,
        animate: Boolean,
    ) {
        map?.let { map: GoogleMap ->
            val cameraUpdate: CameraUpdate = CameraUpdateFactory.newLatLng(coordinate.toLatLng())
            if (animate) {
                map.animateCamera(
                    cameraUpdate,
                    MOVE_ANIMATION_DURATION_MS.toInt(),
                    null,
                )
            } else {
                map.moveCamera(cameraUpdate)
            }
        } ?: run { Timber.w("${GoogleMap::class.simpleName} is null.") }
    }

    override fun resetZoom() {
        map?.let { map: GoogleMap ->
            map.moveCamera(CameraUpdateFactory.zoomTo(DEFAULT_ZOOM_LEVEL))
        } ?: run { Timber.w("${GoogleMap::class.simpleName} is null.") }
    }

    override fun setPadding(
        left: Int,
        top: Int,
        right: Int,
        bottom: Int,
    ) {
        map?.let { map: GoogleMap ->
            map.setPadding(left, top, right, bottom)
        } ?: run { Timber.w("${GoogleMap::class.simpleName} is null.") }
    }

    private fun setLogger() {
        map?.let { map: GoogleMap ->
            map.setOnPoiClickListener { poi: PointOfInterest ->
                Logger.log(
                    Logger.Event.Click("map_poi"),
                    "point_of_interest" to poi.name,
                    "latitude" to poi.latLng.latitude,
                    "longitude" to poi.latLng.longitude,
                )
            }

            map.setOnMapClickListener { latLng: LatLng ->
                Logger.log(
                    Logger.Event.Click("map"),
                    "latitude" to latLng.latitude,
                    "longitude" to latLng.longitude,
                )
            }
        } ?: run { Timber.w("${GoogleMap::class.simpleName} is null.") }
    }

    companion object {
        private const val MOVE_ANIMATION_DURATION_MS = 750L
        private const val DEFAULT_ZOOM_LEVEL = 15F
        private const val CAMERA_MOVE_REASON_GESTURE = 1
    }
}
