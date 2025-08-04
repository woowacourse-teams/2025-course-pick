package io.coursepick.coursepick.view

import android.content.Context
import android.location.Location
import com.kakao.vectormap.KakaoMap
import com.kakao.vectormap.LatLng
import com.kakao.vectormap.label.Label
import com.kakao.vectormap.label.LabelLayer
import com.kakao.vectormap.label.LabelManager
import com.kakao.vectormap.label.LabelOptions
import com.kakao.vectormap.label.LabelStyle
import com.kakao.vectormap.label.LabelStyles
import com.kakao.vectormap.label.LabelTransition
import com.kakao.vectormap.label.Transition
import com.kakao.vectormap.route.RouteLine
import com.kakao.vectormap.route.RouteLineManager
import com.kakao.vectormap.route.RouteLineOptions
import io.coursepick.coursepick.R
import io.coursepick.coursepick.domain.Latitude
import io.coursepick.coursepick.domain.Longitude

class KakaoMapDrawer(
    context: Context,
) {
    private val routeLineOptionsFactory = RouteLineOptionsFactory(context)

    fun drawCourses(
        map: KakaoMap,
        courses: List<CourseItem>,
    ) {
        val manager: RouteLineManager = map.routeLineManager ?: return
        val oldRouteLines: Array<RouteLine> = manager.layer?.allRouteLines ?: return
        courses.forEach { course: CourseItem ->
            val options: RouteLineOptions = routeLineOptionsFactory.routeLineOptions(course)
            manager.layer?.addRouteLine(options)
        }
        manager.layer?.removeAll()
        oldRouteLines.forEach { routeLine: RouteLine ->
            manager.remove(routeLine)
        }
    }

    fun showUserPosition(
        map: KakaoMap,
        location: Location,
    ) {
        val manager: LabelManager = map.labelManager ?: return
        val layer: LabelLayer = manager.layer ?: return
        val labelId: Int = R.drawable.image_current_location
        val latLng = LatLng.from(location.latitude, location.longitude)
        val label: Label? = layer.getLabel(labelId.toString())
        if (label == null) {
            val styles: LabelStyles =
                manager.addLabelStyles(LabelStyles.from(LabelStyle.from(labelId))) ?: return
            val options: LabelOptions =
                LabelOptions.from(latLng).setStyles(styles)
            options.labelId = labelId.toString()
            layer.addLabel(options)
            return
        }
        label.moveTo(latLng, LABEL_MOVE_ANIMATION_DURATION)
    }

    fun showSearchPosition(
        map: KakaoMap,
        latitude: Latitude,
        longitude: Longitude,
    ) {
        val manager: LabelManager = map.labelManager ?: return
        val layer: LabelLayer = manager.layer ?: return
        val labelId: Int = R.drawable.image_search_location
        val label: Label? = layer.getLabel(labelId.toString())
        val styles: LabelStyles =
            manager.addLabelStyles(
                LabelStyles.from(
                    LabelStyle
                        .from(labelId)
                        .setAnchorPoint(0.5F, 0.5F)
                        .setIconTransition(
                            LabelTransition.from(Transition.None, Transition.None),
                        ),
                ),
            ) ?: return
        val options: LabelOptions =
            LabelOptions.from(LatLng.from(latitude.value, longitude.value)).setStyles(styles)
        options.labelId = labelId.toString()
        layer.remove(label)
        layer.addLabel(options)
    }

    fun removeAllLabels(map: KakaoMap) {
        val layer: LabelLayer = map.labelManager?.layer ?: return
        layer.removeAll()
    }

    companion object {
        private const val LABEL_MOVE_ANIMATION_DURATION = 500
    }
}
