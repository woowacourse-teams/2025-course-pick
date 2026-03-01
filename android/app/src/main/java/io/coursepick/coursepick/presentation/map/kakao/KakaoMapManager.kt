package io.coursepick.coursepick.presentation.map.kakao

import android.Manifest
import androidx.annotation.RequiresPermission
import com.kakao.vectormap.KakaoMap
import com.kakao.vectormap.LatLng
import com.kakao.vectormap.MapGravity
import com.kakao.vectormap.MapView
import io.coursepick.coursepick.R
import io.coursepick.coursepick.domain.course.Coordinate
import io.coursepick.coursepick.domain.course.Scope
import io.coursepick.coursepick.domain.location.Location
import io.coursepick.coursepick.presentation.course.CourseItem
import timber.log.Timber

class KakaoMapManager(
    private val mapView: MapView,
) {
    private val lifecycleHandler = KakaoMapLifecycleHandler(mapView)
    private val cameraController = KakaoMapCameraController(mapView.context)
    private val eventHandler = KakaoMapEventHandler()

    private var kakaoMap: KakaoMap? = null
    private var drawer: KakaoMapDrawer? = null

    val cameraPosition get(): LatLng? = kakaoMap?.cameraPosition?.position

    @RequiresPermission(anyOf = [Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION])
    fun start(onMapReady: () -> Unit) {
        val offsetPx: Float =
            mapView.context.resources.getDimension(R.dimen.map_logo_position_offset)
        lifecycleHandler.start { map: KakaoMap ->
            kakaoMap = map
            drawer = KakaoMapDrawer(mapView.context, map)
            map.logo?.setPosition(
                MapGravity.BOTTOM or MapGravity.LEFT,
                offsetPx,
                offsetPx,
            )
            onMapReady()
        }
    }

    fun resume() = lifecycleHandler.resume()

    fun pause() = lifecycleHandler.pause()

    fun finish() = lifecycleHandler.finish()

    fun draw(course: CourseItem) {
        drawer?.drawCourse(course) ?: Timber.w("KakaoMapDrawer is null")
    }

    fun draw(courses: List<CourseItem>) {
        drawer?.drawCourses(courses) ?: Timber.w("KakaoMapDrawer is null")
    }

    fun drawRouteToCourse(
        route: List<Coordinate>,
        course: CourseItem,
    ) {
        drawer?.drawRouteToCourse(route, course) ?: Timber.w("KakaoMapDrawer is null")
    }

    fun removeAllLines() {
        drawer?.removeAllLines() ?: Timber.w("KakaoMapDrawer is null")
    }

    fun drawSearchPosition(coordinate: Coordinate) {
        drawer?.drawSearchPosition(coordinate) ?: Timber.w("KakaoMapDrawer is null")
    }

    fun drawUserPosition(location: Location) {
        drawer?.drawUserPosition(location) ?: Timber.w("KakaoMapDrawer is null")
    }

    fun hideUserPosition() {
        drawer?.hideUserPosition() ?: Timber.w("KakaoMapDrawer is null")
    }

    fun fitTo(coordinates: List<Coordinate>) {
        kakaoMap?.let { kakaoMap: KakaoMap ->
            cameraController.fitTo(coordinates, kakaoMap)
        } ?: Timber.w("kakaoMap is null")
    }

    fun fitTo(course: CourseItem) {
        kakaoMap?.let { kakaoMap: KakaoMap ->
            cameraController.fitTo(course, kakaoMap)
        } ?: Timber.w("kakaoMap is null")
    }

    fun setOnCourseClickListener(
        courses: List<CourseItem>,
        onClick: (CourseItem) -> Unit,
    ) {
        kakaoMap?.let { kakaoMap: KakaoMap ->
            eventHandler.setOnCourseClickListener(kakaoMap, courses) { course: CourseItem ->
                onClick(course)
            }
        } ?: Timber.w("kakaoMap is null")
    }

    fun setOnCameraMoveListener(onCameraMove: () -> Unit) {
        kakaoMap?.let { kakaoMap: KakaoMap ->
            eventHandler.setOnCameraMoveListener(kakaoMap) {
                onCameraMove()
            }
        } ?: Timber.w("kakaoMap is null")
    }

    fun moveTo(coordinate: Coordinate) {
        kakaoMap?.let { kakaoMap: KakaoMap ->
            cameraController.moveTo(kakaoMap, coordinate)
        } ?: Timber.w("kakaoMap is null")
    }

    fun resetZoomLevel() {
        kakaoMap?.let { kakaoMap: KakaoMap ->
            cameraController.resetZoomLevel(kakaoMap)
        } ?: Timber.w("kakaoMap is null")
    }

    fun setBottomPadding(size: Int) {
        kakaoMap?.let { kakaoMap: KakaoMap ->
            kakaoMap.setPadding(0, 0, 0, size)
        } ?: Timber.w("kakaoMap is null")
    }

    fun scopeOrNull(screenCenter: Coordinate): Scope? {
        val screenDiagonalTop: LatLng = kakaoMap?.fromScreenPoint(0, 0) ?: return null
        val distance: Int =
            DistanceCalculator.distance(screenCenter, screenDiagonalTop.toCoordinate())
                ?: return null
        return Scope(distance)
    }
}
