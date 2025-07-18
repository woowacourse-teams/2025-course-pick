package io.coursepick.coursepick

import com.kakao.vectormap.KakaoMap
import com.kakao.vectormap.KakaoMapReadyCallback
import com.kakao.vectormap.MapLifeCycleCallback
import com.kakao.vectormap.MapView

class KakaoMapManager(
    private val mapView: MapView,
) {
    fun init() {
        mapView.start(
            object : MapLifeCycleCallback() {
                override fun onMapDestroy() {
                }

                override fun onMapError(exception: Exception?) {
                }
            },
            object : KakaoMapReadyCallback() {
                override fun onMapReady(kakaoMap: KakaoMap) {
                }
            },
        )
    }

    fun resume() {
        mapView.resume()
    }

    fun pause() {
        mapView.pause()
    }
}
