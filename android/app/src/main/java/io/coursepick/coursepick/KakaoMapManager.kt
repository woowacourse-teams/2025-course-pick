package io.coursepick.coursepick

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.util.Log
import androidx.annotation.RequiresPermission
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.kakao.vectormap.KakaoMap
import com.kakao.vectormap.KakaoMapReadyCallback
import com.kakao.vectormap.LatLng
import com.kakao.vectormap.MapLifeCycleCallback
import com.kakao.vectormap.MapView
import com.kakao.vectormap.camera.CameraUpdate
import com.kakao.vectormap.camera.CameraUpdateFactory

class KakaoMapManager(
    private val mapView: MapView,
) {
    private val context: Context = mapView.context
    private val fusedLocationClient =
        LocationServices.getFusedLocationProviderClient(context)

    init {
        mapView.start(
            object : MapLifeCycleCallback() {
                override fun onMapDestroy() {}

                override fun onMapError(exception: Exception?) {}
            },
            object : KakaoMapReadyCallback() {
                @SuppressLint("MissingPermission")
                override fun onMapReady(kakaoMap: KakaoMap) {
                    fetchLocation(kakaoMap)
                }
            },
        )
    }

    @RequiresPermission(anyOf = [Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION])
    fun fetchLocation(kakaoMap: KakaoMap) {
        if (!hasLocationPermission) return

        fusedLocationClient
            .getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null)
            .addOnSuccessListener { location: Location ->
                val target: LatLng = LatLng.from(location.latitude, location.longitude)
                val cameraUpdate: CameraUpdate = CameraUpdateFactory.newCenterPosition(target)
                kakaoMap.moveCamera(cameraUpdate)
            }.addOnFailureListener { exception: Exception ->
                Log.e("Location", "위치 조회 실패: ${exception.message}")
            }
    }

    fun resume() {
        mapView.resume()
    }

    fun pause() {
        mapView.pause()
    }

    private val hasLocationPermission: Boolean =
        hasFineLocationPermission || hasCoarseLocationPermission

    private val hasCoarseLocationPermission: Boolean
        get() =
            ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_COARSE_LOCATION,
            ) == PackageManager.PERMISSION_GRANTED

    private val hasFineLocationPermission: Boolean
        get() =
            ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION,
            ) == PackageManager.PERMISSION_GRANTED
}
