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

    private val uphillStyle =
        RouteLineStyles.from(
            RouteLineStyle
                .from(
                    lineWidth,
                    context.getColor(R.color.course_difficulty_hard),
                ).arrowPatternedStyle(),
        )
    private val flatStyle: RouteLineStyles =
        RouteLineStyles.from(
            RouteLineStyle
                .from(
                    lineWidth,
                    context.getColor(R.color.course_difficulty_normal),
                ).arrowPatternedStyle(),
        )
    private val downhillStyle =
        RouteLineStyles.from(
            RouteLineStyle
                .from(
                    lineWidth,
                    context.getColor(R.color.course_difficulty_easy),
                ).arrowPatternedStyle(),
        )

    private val unknownStyle =
        RouteLineStyles.from(
            RouteLineStyle
                .from(
                    lineWidth,
                    context.getColor(R.color.course_difficulty_none),
                ).arrowPatternedStyle(),
        )
    private val stylesSet =
        RouteLineStylesSet.from(uphillStyle, flatStyle, downhillStyle, unknownStyle)

    private val patternDistance: Float =
        context.resources.getDimension(R.dimen.course_pattern_between_distance)

    fun routeLineOptions(course: CourseItem): RouteLineOptions {
        val segments: List<RouteLineSegment> =
            course.segments.map { segment: Segment -> segment.toRouteLineSegment() }
        return RouteLineOptions.from(segments).setStylesSet(stylesSet)
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

    private fun RouteLineStyle.arrowPatternedStyle(): RouteLineStyle =
        setPattern(RouteLinePattern.from(R.drawable.image_arrow, patternDistance))
}
