package io.coursepick.coursepick.presentation.filter

import io.coursepick.coursepick.domain.course.Kilometer
import io.coursepick.coursepick.domain.course.Meter
import io.coursepick.coursepick.presentation.course.CourseItem
import io.coursepick.coursepick.presentation.model.Difficulty

data class CourseFilter(
    val lengthRange: ClosedRange<Kilometer> =
        Kilometer(MINIMUM_LENGTH_RANGE)..Kilometer(
            MAXIMUM_LENGTH_RANGE,
        ),
    val difficulties: Set<Difficulty> = setOf(Difficulty.EASY, Difficulty.NORMAL, Difficulty.HARD),
) {
    private val minimumLength: Meter = lengthRange.start.toMeter()
    private val maximumLength: Meter =
        if (lengthRange.endInclusive == Kilometer(MAXIMUM_LENGTH_RANGE)) {
            Meter.MAX_VALUE
        } else {
            lengthRange.endInclusive.toMeter()
        }

    fun matches(courseItem: CourseItem): Boolean =
        courseItem.difficulty in difficulties && Meter(courseItem.length) in minimumLength..maximumLength

    companion object {
        const val MINIMUM_LENGTH_RANGE = 0.0
        const val MAXIMUM_LENGTH_RANGE = 21.0
    }
}
