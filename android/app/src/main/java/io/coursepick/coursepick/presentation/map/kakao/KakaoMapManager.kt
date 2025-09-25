package io.coursepick.coursepick.presentation.map.kakao

import android.Manifest
import android.location.Location
import android.widget.Toast
import androidx.annotation.RequiresPermission
import com.kakao.vectormap.KakaoMap
import com.kakao.vectormap.LatLng
import com.kakao.vectormap.MapGravity
import com.kakao.vectormap.MapView
import io.coursepick.coursepick.R
import io.coursepick.coursepick.domain.course.Coordinate
import io.coursepick.coursepick.domain.course.Latitude
import io.coursepick.coursepick.domain.course.Longitude
import io.coursepick.coursepick.domain.course.Scope
import io.coursepick.coursepick.presentation.LocationProvider
import io.coursepick.coursepick.presentation.course.CourseItem
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import timber.log.Timber
import kotlin.time.Duration.Companion.milliseconds

class KakaoMapManager(
    private val mapView: MapView,
    private val locationProvider: LocationProvider = LocationProvider(mapView.context),
) {
    private val lifecycleHandler = KakaoMapLifecycleHandler(mapView)
    private val cameraController = KakaoMapCameraController(mapView.context)
    private val drawer = KakaoMapDrawer(mapView.context)
    private val eventHandler = KakaoMapEventHandler()

    @Suppress("ktlint:standard:backing-property-naming")
    private var kakaoMap: KakaoMap? = null

    val cameraPosition get(): LatLng? = kakaoMap?.cameraPosition?.position

    @RequiresPermission(anyOf = [Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION])
    fun start(onMapReady: () -> Unit) {
        val offsetPx: Float =
            mapView.context.resources.getDimension(R.dimen.map_logo_position_offset)
        lifecycleHandler.start { map: KakaoMap ->
            kakaoMap = map
            map.logo?.setPosition(
                MapGravity.BOTTOM or MapGravity.LEFT,
                offsetPx,
                offsetPx,
            )
            onMapReady()
            CoroutineScope(Dispatchers.Main).launch {
                delay(500.milliseconds)
                showCurrentLocation()
            }
        }
    }

    fun resume() = lifecycleHandler.resume()

    fun pause() = lifecycleHandler.pause()

    fun draw(course: CourseItem) {
        kakaoMap?.let { kakaoMap: KakaoMap ->
            drawer.drawCourse(kakaoMap, course)
        } ?: Timber.w("kakaoMap is null")
    }

    fun draw(courses: List<CourseItem>) {
        kakaoMap?.let { kakaoMap: KakaoMap ->
            drawer.drawCourses(kakaoMap, courses)
        } ?: Timber.w("kakaoMap is null")
    }

    fun drawRouteToCourse(
        route: List<Coordinate>,
        course: CourseItem,
    ) {
        kakaoMap?.let { kakaoMap: KakaoMap ->
            drawer.drawRouteToCourse(kakaoMap, route, course)
        } ?: Timber.w("kakaoMap is null")
    }

    fun removeAllLines() {
        kakaoMap?.let { kakaoMap: KakaoMap ->
            drawer.removeAllLines(kakaoMap)
        } ?: Timber.w("kakaoMap is null")
    }

    fun showSearchPosition(coordinate: Coordinate) {
        kakaoMap?.let { kakaoMap: KakaoMap ->
            drawer.showSearchPosition(kakaoMap, coordinate)
        } ?: Timber.w("kakaoMap is null")
    }

    @RequiresPermission(anyOf = [Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION])
    fun showCurrentLocation(afterSuccess: () -> Unit = {}) {
        locationProvider.fetchCurrentLocation(
            onSuccess = { location: Location ->
                kakaoMap?.let { kakaoMap: KakaoMap ->
                    drawer.showUserPosition(kakaoMap, location)
                    cameraController.moveTo(kakaoMap, location)
                } ?: Timber.w("kakaoMap is null")
                afterSuccess()
            },
            onFailure = {
                Toast
                    .makeText(
                        mapView.context,
                        "현재 위치를 불러오지 못했습니다.",
                        Toast.LENGTH_SHORT,
                    ).show()
            },
        )
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

    fun moveTo(
        latitude: Latitude,
        longitude: Longitude,
    ) {
        val location =
            Location("search").apply {
                this.latitude = latitude.value
                this.longitude = longitude.value
            }
        kakaoMap?.let { kakaoMap: KakaoMap ->
            cameraController.moveTo(kakaoMap, location)
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

    @RequiresPermission(anyOf = [Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION])
    fun startTrackingCurrentLocation() {
        locationProvider.startLocationUpdates(
            onUpdate = { location ->
                kakaoMap?.let { kakaoMap: KakaoMap ->
                    drawer.showUserPosition(kakaoMap, location)
                } ?: Timber.w("kakaoMap is null")
            },
            onError = {
                kakaoMap?.let { kakaoMap: KakaoMap ->
                    drawer.removeAllLabels(kakaoMap)
                } ?: Timber.w("kakaoMap is null")
            },
        )
    }

    fun stopTrackingCurrentLocation() {
        locationProvider.stopLocationUpdates()
    }

    @RequiresPermission(anyOf = [Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION])
    fun fetchCurrentLocation(
        onSuccess: (Latitude, Longitude) -> Unit,
        onFailure: (Exception) -> Unit,
    ) {
        locationProvider.fetchCurrentLocation(
            onSuccess = { location: Location ->
                onSuccess(Latitude(location.latitude), Longitude(location.longitude))
            },
            onFailure = onFailure,
        )
    }

    fun scope(screenCenter: Coordinate): Scope {
        val screenDiagonalTop =
            kakaoMap?.fromScreenPoint(0, 0)
                ?: throw IllegalStateException("화면 좌표 계산 실패")
        val distance =
            DistanceCalculator.distance(screenCenter, screenDiagonalTop.toCoordinate())
                ?: throw IllegalStateException("거리 계산 실패")
        return Scope(distance)
    }
}
