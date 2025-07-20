package io.coursepick.coursepick

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
        val latLng: LatLng = coordinate.toLatLng()
        val cameraUpdate: CameraUpdate = CameraUpdateFactory.newCenterPosition(latLng)
        map.moveCamera(cameraUpdate)
    }

    @RequiresPermission(anyOf = [Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION])
    fun moveToCurrentLocation(map: KakaoMap) {
        locationProvider.fetchCurrentLocation(
            onSuccess = { location: Location ->
                moveTo(map, location)
            },
            onFailure = { exception: Exception ->
                Log.e("Location", "위치 조회 실패: ${exception.message}")
            },
        )
    }

    private fun Coordinate.toLatLng() = LatLng.from(latitude.value, longitude.value)

    private fun moveTo(
        map: KakaoMap,
        location: Location,
    ) {
        val latLng: LatLng = LatLng.from(location.latitude, location.longitude)
        val cameraUpdate: CameraUpdate = CameraUpdateFactory.newCenterPosition(latLng)
        map.moveCamera(cameraUpdate)
    }
}
