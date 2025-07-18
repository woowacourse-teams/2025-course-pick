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

class KakaoMapManager(
    private val mapView: MapView,
    private val locationProvider: LocationProvider = LocationProvider(mapView.context),
) {
    init {
        mapView.start(
            object : MapLifeCycleCallback() {
                override fun onMapDestroy() {}

                override fun onMapError(exception: Exception?) {}
            },
            object : KakaoMapReadyCallback() {
                @SuppressLint("MissingPermission")
                override fun onMapReady(kakaoMap: KakaoMap) {
                    moveToCurrentLocation(kakaoMap)
                }
            },
        )
    }

    fun resume() = mapView.resume()

    fun pause() = mapView.pause()

    @RequiresPermission(anyOf = [Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION])
    private fun moveToCurrentLocation(kakaoMap: KakaoMap) {
        locationProvider.fetchCurrentLocation(
            onSuccess = { location: Location ->
                val latLng = LatLng.from(location.latitude, location.longitude)
                val cameraUpdate: CameraUpdate = CameraUpdateFactory.newCenterPosition(latLng)
                kakaoMap.moveCamera(cameraUpdate)
            },
            onFailure = {
                Log.e("Location", "위치 조회 실패: ${it.message}")
            },
        )
    }
}
