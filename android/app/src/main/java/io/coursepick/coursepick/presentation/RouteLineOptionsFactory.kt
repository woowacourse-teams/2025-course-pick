package io.coursepick.coursepick.presentation

import android.content.Context
import com.kakao.vectormap.LatLng
import com.kakao.vectormap.route.RouteLineOptions
import com.kakao.vectormap.route.RouteLinePattern
import com.kakao.vectormap.route.RouteLineSegment
import com.kakao.vectormap.route.RouteLineStyle
import com.kakao.vectormap.route.RouteLineStyles
import io.coursepick.coursepick.R
import io.coursepick.coursepick.domain.Coordinate
import io.coursepick.coursepick.domain.InclineType
import io.coursepick.coursepick.domain.Segment
import io.coursepick.coursepick.presentation.model.course.CourseItem

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
    private val unselectedStyle = RouteLineStyles(context.getColor(R.color.course_unselected))

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

    private fun RouteLineStyles(color: Int): RouteLineStyles {
        val baseStyle =
            RouteLineStyle
                .from(lineWidth, color)
                .setPattern(RouteLinePattern.from(R.drawable.image_arrow, patternDistance))
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
