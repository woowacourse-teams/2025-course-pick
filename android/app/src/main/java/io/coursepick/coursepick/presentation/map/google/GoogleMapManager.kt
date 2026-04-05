package io.coursepick.coursepick.presentation.map.google

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.PointOfInterest
import com.google.android.gms.maps.model.Polyline
import io.coursepick.coursepick.R
import io.coursepick.coursepick.domain.course.Coordinate
import io.coursepick.coursepick.domain.course.Scope
import io.coursepick.coursepick.domain.location.Location
import io.coursepick.coursepick.presentation.Logger
import io.coursepick.coursepick.presentation.course.CourseItem
import io.coursepick.coursepick.presentation.map.DistanceCalculator
import io.coursepick.coursepick.presentation.map.MapManager
import timber.log.Timber

class GoogleMapManager(
    private val mapFragment: SupportMapFragment,
) : MapManager {
    private var map: GoogleMap? = null
    private var drawer: GoogleMapDrawer? = null

    override val cameraCoordinate: Coordinate? get() = withNullable(map) { cameraPosition.target.toCoordinate() }
    override val scope: Scope?
        get() =
            withNullable(map) {
                val center =
                    projection.visibleRegion.latLngBounds.center
                        .toCoordinate()
                val topLeft =
                    projection.visibleRegion.farLeft
                        .toCoordinate()
                DistanceCalculator.distance(center, topLeft)?.let(Scope::invoke)
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
            map.moveCamera(
                CameraUpdateFactory.newCameraPosition(
                    CameraPosition
                        .builder()
                        .target(DEFAULT_LATLNG)
                        .zoom(DEFAULT_ZOOM_LEVEL)
                        .build(),
                ),
            )
            setLogger()

            onMapReady()
        }
    }

    override fun draw(course: CourseItem) {
        withNullable(drawer) { drawCourse(course) }
    }

    override fun draw(courses: List<CourseItem>) {
        withNullable(drawer) { courses.forEach(::drawCourse) }
    }

    override fun drawRouteToCourse(
        route: List<Coordinate>,
        course: CourseItem,
    ) {
        withNullable(drawer) { drawRouteToCourse(route, course) }
    }

    override fun removeAllRouteLines() {
        withNullable(drawer) { removeAllRouteLines() }
    }

    override fun drawSearchCoordinate(coordinate: Coordinate) {
        withNullable(drawer) { drawSearchCoordinate(coordinate) }
    }

    override fun drawUserLocation(location: Location) {
        withNullable(drawer) { drawUserLocation(location) }
    }

    override fun hideUserLocation() {
        withNullable(drawer) { hideUserLocation() }
    }

    override fun fitTo(coordinates: List<Coordinate>) {
        withNullable(map) {
            val bounds =
                LatLngBounds
                    .builder()
                    .apply {
                        coordinates.forEach { coordinate: Coordinate ->
                            include(coordinate.toLatLng())
                        }
                    }.build()

            animateCamera(
                CameraUpdateFactory.newLatLngBounds(
                    bounds,
                    mapFragment.requireContext().resources.getDimensionPixelSize(R.dimen.course_route_padding),
                ),
                MOVE_ANIMATION_DURATION_MS.toInt(),
                null,
            )
        }
    }

    override fun fitTo(course: CourseItem) {
        fitTo(course.coordinates)
    }

    override fun setOnCourseClickListener(onClick: (CourseItem) -> Unit) {
        withNullable(map) {
            setOnPolylineClickListener { polyline: Polyline ->
                (polyline.tag as? CourseItem)?.let { course: CourseItem ->
                    Logger.log(
                        Logger.Event.Click("course_on_map"),
                        "id" to course.id,
                        "name" to course.name,
                    )
                    onClick(course)
                }
            }
        }
    }

    override fun setOnCameraMoveListener(onCameraMove: () -> Unit) {
        withNullable(map) {
            setOnCameraMoveStartedListener { reason: Int ->
                if (reason == CAMERA_MOVE_REASON_GESTURE) {
                    Logger.log(
                        Logger.Event.MapMoveStart("map"),
                        "latitude" to cameraPosition.target.latitude,
                        "longitude" to cameraPosition.target.longitude,
                    )
                    onCameraMove()
                }
            }
        }
    }

    override fun moveTo(coordinate: Coordinate) {
        withNullable(map) {
            animateCamera(
                CameraUpdateFactory.newLatLng(coordinate.toLatLng()),
                MOVE_ANIMATION_DURATION_MS.toInt(),
                null,
            )
        }
    }

    override fun resetZoom() {
        withNullable(map) { moveCamera(CameraUpdateFactory.zoomTo(DEFAULT_ZOOM_LEVEL)) }
    }

    override fun setPadding(
        left: Int,
        top: Int,
        right: Int,
        bottom: Int,
    ) {
        withNullable(map) { setPadding(left, top, right, bottom) }
    }

    private fun setLogger() {
        withNullable(map) {
            setOnPoiClickListener { poi: PointOfInterest ->
                Logger.log(
                    Logger.Event.Click("map_poi"),
                    "point_of_interest" to poi.name,
                    "latitude" to poi.latLng.latitude,
                    "longitude" to poi.latLng.longitude,
                )
            }

            setOnMapClickListener { latLng: LatLng ->
                Logger.log(
                    Logger.Event.Click("map"),
                    "latitude" to latLng.latitude,
                    "longitude" to latLng.longitude,
                )
            }
        }
    }

    private inline fun <reified T, R> withNullable(
        receiver: T?,
        block: T.() -> R,
    ): R? =
        receiver?.block() ?: run {
            Timber.w("${T::class.simpleName} is null.")
            null
        }

    companion object {
        private const val MOVE_ANIMATION_DURATION_MS = 750L
        private const val DEFAULT_LATITUDE = 37.5100226
        private const val DEFAULT_LONGITUDE = 127.1026170
        private val DEFAULT_LATLNG = LatLng(DEFAULT_LATITUDE, DEFAULT_LONGITUDE)
        private const val DEFAULT_ZOOM_LEVEL = 15F
        private const val CAMERA_MOVE_REASON_GESTURE = 1
    }
}
