package io.coursepick.coursepick.presentation.map

import android.animation.ValueAnimator
import io.coursepick.coursepick.domain.course.Coordinate
import io.coursepick.coursepick.domain.course.Latitude
import io.coursepick.coursepick.domain.course.Longitude

object CoordinateAnimator {
    const val DEFAULT_ANIMATION_DURATION = 750L

    fun animator(
        start: Coordinate,
        end: Coordinate,
        duration: Long = DEFAULT_ANIMATION_DURATION,
        onChange: (Coordinate) -> Unit,
    ): ValueAnimator {
        val valueAnimator = ValueAnimator.ofFloat(0F, 1F).setDuration(duration)
        valueAnimator.addUpdateListener { animator: ValueAnimator ->
            val latitude = (end.latitude.value - start.latitude.value) * animator.animatedFraction + start.latitude.value
            val longitude = (end.longitude.value - start.longitude.value) * animator.animatedFraction + start.longitude.value
            onChange(Coordinate(Latitude(latitude), Longitude(longitude)))
        }
        return valueAnimator
    }
}
