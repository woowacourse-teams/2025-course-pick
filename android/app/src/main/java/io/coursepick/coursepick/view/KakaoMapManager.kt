package io.coursepick.coursepick.view

import android.Manifest
import androidx.annotation.RequiresPermission
import com.kakao.vectormap.KakaoMap
import com.kakao.vectormap.MapGravity
import com.kakao.vectormap.MapView
import io.coursepick.coursepick.R
import io.coursepick.coursepick.domain.Coordinate
import io.coursepick.coursepick.domain.Latitude
import io.coursepick.coursepick.domain.Longitude

class KakaoMapManager(
    private val mapView: MapView,
    locationProvider: LocationProvider = LocationProvider(mapView.context),
) {
    private val lifecycleHandler = KakaoMapLifecycleHandler(mapView)
    private val cameraController = KakaoMapCameraController(locationProvider)
    private val drawer = KakaoMapDrawer(mapView.context)
    private var kakaoMap: KakaoMap? = null

    @RequiresPermission(anyOf = [Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION])
    fun start(onMapReady: (KakaoMap) -> Unit) {
        lifecycleHandler.start { map: KakaoMap ->
            kakaoMap = map
            map.logo?.setPosition(
                MapGravity.TOP or MapGravity.LEFT,
                mapView.context.dpToPx(LOGO_POSITION_OFFSET_DP),
                mapView.context.dpToPx(LOGO_POSITION_OFFSET_DP),
            )
            map.setPadding(
                0,
                0,
                0,
                mapView.context.resources.getDimensionPixelSize(R.dimen.main_bottom_sheet_peek_height),
            )
            onMapReady(map)
        }
    }

    fun resume() = lifecycleHandler.resume()

    fun pause() = lifecycleHandler.pause()

    fun draw(course: CourseItem) {
        kakaoMap?.let { map: KakaoMap ->
            drawer.drawCourse(map, course)
        }
    }

    fun fitTo(course: CourseItem) {
        val latitudes: List<Latitude> =
            course.coordinates.map { coordinate: Coordinate -> coordinate.latitude }
        val longitudes: List<Longitude> =
            course.coordinates.map { coordinate: Coordinate -> coordinate.longitude }
        val northeast =
            Coordinate(latitudes.maxBy(Latitude::value), longitudes.maxBy(Longitude::value))
        val southwest =
            Coordinate(latitudes.minBy(Latitude::value), longitudes.minBy(Longitude::value))
        val padding = mapView.context.dpToPx(COURSE_PADDING_DP).toInt()
        kakaoMap?.let { map: KakaoMap ->
            cameraController.fitTo(map, northeast, southwest, padding)
        }
    }

    @RequiresPermission(anyOf = [Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION])
    fun moveTo(course: CourseItem) {
        val coordinate: Coordinate = course.coordinates.firstOrNull() ?: return
        kakaoMap?.let { map: KakaoMap ->
            cameraController.moveTo(
                map,
                coordinate,
            )
        }
    }

    @RequiresPermission(anyOf = [Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION])
    fun moveToCurrentLocation() {
        kakaoMap?.let { map: KakaoMap -> cameraController.moveToCurrentLocation(map) }
    }

    companion object {
        private const val LOGO_POSITION_OFFSET_DP = 10F
        private const val COURSE_PADDING_DP = 20F
    }
}
