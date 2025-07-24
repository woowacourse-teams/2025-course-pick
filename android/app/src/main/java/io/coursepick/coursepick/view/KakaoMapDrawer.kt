package io.coursepick.coursepick.view

import android.content.Context
import android.location.Location
import androidx.annotation.DrawableRes
import com.kakao.vectormap.KakaoMap
import com.kakao.vectormap.LatLng
import com.kakao.vectormap.label.LabelLayer
import com.kakao.vectormap.label.LabelManager
import com.kakao.vectormap.label.LabelOptions
import com.kakao.vectormap.label.LabelStyle
import com.kakao.vectormap.label.LabelStyles
import com.kakao.vectormap.route.RouteLineLayer
import com.kakao.vectormap.route.RouteLineOptions
import com.kakao.vectormap.route.RouteLineSegment
import com.kakao.vectormap.route.RouteLineStyle
import com.kakao.vectormap.route.RouteLineStyles
import com.kakao.vectormap.route.RouteLineStylesSet
import io.coursepick.coursepick.R
import io.coursepick.coursepick.domain.Coordinate

class KakaoMapDrawer(
    private val context: Context,
) {
    fun draw(
        kakaoMap: KakaoMap,
        course: CourseItem,
    ) {
        val layer: RouteLineLayer = kakaoMap.routeLineManager?.layer ?: return
        layer.removeAll()

        val lineWidthPx: Float = context.resources.getDimension(R.dimen.course_route_width)
        val styleSet =
            RouteLineStylesSet.from(
                STYLE_ID,
                RouteLineStyles.from(RouteLineStyle.from(lineWidthPx, LINE_COLOR)),
            )
        val segment = RouteLineSegment.from(course.toLatLngs()).setStyles(styleSet.getStyles(0))
        val options = RouteLineOptions.from(segment).setStylesSet(styleSet)
        layer.addRouteLine(options)
    }

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
        val labelManager: LabelManager = map.labelManager ?: return
        val styles: LabelStyles =
            labelManager.addLabelStyles(LabelStyles.from(LabelStyle.from(iconResourceId)))
                ?: return
        val options: LabelOptions =
            LabelOptions
                .from(
                    LatLng.from(
                        latitude,
                        longitude,
                    ),
                ).setStyles(styles)
        options.labelId = CURRENT_LOCATION_LABEL_ID
        val layer: LabelLayer = map.labelManager?.layer ?: return

        layer.addLabel(options)
    }

    private fun CourseItem.toLatLngs() = coordinates.map { coordinate: Coordinate -> coordinate.toLatLng() }

    private fun Coordinate.toLatLng() = LatLng.from(latitude.value, longitude.value)

    companion object {
        private const val STYLE_ID = "CoursePickRouteLineStyle"
        private const val CURRENT_LOCATION_LABEL_ID = "CurrentLocationLabel"
        private const val LINE_COLOR = 0xFF0000FF.toInt()
    }
}
