package io.coursepick.coursepick.view

import android.content.Context
import com.kakao.vectormap.KakaoMap
import com.kakao.vectormap.LatLng
import com.kakao.vectormap.route.RouteLineLayer
import com.kakao.vectormap.route.RouteLineOptions
import com.kakao.vectormap.route.RouteLineSegment
import com.kakao.vectormap.route.RouteLineStyle
import com.kakao.vectormap.route.RouteLineStyles
import com.kakao.vectormap.route.RouteLineStylesSet
import io.coursepick.coursepick.domain.Coordinate

class KakaoMapDrawer(
    private val context: Context,
) {
    fun drawCourse(
        kakaoMap: KakaoMap,
        course: CourseItem,
    ) {
        val layer: RouteLineLayer = kakaoMap.routeLineManager?.layer ?: return
        layer.removeAll()

        val lineWidthPx: Float = context.dpToPx(LINE_WIDTH_DP)
        val styleSet: RouteLineStylesSet =
            RouteLineStylesSet.from(
                STYLE_ID,
                RouteLineStyles.from(RouteLineStyle.from(lineWidthPx, LINE_COLOR)),
            )
        val segment = RouteLineSegment.from(course.toLatLngs()).setStyles(styleSet.getStyles(0))
        val options = RouteLineOptions.from(segment).setStylesSet(styleSet)
        layer.addRouteLine(options)
    }

    private fun CourseItem.toLatLngs() = coordinates.map { coordinate: Coordinate -> coordinate.toLatLng() }

    private fun Coordinate.toLatLng() = LatLng.from(latitude.value, longitude.value)

    private fun Context.dpToPx(dp: Float): Float = dp * resources.displayMetrics.density

    companion object {
        private const val STYLE_ID = "CoursePickRouteLineStyle"
        private const val LINE_WIDTH_DP = 4f
        private const val LINE_COLOR = 0xFF0000FF.toInt()
    }
}
