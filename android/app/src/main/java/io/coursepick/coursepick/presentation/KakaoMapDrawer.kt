package io.coursepick.coursepick.presentation

import android.content.Context
import android.location.Location
import com.kakao.vectormap.KakaoMap
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
import io.coursepick.coursepick.presentation.model.course.CourseItem

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
        val styles =
            LabelStyles.from(
                LabelStyle
                    .from(labelId)
                    .setAnchorPoint(0.5F, 0.5F),
            )
        val latLng = location.toLatLng()
        val options: LabelOptions = LabelOptions.from(latLng).setStyles(styles)
        options.labelId = labelId.toString()
        updateLabel(map, options) { label: Label ->
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
        val latLng = coordinate.toLatLng()
        val options: LabelOptions = LabelOptions.from(latLng).setStyles(styles)
        options.labelId = labelId.toString()
        updateLabel(map, options) { label: Label ->
            label.moveTo(latLng)
        }
    }

    fun removeAllLabels(map: KakaoMap) {
        val layer: LabelLayer = map.labelManager?.layer ?: return
        layer.removeAll()
    }

    private fun updateLabel(
        map: KakaoMap,
        options: LabelOptions,
        handleOldLabel: (Label) -> Unit,
    ) {
        val layer = map.labelManager?.layer ?: return
        layer.getLabel(options.labelId)?.let { existingLabel ->
            handleOldLabel(existingLabel)
            return
        }
        layer.addLabel(options)
    }

    companion object {
        private const val LABEL_MOVE_ANIMATION_DURATION = 500
    }
}
