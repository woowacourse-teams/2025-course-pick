package io.coursepick.coursepick.view

import android.content.Context
import android.graphics.Color
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
import com.kakao.vectormap.route.RouteLinePattern
import com.kakao.vectormap.route.RouteLineSegment
import com.kakao.vectormap.route.RouteLineStyle
import com.kakao.vectormap.route.RouteLineStyles
import com.kakao.vectormap.route.RouteLineStylesSet
import io.coursepick.coursepick.R
import io.coursepick.coursepick.domain.Coordinate
import io.coursepick.coursepick.domain.InclineType
import io.coursepick.coursepick.domain.Segment

class KakaoMapDrawer(
    private val context: Context,
) {
    private val patternDistancePx: Float =
        context.resources.getDimension(R.dimen.course_pattern_between_distance)
    private val lineWidthPx: Float = context.resources.getDimension(R.dimen.course_route_width)
    private val uphillStyle =
        RouteLineStyles.from(RouteLineStyle.from(lineWidthPx, Color.RED).arrowPatternedStyle())
    private val flatStyle: RouteLineStyles =
        RouteLineStyles.from(RouteLineStyle.from(lineWidthPx, Color.GREEN).arrowPatternedStyle())
    private val downhillStyle =
        RouteLineStyles.from(RouteLineStyle.from(lineWidthPx, Color.BLUE).arrowPatternedStyle())

    private val unknownStyle =
        RouteLineStyles.from(RouteLineStyle.from(lineWidthPx, Color.GRAY).arrowPatternedStyle())

    private fun RouteLineStyle.arrowPatternedStyle(): RouteLineStyle =
        setPattern(RouteLinePattern.from(R.drawable.image_arrow, patternDistancePx))

    private val stylesSet =
        RouteLineStylesSet.from(uphillStyle, flatStyle, downhillStyle, unknownStyle)

    fun draw(
        map: KakaoMap,
        @DrawableRes
        iconResourceId: Int,
        coordinate: Coordinate,
    ) {
        draw(
            map,
            iconResourceId,
            coordinate.latitude.value,
            coordinate.longitude.value,
        )
    }

    fun draw(
        map: KakaoMap,
        @DrawableRes
        iconResourceId: Int,
        location: Location,
    ) {
        draw(
            map,
            iconResourceId,
            location.latitude,
            location.longitude,
        )
    }

    fun draw(
        kakaoMap: KakaoMap,
        course: CourseItem,
    ) {
        val layer: RouteLineLayer = kakaoMap.routeLineManager?.layer ?: return
        layer.removeAll()
        val segments: List<RouteLineSegment> = course.segments.map { it.toRouteLineSegment() }
        val options = RouteLineOptions.from(segments).setStylesSet(stylesSet)
        layer.addRouteLine(options)
    }

    fun removeAllLabels(map: KakaoMap) {
        val layer: LabelLayer = map.labelManager?.layer ?: return
        layer.removeAll()
    }

    private fun draw(
        map: KakaoMap,
        @DrawableRes
        iconResourceId: Int,
        latitude: Double,
        longitude: Double,
    ) {
        val manager: LabelManager = map.labelManager ?: return
        val layer: LabelLayer = manager.layer ?: return
        val label: Label? = layer.getLabel(CURRENT_LOCATION_LABEL_ID)
        if (label == null) {
            val styles: LabelStyles =
                manager.addLabelStyles(LabelStyles.from(LabelStyle.from(iconResourceId))) ?: return
            val options: LabelOptions =
                LabelOptions.from(LatLng.from(latitude, longitude)).setStyles(styles)
            options.labelId = CURRENT_LOCATION_LABEL_ID
            layer.addLabel(options)
            return
        }
        label.moveTo(LatLng.from(latitude, longitude), LABEL_MOVE_ANIMATION_DURATION)
    }

    private fun Segment.toRouteLineSegment(): RouteLineSegment =
        RouteLineSegment.from(
            coordinates.map { it.toLatLng() },
            when (inclineType) {
                InclineType.UPHILL -> uphillStyle
                InclineType.DOWNHILL -> downhillStyle
                InclineType.FLAT -> flatStyle
                InclineType.UNKNOWN -> unknownStyle
            },
        )

    private fun Coordinate.toLatLng() = LatLng.from(latitude.value, longitude.value)

    companion object {
        private const val STYLE_ID = "CoursePickRouteLineStyle"
        private const val CURRENT_LOCATION_LABEL_ID = "CurrentLocationLabel"
        private const val STYLE_INCLINE_TYPE_UPHILL = "STYLE_INCLINE_TYPE_UPHILL"
        private const val STYLE_INCLINE_TYPE_DOWNHILL = "STYLE_INCLINE_TYPE_DOWNHILL"
        private const val STYLE_INCLINE_TYPE_FLAT = "STYLE_INCLINE_TYPE_FLAT"
        private const val LINE_COLOR = 0xFF0000FF.toInt()
        private const val LABEL_MOVE_ANIMATION_DURATION = 500
    }
}
