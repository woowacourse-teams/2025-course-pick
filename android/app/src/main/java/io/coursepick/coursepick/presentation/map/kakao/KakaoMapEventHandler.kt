package io.coursepick.coursepick.presentation.map.kakao

import android.graphics.Point
import android.graphics.PointF
import com.kakao.vectormap.GestureType
import com.kakao.vectormap.KakaoMap
import com.kakao.vectormap.LatLng
import com.kakao.vectormap.Poi
import com.kakao.vectormap.camera.CameraPosition
import io.coursepick.coursepick.domain.course.Coordinate
import io.coursepick.coursepick.presentation.Logger
import io.coursepick.coursepick.presentation.course.CourseItem
import io.coursepick.coursepick.presentation.map.CameraMoveReason
import kotlin.math.pow
import kotlin.math.sqrt

class KakaoMapEventHandler {
    private var courses: List<CourseItem> = listOf()

    fun updateCourses(courses: List<CourseItem>) {
        this.courses = courses
    }

    fun setOnCourseClickListener(
        map: KakaoMap,
        onClick: (CourseItem) -> Unit,
    ) {
        map.setOnMapClickListener { kakaoMap: KakaoMap, latLng: LatLng, target: PointF, poi: Poi ->
            Logger.log(
                Logger.Event.Click("map"),
                "latitude" to latLng.latitude,
                "longitude" to latLng.longitude,
                "pixel_x" to target.x,
                "pixel_y" to target.y,
                "point_of_interest" to poi.name,
            )

            for (course: CourseItem in courses) {
                if (course.isNear(kakaoMap, target)) {
                    Logger.log(
                        Logger.Event.Click("course_on_map"),
                        "id" to course.id,
                        "name" to course.name,
                    )
                    onClick(course)
                    break
                }
            }
        }
    }

    fun setOnCameraMoveListener(
        map: KakaoMap,
        onCameraMove: (coordinate: Coordinate?, reason: CameraMoveReason) -> Unit,
    ) {
        map.setOnCameraMoveStartListener { _, gestureType: GestureType ->
            onCameraMove(
                map.cameraPosition?.position?.toCoordinate(),
                if (gestureType == GestureType.Unknown) {
                    CameraMoveReason.SYSTEM
                } else {
                    CameraMoveReason.GESTURE
                },
            )
        }

        map.setOnCameraMoveEndListener { _, cameraPosition: CameraPosition, gestureType: GestureType ->
            onCameraMove(
                cameraPosition.position?.toCoordinate(),
                if (gestureType == GestureType.Unknown) {
                    CameraMoveReason.SYSTEM
                } else {
                    CameraMoveReason.GESTURE
                },
            )
        }
    }

    private fun CourseItem.isNear(
        map: KakaoMap,
        target: PointF,
    ): Boolean {
        val points: List<Point?> =
            coordinates.map { coordinate: Coordinate ->
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
