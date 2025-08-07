package io.coursepick.coursepick.view

import android.Manifest
import android.location.Location
import android.util.Log
import androidx.annotation.RequiresPermission
import com.kakao.vectormap.KakaoMap
import com.kakao.vectormap.LatLng
import com.kakao.vectormap.camera.CameraUpdate
import com.kakao.vectormap.camera.CameraUpdateFactory
import io.coursepick.coursepick.domain.Coordinate

class KakaoMapCameraController(
    private val locationProvider: LocationProvider,
) {
    fun moveTo(
        map: KakaoMap,
        coordinate: Coordinate,
    ) {
        moveTo(map, coordinate.latitude.value, coordinate.longitude.value)
    }

    @RequiresPermission(anyOf = [Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION])
    fun moveToCurrentLocation(map: KakaoMap) {
        locationProvider.fetchCurrentLocation(
            onSuccess = { location: Location ->
                moveTo(map, location.latitude, location.longitude)
            },
            onFailure = { exception: Exception ->
                Log.e("Location", "위치 조회 실패: ${exception.message}")
            },
        )
    }

    private fun moveTo(
        map: KakaoMap,
        latitude: Double,
        longitude: Double,
    ) {
        val latLng = LatLng.from(latitude, longitude)
        val cameraUpdate: CameraUpdate = CameraUpdateFactory.newCenterPosition(latLng)
        map.moveCamera(cameraUpdate)
    }
}
