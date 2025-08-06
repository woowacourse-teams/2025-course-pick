package io.coursepick.coursepick.view

import android.content.Context
import android.location.Location
import com.kakao.vectormap.KakaoMap
import com.kakao.vectormap.LatLng
import com.kakao.vectormap.camera.CameraAnimation
import com.kakao.vectormap.camera.CameraUpdate
import com.kakao.vectormap.camera.CameraUpdateFactory
import io.coursepick.coursepick.R
import io.coursepick.coursepick.domain.Coordinate
import io.coursepick.coursepick.domain.Segment

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
        map.moveCamera(cameraUpdate)
    }

    fun fitTo(
        course: CourseItem,
        map: KakaoMap,
    ) {
        val coordinates: List<Coordinate> =
            course.segments.flatMap { segment: Segment -> segment.coordinates }
        fitTo(coordinates, map)
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
    }
}
