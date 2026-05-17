package io.coursepick.coursepick.presentation.map.kakao

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import com.kakao.vectormap.KakaoMap
import com.kakao.vectormap.KakaoMapReadyCallback
import com.kakao.vectormap.MapLifeCycleCallback
import com.kakao.vectormap.MapView

class KakaoMapLifecycleHandler(
    private val mapView: MapView,
    lifecycle: Lifecycle,
) : DefaultLifecycleObserver {
    init {
        lifecycle.addObserver(this)
    }

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
            },
        )
    }

    override fun onResume(owner: LifecycleOwner) {
        super.onResume(owner)
        mapView.resume()
    }

    override fun onPause(owner: LifecycleOwner) {
        super.onPause(owner)
        mapView.pause()
    }

    override fun onDestroy(owner: LifecycleOwner) {
        super.onDestroy(owner)
        mapView.finish()
    }
}
