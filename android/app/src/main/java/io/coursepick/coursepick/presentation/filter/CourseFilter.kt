package io.coursepick.coursepick.presentation.filter

import io.coursepick.coursepick.domain.course.Kilometer

data class CourseFilter(
    val lengthRange: ClosedRange<Kilometer>,
) {
    val lengthRangeAsFloat: ClosedFloatingPointRange<Float> =
        lengthRange.start.value.toFloat()..lengthRange.endInclusive.value.toFloat()

    companion object {
        val MINIMUM_LENGTH_RANGE = Kilometer(0.0)
        val MAXIMUM_LENGTH_RANGE = Kilometer(21.0)
        val None =
            CourseFilter(
                lengthRange = MINIMUM_LENGTH_RANGE..MAXIMUM_LENGTH_RANGE,
            )
    }
}
