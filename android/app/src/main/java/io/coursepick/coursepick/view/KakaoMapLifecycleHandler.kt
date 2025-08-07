package io.coursepick.coursepick.view

import com.kakao.vectormap.KakaoMap
import com.kakao.vectormap.KakaoMapReadyCallback
import com.kakao.vectormap.MapLifeCycleCallback
import com.kakao.vectormap.MapView

class KakaoMapLifecycleHandler(
    private val mapView: MapView,
) {
    private var kakaoMap: KakaoMap? = null

    fun start(onMapReady: (KakaoMap) -> Unit) {
        mapView.start(
            object : MapLifeCycleCallback() {
                override fun onMapDestroy() {}

                override fun onMapError(exception: Exception?) {}
            },
            object : KakaoMapReadyCallback() {
                override fun onMapReady(map: KakaoMap) {
                    kakaoMap = map
                    onMapReady(map)
                }
            },
        )
    }

    fun resume() = mapView.resume()

    fun pause() = mapView.pause()
}
