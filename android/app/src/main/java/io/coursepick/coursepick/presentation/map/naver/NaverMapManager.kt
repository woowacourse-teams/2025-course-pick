package io.coursepick.coursepick.presentation.map.naver

import com.naver.maps.map.MapFragment
import com.naver.maps.map.NaverMap
import io.coursepick.coursepick.domain.course.Coordinate
import io.coursepick.coursepick.domain.course.Scope
import io.coursepick.coursepick.domain.location.Location
import io.coursepick.coursepick.presentation.course.CourseItem
import io.coursepick.coursepick.presentation.map.MapManager
import timber.log.Timber

class NaverMapManager(
    private val mapFragment: MapFragment,
) : MapManager {
    private var map: NaverMap? = null
    private var drawer: NaverMapDrawer? = null

    override val cameraCoordinate: Coordinate
        get() = TODO("Not yet implemented")
    override val scope: Scope
        get() = TODO("Not yet implemented")

    override fun startMap(onMapReady: () -> Unit) {
        mapFragment.getMapAsync { naverMap: NaverMap ->
            map = naverMap
            drawer = NaverMapDrawer(mapFragment.requireContext(), naverMap)

            onMapReady()
        }
    }

    override fun draw(course: CourseItem) {
        drawer?.drawCourse(course) ?: run { Timber.w(DRAWER_IS_NULL_MESSAGE) }
    }

    override fun draw(courses: List<CourseItem>) {
        courses.forEach(::draw)
    }

    override fun drawRouteToCourse(
        route: List<Coordinate>,
        course: CourseItem,
    ) {
        drawer?.drawRouteToCourse(route, course) ?: run { Timber.w(DRAWER_IS_NULL_MESSAGE) }
    }

    override fun removeAllRouteLines() {
        drawer?.removeAllRouteLines() ?: run { Timber.w(DRAWER_IS_NULL_MESSAGE) }
    }

    override fun drawSearchCoordinate(coordinate: Coordinate) {
        drawer?.drawSearchCoordinate(coordinate) ?: run { Timber.w(DRAWER_IS_NULL_MESSAGE) }
    }

    override fun drawUserLocation(location: Location) {
        drawer?.drawUserLocation(location) ?: run { Timber.w(DRAWER_IS_NULL_MESSAGE) }
    }

    override fun hideUserLocation() {
        drawer?.hideUserLocation() ?: run { Timber.w(DRAWER_IS_NULL_MESSAGE) }
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

    companion object {
        private val MAP_IS_NULL_MESSAGE = "${NaverMap::class.simpleName} is null."
        private val DRAWER_IS_NULL_MESSAGE = "${NaverMapDrawer::class.simpleName} is null."
    }
}
