package io.coursepick.coursepick.view

import android.content.Context
import android.location.Location
import androidx.annotation.DrawableRes
import com.kakao.vectormap.KakaoMap
import com.kakao.vectormap.LatLng
import com.kakao.vectormap.label.Label
import com.kakao.vectormap.label.LabelLayer
import com.kakao.vectormap.label.LabelOptions
import com.kakao.vectormap.label.LabelStyle
import com.kakao.vectormap.label.LabelStyles
import com.kakao.vectormap.label.LabelTransition
import com.kakao.vectormap.label.Transition
import com.kakao.vectormap.route.RouteLineLayer
import com.kakao.vectormap.route.RouteLineOptions
import io.coursepick.coursepick.R
import io.coursepick.coursepick.domain.Coordinate

class KakaoMapDrawer(
    context: Context,
) {
    private val routeLineOptionsFactory = RouteLineOptionsFactory(context)

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

    fun showUserPosition(
        map: KakaoMap,
        location: Location,
    ) {
        val labelId: Int = R.drawable.image_current_location
        val styles = LabelStyles.from(LabelStyle.from(labelId))
        val latLng = location.toLatLng()
        updateLabel(map, latLng, labelId, styles) { _, label: Label ->
            label.moveTo(latLng, LABEL_MOVE_ANIMATION_DURATION)
        }
    }

    fun showSearchPosition(
        map: KakaoMap,
        coordinate: Coordinate,
    ) {
        val labelId: Int = R.drawable.image_search_location
        val styles =
            LabelStyles.from(
                LabelStyle
                    .from(labelId)
                    .setAnchorPoint(0.5F, 0.5F)
                    .setIconTransition(
                        LabelTransition.from(Transition.None, Transition.None),
                    ),
            )
        updateLabel(map, coordinate.toLatLng(), labelId, styles) {
            layer: LabelLayer,
            label: Label,
            ->
            layer.remove(label)
        }
    }

    fun removeAllLabels(map: KakaoMap) {
        val layer: LabelLayer = map.labelManager?.layer ?: return
        layer.removeAll()
    }

    private fun updateLabel(
        map: KakaoMap,
        position: LatLng,
        @DrawableRes labelIdRes: Int,
        styles: LabelStyles,
        handleOldLabel: (LabelLayer, Label) -> Unit,
    ) {
        val manager = map.labelManager ?: return
        val layer = manager.layer ?: return

        layer.getLabel(labelIdRes.toString())?.let { existingLabel ->
            handleOldLabel(layer, existingLabel)
            return
        }

        val options: LabelOptions = LabelOptions.from(position).setStyles(styles)
        options.labelId = labelIdRes.toString()
        layer.addLabel(options)
    }

    companion object {
        private const val LABEL_MOVE_ANIMATION_DURATION = 500
    }
}
