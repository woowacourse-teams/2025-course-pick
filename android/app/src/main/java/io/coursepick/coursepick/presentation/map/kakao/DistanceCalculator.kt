package io.coursepick.coursepick.presentation.map.kakao

import android.location.Location
import io.coursepick.coursepick.domain.course.Coordinate

object DistanceCalculator {
    fun distance(
        from: Coordinate,
        to: Coordinate,
    ): Int? {
        val result = FloatArray(1)
        Location.distanceBetween(
            from.latitude.value,
            from.longitude.value,
            to.latitude.value,
            to.longitude.value,
            result,
        )
        return result.firstOrNull()?.toInt()
    }
}
