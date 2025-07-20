package io.coursepick.coursepick

import android.Manifest
import android.location.Location
import android.util.Log
import androidx.annotation.RequiresPermission
import com.kakao.vectormap.KakaoMap
import com.kakao.vectormap.LatLng
import com.kakao.vectormap.camera.CameraUpdate
import com.kakao.vectormap.camera.CameraUpdateFactory

class KakaoMapCameraController(
    private val locationProvider: LocationProvider,
) {
    @RequiresPermission(anyOf = [Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION])
    fun moveToCurrentLocation(kakaoMap: KakaoMap) {
        locationProvider.fetchCurrentLocation(
            onSuccess = { location: Location ->
                kakaoMap.moveTo(location)
            },
            onFailure = { exception: Exception ->
                Log.e("Location", "위치 조회 실패: ${exception.message}")
            },
        )
    }

    private fun KakaoMap.moveTo(location: Location) {
        val latLng = LatLng.from(location.latitude, location.longitude)
        val cameraUpdate: CameraUpdate = CameraUpdateFactory.newCenterPosition(latLng)
        moveCamera(cameraUpdate)
    }
}
