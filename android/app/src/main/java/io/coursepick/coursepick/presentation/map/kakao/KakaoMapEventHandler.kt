package io.coursepick.coursepick.presentation.map.kakao

import android.graphics.Point
import android.graphics.PointF
import com.kakao.vectormap.GestureType
import com.kakao.vectormap.KakaoMap
import com.kakao.vectormap.LatLng
import com.kakao.vectormap.Poi
import com.kakao.vectormap.camera.CameraPosition
import io.coursepick.coursepick.domain.course.Coordinate
import io.coursepick.coursepick.domain.course.Segment
import io.coursepick.coursepick.presentation.Logger
import io.coursepick.coursepick.presentation.course.CourseItem
import kotlin.math.pow
import kotlin.math.sqrt

class KakaoMapEventHandler {
    fun setOnCourseClickListener(
        map: KakaoMap,
        courses: List<CourseItem>,
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

            courses.forEach { course: CourseItem ->
                if (course.isNear(kakaoMap, target)) {
                    Logger.log(
                        Logger.Event.Click("course_on_map"),
                        "id" to course.id,
                        "name" to course.name,
                    )
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
        map.setOnCameraMoveStartListener { _, gestureType: GestureType ->
            val cameraPosition: CameraPosition? = map.cameraPosition
            Logger.log(
                Logger.Event.MapMoveStart("map"),
                "gesture_type" to gestureType.name,
                "latitude" to cameraPosition?.position?.latitude.toString(),
                "longitude" to cameraPosition?.position?.longitude.toString(),
                "height" to cameraPosition?.height.toString(),
                "tilt_angle" to cameraPosition?.tiltAngle.toString(),
                "rotation_angle" to cameraPosition?.rotationAngle.toString(),
                "zoom_level" to cameraPosition?.zoomLevel.toString(),
            )
            onCameraMove()
        }

        map.setOnCameraMoveEndListener { _, cameraPosition: CameraPosition, gestureType: GestureType ->
            Logger.log(
                Logger.Event.MapMoveEnd("map"),
                "gesture_type" to gestureType.name,
                "latitude" to cameraPosition.position.latitude,
                "longitude" to cameraPosition.position.longitude,
                "height" to cameraPosition.height,
                "tilt_angle" to cameraPosition.tiltAngle,
                "rotation_angle" to cameraPosition.rotationAngle,
                "zoom_level" to cameraPosition.zoomLevel,
            )
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
