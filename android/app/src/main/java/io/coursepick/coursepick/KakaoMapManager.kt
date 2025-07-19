package io.coursepick.coursepick

import android.Manifest
import androidx.annotation.RequiresPermission
import com.kakao.vectormap.KakaoMap
import com.kakao.vectormap.MapView
import io.coursepick.coursepick.domain.Course

class KakaoMapManager(
    mapView: MapView,
    locationProvider: LocationProvider = LocationProvider(mapView.context),
) {
    private val lifecycleHandler: KakaoMapLifecycleHandler = KakaoMapLifecycleHandler(mapView)
    private val cameraController: KakaoMapCameraController =
        KakaoMapCameraController(locationProvider)
    private val drawer: KakaoMapDrawer = KakaoMapDrawer(mapView.context)
    private var kakaoMap: KakaoMap? = null

    @RequiresPermission(anyOf = [Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION])
    fun start(course: Course?) {
        lifecycleHandler.start { map ->
            kakaoMap = map
            cameraController.moveToCurrentLocation(map)
            if (course != null) draw(course)
        }
    }

    fun resume() = lifecycleHandler.resume()

    fun pause() = lifecycleHandler.pause()

    fun draw(course: Course) {
        kakaoMap?.let { map: KakaoMap -> drawer.drawCourse(map, course) }
    }

    @RequiresPermission(anyOf = [Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION])
    fun moveToCurrentLocation() {
        kakaoMap?.let { map: KakaoMap -> cameraController.moveToCurrentLocation(map) }
    }
}
