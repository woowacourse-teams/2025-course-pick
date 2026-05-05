package io.coursepick.coursepick.presentation.map.naver

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Bitmap
import android.graphics.PointF
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.NaverMap
import com.naver.maps.map.overlay.CircleOverlay
import com.naver.maps.map.overlay.Marker
import com.naver.maps.map.overlay.Overlay
import com.naver.maps.map.overlay.OverlayImage
import com.naver.maps.map.overlay.PathOverlay
import io.coursepick.coursepick.R
import io.coursepick.coursepick.domain.course.Coordinate
import io.coursepick.coursepick.domain.customcourse.DraftSegment
import io.coursepick.coursepick.domain.location.Location
import io.coursepick.coursepick.presentation.Logger
import io.coursepick.coursepick.presentation.course.CourseItem
import io.coursepick.coursepick.presentation.map.BitmapScaler
import io.coursepick.coursepick.presentation.map.CoordinateAnimator
import io.coursepick.coursepick.presentation.map.DiffHandler

class NaverMapOverlayManager(
    private val context: Context,
    private val map: NaverMap,
) {
    private val coursesDiffHandler = DiffHandler(onItemAdded = ::addCourseOverlay, onItemRemoved = ::removeCourseOverlay)
    private val courseIdToOverlay = mutableMapOf<String, PathOverlay>()
    private val courseIdToClickableOverlay = mutableMapOf<String, PathOverlay>()
    private var routeOverlay: PathOverlay? = null

    private val waypoints = mutableListOf<Marker>()
    private val segments = mutableListOf<PathOverlay>()

    private val bitmapScaler = BitmapScaler(context)
    private val selectedCoursePatternImage: Bitmap =
        bitmapScaler.scaleDrawableToWidth(
            R.drawable.image_arrow,
            context.resources.getDimension(R.dimen.selected_course_width),
        )
    private val searchCoordinateImage: Bitmap =
        bitmapScaler.scaleDrawableToHeight(
            R.drawable.image_search_location,
            context.resources.getDimension(R.dimen.search_coordinate_marker_height),
        )
    private val fineUserLocationImage: Bitmap =
        bitmapScaler.scaleDrawableToSize(
            R.drawable.image_current_location,
            context.resources.getDimension(R.dimen.fine_user_location_size),
            context.resources.getDimension(R.dimen.fine_user_location_size),
        )
    private val waypointImage: Bitmap =
        bitmapScaler.scaleDrawableToSize(
            R.drawable.icon_waypoint,
            context.resources.getDimension(R.dimen.waypoint_marker_size),
            context.resources.getDimension(R.dimen.waypoint_marker_size),
        )

    private var searchCoordinateMarker: Marker? = null
    private var fineUserLocationMarker: Marker? = null
    private var coarseUserLocationCircle: CircleOverlay? = null

    private var fineUserLocationAnimator: ValueAnimator? = null
    private var coarseUserLocationAnimator: ValueAnimator? = null

    private var courseClickListener: Overlay.OnClickListener? = null

    fun updateCourses(newCourses: List<CourseItem>) {
        coursesDiffHandler.updateItems(newCourses.toSet())
    }

    fun addCourseOverlay(course: CourseItem) {
        if (course.coordinates.size < 2) return
        val latLngs: List<LatLng> = course.coordinates.map(Coordinate::toLatLng)

        PathOverlay().apply {
            coords = latLngs
            outlineWidth = 0

            if (course.selected) {
                color = context.getColor(R.color.course_selected)
                width = context.resources.getDimension(R.dimen.selected_course_width).toInt()
                zIndex = SELECTED_COURSE_Z_INDEX
                patternImage = OverlayImage.fromBitmap(selectedCoursePatternImage)
                patternInterval = context.resources.getDimension(R.dimen.selected_course_pattern_interval_naver).toInt()
            } else {
                color = context.getColor(R.color.course_unselected)
                width = context.resources.getDimension(R.dimen.unselected_course_width).toInt()
                zIndex = UNSELECTED_COURSE_Z_INDEX
            }

            map = this@NaverMapOverlayManager.map
            courseIdToOverlay[course.id] = this
        }

        addClickableCourseOverlay(latLngs, course)
    }

    private fun addClickableCourseOverlay(
        latLngs: List<LatLng>,
        course: CourseItem,
    ) {
        PathOverlay().apply {
            coords = latLngs
            color = context.getColor(R.color.transparent)
            globalZIndex = 0
            tag = course
            onClickListener = courseClickListener

            if (course.selected) {
                width = context.resources.getDimension(R.dimen.selected_course_width).toInt()
                zIndex = SELECTED_COURSE_Z_INDEX
            } else {
                width = context.resources.getDimension(R.dimen.unselected_course_width).toInt()
                zIndex = UNSELECTED_COURSE_Z_INDEX
            }

            map = this@NaverMapOverlayManager.map
            courseIdToClickableOverlay[course.id] = this
        }
    }

    private fun removeCourseOverlay(course: CourseItem) {
        courseIdToOverlay.remove(course.id)?.map = null
        courseIdToClickableOverlay.remove(course.id)?.map = null
    }

    fun drawRoute(route: List<Coordinate>) {
        if (route.size < 2) return

        PathOverlay().apply {
            coords = route.map(Coordinate::toLatLng)
            color = context.getColor(R.color.course_route)
            width = context.resources.getDimension(R.dimen.course_route_width).toInt()
            outlineWidth = 0

            map = this@NaverMapOverlayManager.map
            routeOverlay = this
        }
    }

    fun clearRoute() {
        routeOverlay?.map = null
        routeOverlay = null
    }

    fun drawSearchCoordinate(coordinate: Coordinate) {
        searchCoordinateMarker?.let { marker: Marker ->
            marker.position = coordinate.toLatLng()
        } ?: run {
            searchCoordinateMarker =
                Marker().apply {
                    position = coordinate.toLatLng()
                    icon = OverlayImage.fromBitmap(searchCoordinateImage)
                    anchor = PointF(0.5F, 0.5F)
                    map = this@NaverMapOverlayManager.map
                }
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

        fineUserLocationMarker?.apply {
            fineUserLocationAnimator?.cancel()
            fineUserLocationAnimator =
                CoordinateAnimator.animator(
                    start = position.toCoordinate(),
                    end = location.coordinate,
                ) { coordinate: Coordinate -> position = coordinate.toLatLng() }
            fineUserLocationAnimator?.start()
        } ?: run {
            fineUserLocationMarker =
                Marker().apply {
                    position = location.coordinate.toLatLng()
                    icon = OverlayImage.fromBitmap(fineUserLocationImage)
                    anchor = PointF(0.5F, 0.5F)
                    map = this@NaverMapOverlayManager.map
                }
        }
    }

    private fun drawCoarseUserLocation(location: Location.Coarse) {
        hideFineUserLocation()

        coarseUserLocationCircle?.apply {
            radius = location.accuracy.meter.value
            coarseUserLocationAnimator?.cancel()
            coarseUserLocationAnimator =
                CoordinateAnimator.animator(
                    start = center.toCoordinate(),
                    end = location.coordinate,
                ) { coordinate: Coordinate -> center = coordinate.toLatLng() }
            coarseUserLocationAnimator?.start()
        } ?: run {
            coarseUserLocationCircle =
                CircleOverlay().apply {
                    center = location.coordinate.toLatLng()
                    radius = location.accuracy.meter.value
                    color = context.getColor(R.color.coarse_location_area)
                    map = this@NaverMapOverlayManager.map
                }
        }
    }

    fun hideUserLocation() {
        hideFineUserLocation()
        hideCoarseUserLocation()
    }

    private fun hideFineUserLocation() {
        fineUserLocationMarker?.map = null
        fineUserLocationMarker = null

        fineUserLocationAnimator?.cancel()
        fineUserLocationAnimator = null
    }

    private fun hideCoarseUserLocation() {
        coarseUserLocationCircle?.map = null
        coarseUserLocationCircle = null

        coarseUserLocationAnimator?.cancel()
        coarseUserLocationAnimator = null
    }

    fun drawWaypoint(coordinate: Coordinate) {
        Marker().apply {
            position = coordinate.toLatLng()
            icon = OverlayImage.fromBitmap(waypointImage)
            anchor = PointF(0.5F, 0.5F)
            map = this@NaverMapOverlayManager.map
            waypoints.add(this)
        }
    }

    fun removeLastWaypoint() {
        waypoints.removeLastOrNull()?.apply { map = null }
        segments.removeLastOrNull()?.apply { map = null }
    }

    fun clearWaypoints() {
        waypoints.forEach { waypoint: Marker -> waypoint.map = null }
        waypoints.clear()
    }

    fun drawDraftSegment(segment: DraftSegment) {
        val coordinates: List<Coordinate> =
            when (segment.coordinates.size) {
                0 -> return
                1 -> segment.coordinates + segment.coordinates
                else -> segment.coordinates
            }

        PathOverlay().apply {
            coords = coordinates.map(Coordinate::toLatLng)
            color = context.getColor(R.color.course_draft)
            width = context.resources.getDimension(R.dimen.draft_segment_width).toInt()
            outlineWidth = 0
            map = this@NaverMapOverlayManager.map
            segments.add(this)
        }
    }

    fun clearDraftSegments() {
        segments.forEach { segment: PathOverlay -> segment.map = null }
        segments.clear()
    }

    fun setOnCourseClickListener(onClick: (CourseItem) -> Unit) {
        courseClickListener =
            Overlay.OnClickListener { overlay: Overlay ->
                if (overlay is PathOverlay) {
                    (overlay.tag as? CourseItem)?.let { course: CourseItem ->
                        Logger.log(
                            Logger.Event.Click("course_on_map"),
                            "id" to course.id,
                            "name" to course.name,
                        )
                        onClick(course)
                    }
                }
                false
            }

        courseIdToClickableOverlay.values.forEach { pathOverlay: PathOverlay ->
            if (pathOverlay.tag is CourseItem) {
                pathOverlay.onClickListener = courseClickListener
            }
        }
    }

    companion object {
        private const val UNSELECTED_COURSE_Z_INDEX = 0
        private const val SELECTED_COURSE_Z_INDEX = 1
    }
}
