package io.coursepick.coursepick.presentation.map.kakao

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import com.kakao.vectormap.MapView
import io.coursepick.coursepick.R
import io.coursepick.coursepick.presentation.map.MapManager
import io.coursepick.coursepick.presentation.map.MapManagerFactory
import javax.inject.Inject

class KakaoMapManagerFactory
    @Inject
    constructor() : MapManagerFactory {
        override fun create(container: ViewGroup): MapManager {
            val view: View =
                LayoutInflater
                    .from(container.context)
                    .inflate(R.layout.layout_kakao_map, container, false)

            val mapView =
                view as? MapView
                    ?: throw IllegalStateException("Inflate된 레이아웃이 com.kakao.vectormap.MapView가 아닙니다.")
            container.addView(mapView)

            val lifecycle: Lifecycle =
                (container.context as? LifecycleOwner)?.lifecycle
                    ?: throw IllegalStateException("KakaoMapManager 생성에는 LifecycleOwner가 필요합니다.")

            return KakaoMapManager(mapView, lifecycle)
        }
    }
