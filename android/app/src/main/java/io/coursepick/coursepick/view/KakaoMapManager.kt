package io.coursepick.coursepick.view

import android.Manifest
import androidx.annotation.RequiresPermission
import com.kakao.vectormap.KakaoMap
import com.kakao.vectormap.MapGravity
import com.kakao.vectormap.MapView
import io.coursepick.coursepick.domain.Coordinate

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
                mapView.context.dpToPx(10F),
                mapView.context.dpToPx(10F),
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
}
