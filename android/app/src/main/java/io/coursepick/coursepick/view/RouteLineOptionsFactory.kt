package io.coursepick.coursepick.view

import android.content.Context
import com.kakao.vectormap.LatLng
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

class RouteLineOptionsFactory(
    context: Context,
) {
    private val lineWidth: Float = context.resources.getDimension(R.dimen.course_route_width)
    private val patternDistance: Float =
        context.resources.getDimension(R.dimen.course_pattern_between_distance)

    private val uphillStyle = RouteLineStyles(context.getColor(R.color.course_difficulty_hard))
    private val flatStyle = RouteLineStyles(context.getColor(R.color.course_difficulty_normal))
    private val downhillStyle = RouteLineStyles(context.getColor(R.color.course_difficulty_easy))
    private val unknownStyle = RouteLineStyles(context.getColor(R.color.course_difficulty_none))

    private val stylesSet =
        RouteLineStylesSet.from(uphillStyle, flatStyle, downhillStyle, unknownStyle)

    fun routeLineOptions(course: CourseItem): RouteLineOptions {
        val segments: List<RouteLineSegment?> =
            course.segments.map { segment: Segment -> segment.toRouteLineSegment() }
        return RouteLineOptions.from(segments).setStylesSet(stylesSet)
    }

    private fun RouteLineStyles(color: Int): RouteLineStyles {
        val baseStyle =
            RouteLineStyle
                .from(lineWidth, color)
                .setPattern(RouteLinePattern.from(R.drawable.image_arrow, patternDistance))
        return RouteLineStyles.from(baseStyle)
    }

    private fun Segment.toRouteLineSegment(): RouteLineSegment? {
        val points: List<LatLng> = coordinates.map { it.toLatLng() }
        val styles: RouteLineStyles = inclineType.routeLineStyles

        return RouteLineSegment.from(points, styles)
    }

    private fun Coordinate.toLatLng() = LatLng.from(latitude.value, longitude.value)

    private val InclineType.routeLineStyles
        get() =
            when (this) {
                InclineType.UPHILL -> uphillStyle
                InclineType.DOWNHILL -> downhillStyle
                InclineType.FLAT -> flatStyle
                InclineType.UNKNOWN -> unknownStyle
            }
}
