package io.coursepick.coursepick.presentation.map.naver

import android.content.Context
import android.graphics.PointF
import com.naver.maps.geometry.LatLng
import com.naver.maps.geometry.LatLngBounds
import com.naver.maps.map.CameraAnimation
import com.naver.maps.map.CameraPosition
import com.naver.maps.map.CameraUpdate
import com.naver.maps.map.CameraUpdate.REASON_GESTURE
import com.naver.maps.map.MapFragment
import com.naver.maps.map.NaverMap
import com.naver.maps.map.Symbol
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

class NaverMapManager(
    private val mapFragment: MapFragment,
) : MapManager {
    private var map: NaverMap? = null
    private var overlayManager: NaverMapOverlayManager? = null
    private val context: Context = mapFragment.requireContext()

    override val cameraCoordinate: Coordinate?
        get() =
            map?.let { map: NaverMap ->
                map.cameraPosition.target.toCoordinate()
            } ?: run {
                Timber.w(MAP_IS_NULL_MESSAGE)
                null
            }

    override val scope: Scope?
        get() =
            map?.let { map: NaverMap ->
                val center = map.projection.fromScreenLocation(PointF(0.5F, 0.5F)).toCoordinate()
                val topLeft = map.projection.fromScreenLocation(PointF(0F, 0F)).toCoordinate()
                DistanceCalculator.distance(center, topLeft)?.let(Scope::invoke)
            } ?: run {
                Timber.w(MAP_IS_NULL_MESSAGE)
                null
            }

    override fun startMap(onMapReady: () -> Unit) {
        mapFragment.getMapAsync { naverMap: NaverMap ->
            map = naverMap
            overlayManager = NaverMapOverlayManager(mapFragment.requireContext(), naverMap)
            naverMap.uiSettings.apply {
                isCompassEnabled = false
                isScaleBarEnabled = false
                isZoomControlEnabled = false
            }
            setLogger()
            onMapReady()
        }
    }

    override fun draw(course: CourseItem) {
        overlayManager?.drawCourse(course) ?: run { Timber.w(OVERLAY_MANAGER_IS_NULL_MESSAGE) }
    }

    override fun draw(courses: List<CourseItem>) {
        courses.forEach(::draw)
    }

    override fun drawRouteToCourse(
        route: List<Coordinate>,
        course: CourseItem,
    ) {
        overlayManager?.drawRouteToCourse(route, course) ?: run { Timber.w(OVERLAY_MANAGER_IS_NULL_MESSAGE) }
    }

    override fun removeAllRouteLines() {
        overlayManager?.removeAllRouteLines() ?: run { Timber.w(OVERLAY_MANAGER_IS_NULL_MESSAGE) }
    }

    override fun drawSearchCoordinate(coordinate: Coordinate) {
        overlayManager?.drawSearchCoordinate(coordinate) ?: run { Timber.w(OVERLAY_MANAGER_IS_NULL_MESSAGE) }
    }

    override fun drawUserLocation(location: Location) {
        overlayManager?.drawUserLocation(location) ?: run { Timber.w(OVERLAY_MANAGER_IS_NULL_MESSAGE) }
    }

    override fun hideUserLocation() {
        overlayManager?.hideUserLocation() ?: run { Timber.w(OVERLAY_MANAGER_IS_NULL_MESSAGE) }
    }

    override fun drawWaypoint(coordinate: Coordinate) {
        overlayManager?.drawWaypoint(coordinate) ?: run { Timber.w(OVERLAY_MANAGER_IS_NULL_MESSAGE) }
    }

    override fun removeLastWaypoint() {
        overlayManager?.removeLastWaypoint() ?: run { Timber.w(OVERLAY_MANAGER_IS_NULL_MESSAGE) }
    }

    override fun clearWaypoints() {
        overlayManager?.clearWaypoints() ?: run { Timber.w(OVERLAY_MANAGER_IS_NULL_MESSAGE) }
    }

    override fun drawDraftSegment(segment: DraftSegment) {
        overlayManager?.drawDraftSegment(segment) ?: run { Timber.w(OVERLAY_MANAGER_IS_NULL_MESSAGE) }
    }

    override fun clearDraftSegments() {
        overlayManager?.clearDraftSegments() ?: run { Timber.w(OVERLAY_MANAGER_IS_NULL_MESSAGE) }
    }

    override fun fitTo(coordinates: List<Coordinate>) {
        map?.moveCamera(
            CameraUpdate
                .fitBounds(
                    LatLngBounds.from(coordinates.map(Coordinate::toLatLng)),
                    context.resources.getDimension(R.dimen.course_route_padding).toInt(),
                ).animate(CameraAnimation.Easing, MOVE_ANIMATION_DURATION_MS),
        ) ?: run { Timber.w(MAP_IS_NULL_MESSAGE) }
    }

    override fun fitTo(course: CourseItem) {
        fitTo(course.coordinates)
    }

    override fun setOnCourseClickListener(onClick: (CourseItem) -> Unit) {
        overlayManager?.setOnCourseClickListener(onClick) ?: run { Timber.w(OVERLAY_MANAGER_IS_NULL_MESSAGE) }
    }

    override fun setOnCameraMoveListener(onCameraMove: (coordinate: Coordinate, reason: CameraMoveReason) -> Unit) {
        map?.let { map: NaverMap ->
            map.addOnCameraChangeListener { reason: Int, _ ->
                onCameraMove(
                    map.cameraPosition.target.toCoordinate(),
                    if (reason == REASON_GESTURE) CameraMoveReason.GESTURE else CameraMoveReason.SYSTEM,
                )
            }
        } ?: run { Timber.w(MAP_IS_NULL_MESSAGE) }
    }

    override fun moveTo(
        coordinate: Coordinate,
        animate: Boolean,
    ) {
        map?.let { map: NaverMap ->
            val cameraUpdate =
                CameraUpdate.toCameraPosition(CameraPosition(coordinate.toLatLng(), map.cameraPosition.zoom)).apply {
                    if (animate) animate(CameraAnimation.Easing, MOVE_ANIMATION_DURATION_MS)
                }
            map.moveCamera(cameraUpdate)
        } ?: run { Timber.w(MAP_IS_NULL_MESSAGE) }
    }

    override fun resetZoom() {
        map?.let { map: NaverMap ->
            map.moveCamera(CameraUpdate.zoomTo(DEFAULT_ZOOM_LEVEL))
        } ?: run { Timber.w(MAP_IS_NULL_MESSAGE) }
    }

    override fun setPadding(
        left: Int,
        top: Int,
        right: Int,
        bottom: Int,
    ) {
        map?.let { map: NaverMap ->
            map.setContentPadding(left, top, right, bottom)
        } ?: run { Timber.w(MAP_IS_NULL_MESSAGE) }
    }

    private fun setLogger() {
        map?.let { map: NaverMap ->
            map.onSymbolClickListener =
                NaverMap.OnSymbolClickListener { symbol: Symbol ->
                    Logger.log(
                        Logger.Event.Click("map_poi"),
                        "point_of_interest" to symbol.caption,
                        "latitude" to symbol.position.latitude,
                        "longitude" to symbol.position.longitude,
                    )
                    false
                }

            map.onMapClickListener =
                NaverMap.OnMapClickListener { _, latLng: LatLng ->
                    Logger.log(
                        Logger.Event.Click("map"),
                        "latitude" to latLng.latitude,
                        "longitude" to latLng.longitude,
                    )
                }
        } ?: run { Timber.w(MAP_IS_NULL_MESSAGE) }
    }

    companion object {
        private val MAP_IS_NULL_MESSAGE = "${NaverMap::class.simpleName} is null."
        private val OVERLAY_MANAGER_IS_NULL_MESSAGE = "${NaverMapOverlayManager::class.simpleName} is null."

        private const val MOVE_ANIMATION_DURATION_MS = 750L
        private const val DEFAULT_ZOOM_LEVEL = 15.0
    }
}
