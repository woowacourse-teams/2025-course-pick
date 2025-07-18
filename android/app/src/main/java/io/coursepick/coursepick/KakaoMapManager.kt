package io.coursepick.coursepick

import android.Manifest
import android.annotation.SuppressLint
import android.location.Location
import android.util.Log
import androidx.annotation.RequiresPermission
import com.kakao.vectormap.KakaoMap
import com.kakao.vectormap.KakaoMapReadyCallback
import com.kakao.vectormap.LatLng
import com.kakao.vectormap.MapLifeCycleCallback
import com.kakao.vectormap.MapView
import com.kakao.vectormap.camera.CameraUpdate
import com.kakao.vectormap.camera.CameraUpdateFactory
import com.kakao.vectormap.route.RouteLineLayer
import com.kakao.vectormap.route.RouteLineOptions
import com.kakao.vectormap.route.RouteLineSegment
import com.kakao.vectormap.route.RouteLineStyle
import com.kakao.vectormap.route.RouteLineStyles
import com.kakao.vectormap.route.RouteLineStylesSet
import io.coursepick.coursepick.domain.Course

class KakaoMapManager(
    private val mapView: MapView,
    private val locationProvider: LocationProvider = LocationProvider(mapView.context),
) {
    private var kakaoMap: KakaoMap? = null

    init {
        mapView.start(
            object : MapLifeCycleCallback() {
                override fun onMapDestroy() {}

                override fun onMapError(exception: Exception?) {}
            },
            object : KakaoMapReadyCallback() {
                @SuppressLint("MissingPermission")
                override fun onMapReady(kakaoMap: KakaoMap) {
                    this@KakaoMapManager.kakaoMap = kakaoMap
                    moveToCurrentLocation()
                }
            },
        )
    }

    fun resume() = mapView.resume()

    fun pause() = mapView.pause()

    fun draw(course: Course) {
        val kakaoMap: KakaoMap = kakaoMap ?: return
        val layer: RouteLineLayer = kakaoMap.routeLineManager?.layer ?: return

        val latLngs: List<LatLng> = course.toLatLngs()
        val stylesSet: RouteLineStylesSet =
            RouteLineStylesSet.from(
                "blueStyles",
                RouteLineStyles.from(RouteLineStyle.from(16f, 0xFF0000FF.toInt())),
            )
        val segment: RouteLineSegment =
            RouteLineSegment
                .from(latLngs)
                .setStyles(stylesSet.getStyles(0))
        val options: RouteLineOptions =
            RouteLineOptions
                .from(segment)
                .setStylesSet(stylesSet)

        layer.addRouteLine(options)
    }

    @RequiresPermission(anyOf = [Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION])
    private fun moveToCurrentLocation() {
        locationProvider.fetchCurrentLocation(
            onSuccess = { location: Location ->
                val latLng = LatLng.from(location.latitude, location.longitude)
                val cameraUpdate: CameraUpdate = CameraUpdateFactory.newCenterPosition(latLng)
                kakaoMap?.moveCamera(cameraUpdate)
            },
            onFailure = { exception: Exception ->
                Log.e("Location", "위치 조회 실패: ${exception.message}")
            },
        )
    }

    private fun Course.toLatLngs() =
        coordinates.map { coordinate ->
            LatLng.from(coordinate.latitude.value, coordinate.longitude.value)
        }
}
