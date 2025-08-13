package io.coursepick.coursepick.presentation.map.kakao

import android.graphics.Point
import android.graphics.PointF
import com.kakao.vectormap.KakaoMap
import io.coursepick.coursepick.domain.Coordinate
import io.coursepick.coursepick.domain.Segment
import io.coursepick.coursepick.presentation.course.CourseItem
import kotlin.math.pow
import kotlin.math.sqrt

class KakaoMapEventHandler {
    fun setOnCourseClickListener(
        map: KakaoMap,
        courses: List<CourseItem>,
        onClick: (CourseItem) -> Unit,
    ) {
        map.setOnMapClickListener { kakaoMap: KakaoMap, _, target: PointF, _ ->
            courses.forEach { course: CourseItem ->
                if (course.isNear(kakaoMap, target)) {
                    onClick(course)
                    return@forEach
                }
            }
        }
    }

    fun setOnCameraMoveListener(
        map: KakaoMap,
        onCameraMove: () -> Unit,
    ) {
        map.setOnCameraMoveStartListener { _, _ ->
            onCameraMove()
        }
    }

    private fun CourseItem.isNear(
        map: KakaoMap,
        target: PointF,
    ): Boolean {
        val points: List<Point?> =
            segments.flatMap(Segment::coordinates).map { coordinate: Coordinate ->
                map.toScreenPoint(coordinate.toLatLng())
            }
        return (points.any { point: Point? -> point != null && point.isNear(target) })
    }

    private fun Point.isNear(point: PointF): Boolean {
        val distance = sqrt((this.x - point.x).pow(2) + (this.y - point.y).pow(2))
        return distance <= NEAR_TOUCH_THRESHOLD
    }

    companion object {
        private const val NEAR_TOUCH_THRESHOLD = 50
    }
}
