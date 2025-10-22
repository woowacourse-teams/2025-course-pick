package io.coursepick.coursepick.presentation.map.kakao

import android.content.Context
import android.location.Location
import com.kakao.vectormap.KakaoMap
import com.kakao.vectormap.LatLng
import com.kakao.vectormap.camera.CameraAnimation
import com.kakao.vectormap.camera.CameraUpdate
import com.kakao.vectormap.camera.CameraUpdateFactory
import io.coursepick.coursepick.R
import io.coursepick.coursepick.domain.course.Coordinate
import io.coursepick.coursepick.domain.course.Segment
import io.coursepick.coursepick.presentation.course.CourseItem

class KakaoMapCameraController(
    context: Context,
) {
    private val fitMapPadding =
        context.resources.getDimensionPixelSize(R.dimen.course_route_padding)

    fun moveTo(
        map: KakaoMap,
        location: Location,
    ) {
        val cameraUpdate: CameraUpdate = CameraUpdateFactory.newCenterPosition(location.toLatLng())
        val cameraAnimation = CameraAnimation.from(MOVE_ANIMATION_DURATION, true, false)
        map.moveCamera(cameraUpdate, cameraAnimation)
    }

    fun fitTo(
        course: CourseItem,
        map: KakaoMap,
    ) {
        val coordinates: List<Coordinate> =
            course.segments.flatMap { segment: Segment -> segment.coordinates }
        fitTo(coordinates, map)
    }

    fun resetZoomLevel(map: KakaoMap) {
        val cameraUpdate: CameraUpdate = CameraUpdateFactory.zoomTo(DEFAULT_ZOOM_LEVEL)
        map.moveCamera(cameraUpdate)
    }

    private fun fitTo(
        coordinates: List<Coordinate>,
        map: KakaoMap,
    ) {
        val latLngs: Array<LatLng> =
            coordinates.map(Coordinate::toLatLng).toTypedArray()
        val cameraUpdate: CameraUpdate = CameraUpdateFactory.fitMapPoints(latLngs, fitMapPadding)
        val cameraAnimation = CameraAnimation.from(MOVE_ANIMATION_DURATION, true, false)
        map.moveCamera(cameraUpdate, cameraAnimation)
    }

    companion object {
        private const val MOVE_ANIMATION_DURATION = 750
        private const val DEFAULT_ZOOM_LEVEL = 15
    }
}
