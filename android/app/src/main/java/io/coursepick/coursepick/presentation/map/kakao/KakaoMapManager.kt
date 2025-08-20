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

class KakaoMapManager(
    private val mapView: MapView,
    private val locationProvider: LocationProvider = LocationProvider(mapView.context),
) {
    private val lifecycleHandler = KakaoMapLifecycleHandler(mapView)
    private val cameraController = KakaoMapCameraController(mapView.context)
    private val drawer = KakaoMapDrawer(mapView.context)
    private val eventHandler = KakaoMapEventHandler()

    @Suppress("ktlint:standard:backing-property-naming")
    private var _kakaoMap: KakaoMap? = null
    private val kakaoMap: KakaoMap get() = _kakaoMap!!

    val cameraPosition get(): LatLng? = kakaoMap.cameraPosition?.position

    @RequiresPermission(anyOf = [Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION])
    fun start(onMapReady: () -> Unit) {
        val offsetPx: Float =
            mapView.context.resources.getDimension(R.dimen.map_logo_position_offset)
        lifecycleHandler.start { map: KakaoMap ->
            _kakaoMap = map
            map.logo?.setPosition(
                MapGravity.BOTTOM or MapGravity.LEFT,
                offsetPx,
                offsetPx,
            )
            showCurrentLocation()
            onMapReady()
        }
    }

    fun resume() = lifecycleHandler.resume()

    fun pause() = lifecycleHandler.pause()

    fun draw(courses: List<CourseItem>) {
        drawer.drawCourses(kakaoMap, courses)
    }

    fun showSearchPosition(coordinate: Coordinate) {
        drawer.showSearchPosition(kakaoMap, coordinate)
    }

    @RequiresPermission(anyOf = [Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION])
    fun showCurrentLocation() {
        locationProvider.fetchCurrentLocation(
            onSuccess = { location: Location ->
                drawer.showUserPosition(kakaoMap, location)
                cameraController.moveTo(kakaoMap, location)
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

    fun fitTo(course: CourseItem) {
        cameraController.fitTo(course, kakaoMap)
    }

    fun setOnCourseClickListener(
        courses: List<CourseItem>,
        onClick: (CourseItem) -> Unit,
    ) {
        eventHandler.setOnCourseClickListener(kakaoMap, courses) { course: CourseItem ->
            onClick(course)
        }
    }

    fun setOnCameraMoveListener(onCameraMove: () -> Unit) {
        eventHandler.setOnCameraMoveListener(kakaoMap) {
            onCameraMove()
        }
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
        cameraController.moveTo(kakaoMap, location)
    }

    fun resetZoomLevel() {
        cameraController.resetZoomLevel(kakaoMap)
    }

    fun setBottomPadding(size: Int) {
        kakaoMap.setPadding(0, 0, 0, size)
    }

    @RequiresPermission(anyOf = [Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION])
    fun startTrackingCurrentLocation() {
        locationProvider.startLocationUpdates(
            onUpdate = { location ->
                drawer.showUserPosition(kakaoMap, location)
            },
            onError = {
                drawer.removeAllLabels(kakaoMap)
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
            kakaoMap.fromScreenPoint(0, 0)
                ?: throw IllegalStateException("화면 좌표 계산 실패")
        val distance =
            DistanceCalculator.distance(screenCenter, screenDiagonalTop.toCoordinate())
                ?: throw IllegalStateException("거리 계산 실패")
        return Scope(distance)
    }
}
