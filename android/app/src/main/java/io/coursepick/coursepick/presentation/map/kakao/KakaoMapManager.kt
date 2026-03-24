package io.coursepick.coursepick.presentation.map.kakao

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import com.kakao.vectormap.KakaoMap
import com.kakao.vectormap.LatLng
import com.kakao.vectormap.MapGravity
import com.kakao.vectormap.MapView
import io.coursepick.coursepick.R
import io.coursepick.coursepick.domain.course.Coordinate
import io.coursepick.coursepick.domain.course.Scope
import io.coursepick.coursepick.domain.location.Location
import io.coursepick.coursepick.presentation.course.CourseItem
import io.coursepick.coursepick.presentation.map.MapManager
import timber.log.Timber

class KakaoMapManager(
    private val mapView: MapView,
    lifecycle: Lifecycle,
) : MapManager,
    DefaultLifecycleObserver {
    private var kakaoMap: KakaoMap? = null
    private var drawer: KakaoMapDrawer? = null
    private val cameraController = KakaoMapCameraController(mapView.context)
    private val eventHandler = KakaoMapEventHandler()

    private var courses: List<CourseItem> = emptyList()

    override val cameraCoordinate: Coordinate?
        get() = kakaoMap?.cameraPosition?.position?.toCoordinate()

    override val scope: Scope?
        get() {
            val screenTopLeft: LatLng = kakaoMap?.fromScreenPoint(0, 0) ?: return null
            val mapCenter: Coordinate = cameraCoordinate ?: return null
            val distance: Int =
                DistanceCalculator.distance(mapCenter, screenTopLeft.toCoordinate())
                    ?: return null
            return Scope(distance)
        }

    init {
        lifecycle.addObserver(this)
    }

    override fun startMap(onMapReady: () -> Unit) {
        KakaoMapLifecycleHandler(mapView).start { map: KakaoMap ->
            kakaoMap = map
            drawer = KakaoMapDrawer(mapView.context, map)

            val offsetPx: Float =
                mapView.context.resources.getDimension(R.dimen.map_logo_position_offset)
            map.logo?.setPosition(
                MapGravity.BOTTOM or MapGravity.LEFT,
                offsetPx,
                offsetPx,
            )

            onMapReady()
        }
    }

    override fun draw(course: CourseItem) {
        courses = listOf(course)
        drawer?.drawCourse(course) ?: Timber.w("KakaoMapDrawer is null")
    }

    override fun draw(courses: List<CourseItem>) {
        this.courses = courses
        drawer?.drawCourses(courses) ?: Timber.w("KakaoMapDrawer is null")
    }

    override fun drawRouteToCourse(
        route: List<Coordinate>,
        course: CourseItem,
    ) {
        courses = listOf(course)
        drawer?.drawRouteToCourse(route, course) ?: Timber.w("KakaoMapDrawer is null")
    }

    override fun removeAllRouteLines() {
        drawer?.removeAllLines() ?: Timber.w("KakaoMapDrawer is null")
    }

    override fun drawSearchCoordinate(coordinate: Coordinate) {
        drawer?.drawSearchPosition(coordinate) ?: Timber.w("KakaoMapDrawer is null")
    }

    override fun drawUserLocation(location: Location) {
        drawer?.drawUserPosition(location) ?: Timber.w("KakaoMapDrawer is null")
    }

    override fun hideUserLocation() {
        drawer?.hideUserPosition() ?: Timber.w("KakaoMapDrawer is null")
    }

    override fun fitTo(coordinates: List<Coordinate>) {
        kakaoMap?.let { kakaoMap: KakaoMap ->
            cameraController.fitTo(coordinates, kakaoMap)
        } ?: Timber.w("kakaoMap is null")
    }

    override fun fitTo(course: CourseItem) {
        kakaoMap?.let { kakaoMap: KakaoMap ->
            cameraController.fitTo(course, kakaoMap)
        } ?: Timber.w("kakaoMap is null")
    }

    override fun setOnCourseClickListener(onClick: (CourseItem) -> Unit) {
        kakaoMap?.let { kakaoMap: KakaoMap ->
            eventHandler.setOnCourseClickListener(kakaoMap, courses) { course: CourseItem ->
                onClick(course)
            }
        } ?: Timber.w("kakaoMap is null")
    }

    override fun setOnCameraMoveListener(onCameraMove: () -> Unit) {
        kakaoMap?.let { kakaoMap: KakaoMap ->
            eventHandler.setOnCameraMoveListener(kakaoMap) {
                onCameraMove()
            }
        } ?: Timber.w("kakaoMap is null")
    }

    override fun moveTo(coordinate: Coordinate) {
        kakaoMap?.let { kakaoMap: KakaoMap ->
            cameraController.moveTo(kakaoMap, coordinate)
        } ?: Timber.w("kakaoMap is null")
    }

    override fun resetZoom() {
        kakaoMap?.let { kakaoMap: KakaoMap ->
            cameraController.resetZoomLevel(kakaoMap)
        } ?: Timber.w("kakaoMap is null")
    }

    override fun setPadding(
        left: Int,
        top: Int,
        right: Int,
        bottom: Int,
    ) {
        kakaoMap?.let { kakaoMap: KakaoMap ->
            kakaoMap.setPadding(left, top, right, bottom)
        } ?: Timber.w("kakaoMap is null")
    }

    override fun onResume(owner: LifecycleOwner) {
        super.onResume(owner)
        mapView.resume()
    }

    override fun onPause(owner: LifecycleOwner) {
        super.onPause(owner)
        mapView.pause()
    }

    override fun onDestroy(owner: LifecycleOwner) {
        super.onDestroy(owner)
        mapView.finish()
    }
}
