package io.coursepick.coursepick.presentation.map.kakao

import android.content.Context
import androidx.annotation.ColorRes
import androidx.annotation.DimenRes
import com.kakao.vectormap.LatLng
import com.kakao.vectormap.route.RouteLineOptions
import com.kakao.vectormap.route.RouteLinePattern
import com.kakao.vectormap.route.RouteLineSegment
import com.kakao.vectormap.route.RouteLineStyle
import com.kakao.vectormap.route.RouteLineStyles
import io.coursepick.coursepick.R
import io.coursepick.coursepick.domain.course.Coordinate
import io.coursepick.coursepick.domain.course.InclineType
import io.coursepick.coursepick.domain.course.Segment
import io.coursepick.coursepick.presentation.course.CourseItem

class RouteLineOptionsFactory(
    private val context: Context,
) {
    private val patternDistance: Float =
        context.resources.getDimension(R.dimen.course_pattern_between_distance)

    private val uphillStyle =
        RouteLineStyles(R.color.course_uphill, R.dimen.selected_course_width, true)
    private val flatStyle =
        RouteLineStyles(R.color.course_flat, R.dimen.selected_course_width, true)
    private val downhillStyle =
        RouteLineStyles(R.color.course_downhill, R.dimen.selected_course_width, true)
    private val unknownStyle =
        RouteLineStyles(R.color.course_unknown, R.dimen.selected_course_width, true)
    private val unselectedStyle =
        RouteLineStyles(R.color.course_unselected, R.dimen.unselected_course_width, false)
    private val routeStyle =
        RouteLineStyles(R.color.course_route, R.dimen.course_route_width, false)

    fun routeLineOptions(route: List<Coordinate>): RouteLineOptions =
        RouteLineOptions.from(
            RouteLineSegment.from(
                route.map(Coordinate::toLatLng),
                routeStyle,
            ),
        )

    fun routeLineOptions(course: CourseItem): RouteLineOptions {
        val segments: List<RouteLineSegment> =
            course.segments.map { segment: Segment ->
                routeLineSegmentWithStyle(
                    segment,
                    course.selected,
                )
            }
        return RouteLineOptions.from(segments)
    }

    private fun RouteLineStyles(
        @ColorRes colorRes: Int,
        @DimenRes dimenRes: Int,
        withPattern: Boolean,
    ): RouteLineStyles {
        val baseStyle =
            RouteLineStyle
                .from(context.resources.getDimension(dimenRes), context.getColor(colorRes))
                .apply {
                    if (withPattern) {
                        setPattern(RouteLinePattern.from(R.drawable.image_arrow, patternDistance))
                    }
                }

        return RouteLineStyles.from(baseStyle)
    }

    private fun routeLineSegmentWithStyle(
        segment: Segment,
        selected: Boolean,
    ): RouteLineSegment {
        val points: List<LatLng> =
            segment.coordinates.map { coordinate: Coordinate -> coordinate.toLatLng() }
        val styles: RouteLineStyles =
            if (selected) segment.inclineType.routeLineStyles else unselectedStyle

        return RouteLineSegment.from(points, styles)
    }

    private val InclineType.routeLineStyles
        get() =
            when (this) {
                InclineType.UPHILL -> uphillStyle
                InclineType.DOWNHILL -> downhillStyle
                InclineType.FLAT -> flatStyle
                InclineType.UNKNOWN -> unknownStyle
            }
}
