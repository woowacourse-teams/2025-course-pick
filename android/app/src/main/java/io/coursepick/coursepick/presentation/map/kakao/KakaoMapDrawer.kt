package io.coursepick.coursepick.presentation.map.kakao

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
import io.coursepick.coursepick.domain.course.Coordinate
import io.coursepick.coursepick.presentation.course.CourseItem

class KakaoMapDrawer(
    context: Context,
) {
    private val routeLineOptionsFactory = RouteLineOptionsFactory(context)

    fun drawCourse(
        map: KakaoMap,
        course: CourseItem,
    ) {
        val layer: RouteLineLayer = map.routeLineManager?.layer ?: return
        val options: RouteLineOptions =
            routeLineOptionsFactory.routeLineOptions(course).apply {
                zOrder = if (course.selected) SELECTED_COURSE_Z_ORDER else UNSELECTED_COURSE_Z_ORDER
            }
        layer.addRouteLine(options)
    }

    fun drawCourses(
        map: KakaoMap,
        courses: List<CourseItem>,
    ) {
        val layer: RouteLineLayer = map.routeLineManager?.layer ?: return
        courses.forEach { course: CourseItem ->
            val options: RouteLineOptions =
                routeLineOptionsFactory.routeLineOptions(course).apply {
                    zOrder =
                        if (course.selected) SELECTED_COURSE_Z_ORDER else UNSELECTED_COURSE_Z_ORDER
                }
            layer.addRouteLine(options)
        }
    }

    fun drawRouteToCourse(
        map: KakaoMap,
        route: List<Coordinate>,
        course: CourseItem,
    ) {
        val layer: RouteLineLayer = map.routeLineManager?.layer ?: return
        val courseOptions = routeLineOptionsFactory.routeLineOptions(course)
        layer.addRouteLine(routeLineOptionsFactory.routeLineOptions(route))
        layer.addRouteLine(courseOptions)
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

    fun removeAllLines(map: KakaoMap) {
        val layer: RouteLineLayer = map.routeLineManager?.layer ?: return
        layer.removeAll()
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
        private const val SELECTED_COURSE_Z_ORDER = 1
        private const val UNSELECTED_COURSE_Z_ORDER = 0
    }
}
