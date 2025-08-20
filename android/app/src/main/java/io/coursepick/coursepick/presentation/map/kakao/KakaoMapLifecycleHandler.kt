package io.coursepick.coursepick.presentation.map.kakao

import com.kakao.vectormap.KakaoMap
import com.kakao.vectormap.KakaoMapReadyCallback
import com.kakao.vectormap.LatLng
import com.kakao.vectormap.MapLifeCycleCallback
import com.kakao.vectormap.MapView

class KakaoMapLifecycleHandler(
    private val mapView: MapView,
) {
    fun start(onMapReady: (KakaoMap) -> Unit) {
        mapView.start(
            object : MapLifeCycleCallback() {
                override fun onMapDestroy() {}

                override fun onMapError(exception: Exception?) {}
            },
            object : KakaoMapReadyCallback() {
                override fun onMapReady(map: KakaoMap) {
                    onMapReady(map)
                }

                override fun getPosition(): LatLng = LatLng.from(37.5100226, 127.1026170)
            },
        )
    }

    fun resume() = mapView.resume()

    fun pause() = mapView.pause()
}
