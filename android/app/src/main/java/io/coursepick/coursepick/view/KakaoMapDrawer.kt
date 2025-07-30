package io.coursepick.coursepick.view

import android.content.Context
import android.location.Location
import androidx.annotation.DrawableRes
import com.kakao.vectormap.KakaoMap
import com.kakao.vectormap.LatLng
import com.kakao.vectormap.label.LabelLayer
import com.kakao.vectormap.label.LabelManager
import com.kakao.vectormap.label.LabelOptions
import com.kakao.vectormap.label.LabelStyle
import com.kakao.vectormap.label.LabelStyles
import com.kakao.vectormap.route.RouteLineLayer
import com.kakao.vectormap.route.RouteLineOptions

class KakaoMapDrawer(
    context: Context,
) {
    private val routeLineOptionsFactory = RouteLineOptionsFactory(context)

    fun drawCourse(
        kakaoMap: KakaoMap,
        course: CourseItem,
    ) {
        val layer: RouteLineLayer = kakaoMap.routeLineManager?.layer ?: return
        layer.removeAll()
        val options: RouteLineOptions = routeLineOptionsFactory.routeLineOptions(course)
        layer.addRouteLine(options)
    }

    fun drawLabel(
        map: KakaoMap,
        @DrawableRes
        iconResourceId: Int,
        location: Location,
    ) {
        drawLabel(
            map,
            iconResourceId,
            location.latitude,
            location.longitude,
        )
    }

    fun removeAllLabels(map: KakaoMap) {
        val layer: LabelLayer = map.labelManager?.layer ?: return
        layer.removeAll()
    }

    private fun drawLabel(
        map: KakaoMap,
        @DrawableRes
        iconResourceId: Int,
        latitude: Double,
        longitude: Double,
    ) {
        val labelManager: LabelManager = map.labelManager ?: return
        val styles: LabelStyles =
            labelManager.addLabelStyles(LabelStyles.from(LabelStyle.from(iconResourceId)))
                ?: return
        val options: LabelOptions =
            LabelOptions
                .from(
                    LatLng.from(
                        latitude,
                        longitude,
                    ),
                ).setStyles(styles)
        val layer: LabelLayer = map.labelManager?.layer ?: return

        layer.addLabel(options)
    }
}
