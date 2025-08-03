package io.coursepick.coursepick.view

import android.content.Context
import android.location.Location
import androidx.annotation.DrawableRes
import com.kakao.vectormap.KakaoMap
import com.kakao.vectormap.LatLng
import com.kakao.vectormap.label.Label
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

    fun drawCourses(
        map: KakaoMap,
        courses: List<CourseItem>,
    ) {
        val layer: RouteLineLayer = map.routeLineManager?.layer ?: return
        layer.removeAll()
        courses.forEach { course: CourseItem ->
            val options: RouteLineOptions = routeLineOptionsFactory.routeLineOptions(course)
            layer.addRouteLine(options)
        }
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
        val manager: LabelManager = map.labelManager ?: return
        val layer: LabelLayer = manager.layer ?: return
        val labelId: String = iconResourceId.toString()
        val label: Label? = layer.getLabel(labelId)
        if (label == null) {
            val styles: LabelStyles =
                manager.addLabelStyles(LabelStyles.from(LabelStyle.from(iconResourceId))) ?: return
            val options: LabelOptions =
                LabelOptions.from(LatLng.from(latitude, longitude)).setStyles(styles)
            options.labelId = labelId
            layer.addLabel(options)
            return
        }
        label.moveTo(LatLng.from(latitude, longitude), LABEL_MOVE_ANIMATION_DURATION)
    }

    companion object {
        private const val LABEL_MOVE_ANIMATION_DURATION = 500
    }
}
