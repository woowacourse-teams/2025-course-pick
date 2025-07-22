package io.coursepick.coursepick.view

import android.location.Location
import com.kakao.vectormap.KakaoMap
import com.kakao.vectormap.LatLng
import com.kakao.vectormap.camera.CameraAnimation
import com.kakao.vectormap.camera.CameraUpdate
import com.kakao.vectormap.camera.CameraUpdateFactory
import io.coursepick.coursepick.domain.Coordinate

class KakaoMapCameraController {
    fun moveTo(
        map: KakaoMap,
        coordinate: Coordinate,
    ) {
        moveTo(map, coordinate.latitude.value, coordinate.longitude.value)
    }

    fun moveTo(
        map: KakaoMap,
        location: Location,
    ) {
        moveTo(map, location.latitude, location.longitude)
    }

    fun fitTo(
        map: KakaoMap,
        coordinates: List<Coordinate>,
        padding: Int,
    ) {
        val latLngs: Array<LatLng> =
            coordinates
                .map { coordinate: Coordinate ->
                    LatLng.from(coordinate.latitude.value, coordinate.longitude.value)
                }.toTypedArray()
        val cameraUpdate: CameraUpdate = CameraUpdateFactory.fitMapPoints(latLngs, padding)
        val cameraAnimation = CameraAnimation.from(MOVE_ANIMATION_DURATION, true, false)
        map.moveCamera(cameraUpdate, cameraAnimation)
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

    companion object {
        private const val MOVE_ANIMATION_DURATION = 750
    }
}
