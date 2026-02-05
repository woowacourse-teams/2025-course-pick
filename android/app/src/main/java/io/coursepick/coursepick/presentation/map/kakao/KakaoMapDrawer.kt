package io.coursepick.coursepick.presentation.map.kakao

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import com.kakao.vectormap.KakaoMap
import com.kakao.vectormap.label.Label
import com.kakao.vectormap.label.LabelOptions
import com.kakao.vectormap.label.LabelStyle
import com.kakao.vectormap.label.LabelStyles
import com.kakao.vectormap.label.LabelTransition
import com.kakao.vectormap.label.Transition
import com.kakao.vectormap.route.RouteLineLayer
import com.kakao.vectormap.route.RouteLineOptions
import com.kakao.vectormap.shape.DotPoints
import com.kakao.vectormap.shape.Polygon
import com.kakao.vectormap.shape.PolygonOptions
import com.kakao.vectormap.shape.PolygonStyles
import com.kakao.vectormap.shape.PolygonStylesSet
import io.coursepick.coursepick.R
import io.coursepick.coursepick.domain.course.Coordinate
import io.coursepick.coursepick.presentation.course.CourseItem

class KakaoMapDrawer(
    private val context: Context,
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
        if (context.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            showAccurateUserPosition(map, location)
        } else {
            showApproximateUserPosition(map, location)
        }
    }

    fun hideUserPosition(map: KakaoMap) {
        hideAccurateUserPosition(map)
        hideApproximateUserPosition(map)
    }

    fun showSearchPosition(
        map: KakaoMap,
        coordinate: Coordinate,
    ) {
        val latLng = coordinate.toLatLng()
        val style =
            LabelStyle
                .from(R.drawable.image_search_location)
                .setAnchorPoint(0.5F, 0.5F)
                .setIconTransition(LabelTransition.from(Transition.None, Transition.None))
        val options: LabelOptions =
            LabelOptions
                .from(latLng)
                .setStyles(LabelStyles.from(style))
                .apply { labelId = SEARCH_POSITION_MARK_ID }

        updateOrAddLabel(map, options) { oldLabel: Label ->
            oldLabel.moveTo(latLng)
        }
    }

    fun removeAllLines(map: KakaoMap) {
        val layer: RouteLineLayer = map.routeLineManager?.layer ?: return
        layer.removeAll()
    }

    private fun showAccurateUserPosition(
        map: KakaoMap,
        location: Location,
    ) {
        hideApproximateUserPosition(map)

        val latLng = location.toLatLng()
        val style = LabelStyle.from(R.drawable.image_current_location).setAnchorPoint(0.5F, 0.5F)
        val options: LabelOptions =
            LabelOptions
                .from(latLng)
                .setStyles(LabelStyles.from(style))
                .apply { labelId = ACCURATE_USER_POSITION_ID }

        updateOrAddLabel(map, options) { oldLabel: Label ->
            oldLabel.moveTo(latLng, LABEL_MOVE_ANIMATION_DURATION)
        }
    }

    private fun showApproximateUserPosition(
        map: KakaoMap,
        location: Location,
    ) {
        hideAccurateUserPosition(map)

        val options =
            PolygonOptions
                .from(DotPoints.fromCircle(location.toLatLng(), location.accuracy))
                .setStylesSet(PolygonStylesSet.from(PolygonStyles.from(context.getColor(R.color.coarse_location_area))))
                .apply { polygonId = APPROXIMATE_USER_POSITION_ID }

        updateOrAddPolygon(map, options) { oldPolygon: Polygon ->
            oldPolygon.setPosition(location.toLatLng())
        }
    }

    private fun hideAccurateUserPosition(map: KakaoMap) {
        map.labelManager
            ?.layer
            ?.getLabel(ACCURATE_USER_POSITION_ID)
            ?.let(Label::remove)
    }

    private fun hideApproximateUserPosition(map: KakaoMap) {
        map.shapeManager
            ?.layer
            ?.getPolygon(APPROXIMATE_USER_POSITION_ID)
            ?.let(Polygon::remove)
    }

    private fun updateOrAddLabel(
        map: KakaoMap,
        options: LabelOptions,
        handleOldLabel: (Label) -> Unit,
    ) {
        val layer = map.labelManager?.layer ?: return
        layer.getLabel(options.labelId)?.let { oldLabel: Label ->
            handleOldLabel(oldLabel)
            return
        }
        layer.addLabel(options)
    }

    private fun updateOrAddPolygon(
        map: KakaoMap,
        options: PolygonOptions,
        handleOldPolygon: (Polygon) -> Unit,
    ) {
        val layer = map.shapeManager?.layer ?: return
        layer.getPolygon(options.polygonId)?.let { oldPolygon: Polygon ->
            handleOldPolygon(oldPolygon)
            return
        }
        layer.addPolygon(options)
    }

    companion object {
        private const val LABEL_MOVE_ANIMATION_DURATION = 500
        private const val SELECTED_COURSE_Z_ORDER = 1
        private const val UNSELECTED_COURSE_Z_ORDER = 0
        private const val SEARCH_POSITION_MARK_ID = "search_location_mark_id"
        private const val ACCURATE_USER_POSITION_ID = "Accurate user position id"
        private const val APPROXIMATE_USER_POSITION_ID = "Approximate user position id"
    }
}
