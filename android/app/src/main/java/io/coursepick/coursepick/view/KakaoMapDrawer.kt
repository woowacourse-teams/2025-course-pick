package io.coursepick.coursepick.view

import android.Manifest
import android.content.Context
import android.location.Location
import android.util.Log
import androidx.annotation.RequiresPermission
import com.kakao.vectormap.KakaoMap
import com.kakao.vectormap.LatLng
import com.kakao.vectormap.label.LabelLayer
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
    private val locationProvider: LocationProvider = LocationProvider(context)

    fun drawCourse(
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

    @RequiresPermission(anyOf = [Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION])
    fun drawCurrentLocation(map: KakaoMap) {
        locationProvider.fetchCurrentLocation(
            onSuccess = { location: Location ->
                val styles: LabelStyles? =
                    map.labelManager
                        ?.addLabelStyles(LabelStyles.from(LabelStyle.from(R.drawable.image_current_location)))
                val options: LabelOptions =
                    LabelOptions
                        .from(
                            LatLng.from(
                                location.latitude,
                                location.longitude,
                            ),
                        ).setStyles(styles)

                val layer: LabelLayer? = map.labelManager?.layer

                layer?.addLabel(options)
            },
            onFailure = { exception: Exception ->
                Log.e("Location", "위치 조회 실패: ${exception.message}")
            },
        )
    }

    private fun CourseItem.toLatLngs() = coordinates.map { coordinate: Coordinate -> coordinate.toLatLng() }

    private fun Coordinate.toLatLng() = LatLng.from(latitude.value, longitude.value)

    companion object {
        private const val STYLE_ID = "CoursePickRouteLineStyle"
        private const val LINE_COLOR = 0xFF0000FF.toInt()
    }
}
