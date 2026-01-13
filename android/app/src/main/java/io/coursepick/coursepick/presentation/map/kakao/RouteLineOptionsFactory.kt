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
import io.coursepick.coursepick.presentation.course.CourseItem

class RouteLineOptionsFactory(
    private val context: Context,
) {
    private val patternDistance: Float =
        context.resources.getDimension(R.dimen.course_pattern_between_distance)

    private val selectedStyle =
        RouteLineStyles(R.color.course_selected, R.dimen.selected_course_width, true)
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

    fun routeLineOptions(course: CourseItem): RouteLineOptions =
        RouteLineOptions.from(
            routeLineSegmentWithStyle(
                course.coordinates,
                course.selected,
            ),
        )

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
        coordinates: List<Coordinate>,
        selected: Boolean,
    ): RouteLineSegment {
        val points: List<LatLng> =
            coordinates.map { coordinate: Coordinate -> coordinate.toLatLng() }
        val styles: RouteLineStyles = if (selected) selectedStyle else unselectedStyle

        return RouteLineSegment.from(points, styles)
    }
}
