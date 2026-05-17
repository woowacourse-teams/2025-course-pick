package io.coursepick.coursepick.presentation.map.naver

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import com.naver.maps.map.MapFragment
import io.coursepick.coursepick.R
import io.coursepick.coursepick.presentation.map.MapManager
import io.coursepick.coursepick.presentation.map.MapManagerFactory
import javax.inject.Inject

class NaverMapManagerFactory
    @Inject
    constructor() : MapManagerFactory {
        override fun create(container: ViewGroup): MapManager {
            val view: View =
                LayoutInflater
                    .from(container.context)
                    .inflate(R.layout.layout_naver_map, container, false)
            container.addView(view)

            val activity = container.context as FragmentActivity
            val mapFragment = activity.supportFragmentManager.findFragmentById(R.id.naver_map) as MapFragment

            return NaverMapManager(mapFragment)
        }
    }
