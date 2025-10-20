package io.coursepick.coursepick.presentation.filter

import io.coursepick.coursepick.domain.course.Meter
import io.coursepick.coursepick.presentation.course.CourseItem
import io.coursepick.coursepick.presentation.model.Difficulty

data class CourseFilter(
    val lengthRange: ClosedFloatingPointRange<Float> = MINIMUM_LENGTH_RANGE..MAXIMUM_LENGTH_RANGE,
    val difficulties: Set<Difficulty> = setOf(Difficulty.EASY, Difficulty.NORMAL, Difficulty.HARD),
) {
    private val minimumLength: Meter get() = Meter.kmToMeter(lengthRange.start)
    private val maximumLength: Meter
        get() =
            if (lengthRange.start == MAXIMUM_LENGTH_RANGE) {
                Meter.MAX_VALUE
            } else {
                Meter.kmToMeter(lengthRange.endInclusive)
            }

    fun matches(courseItem: CourseItem): Boolean =
        (difficulties.isEmpty() || courseItem.difficulty in difficulties) &&
            (Meter(courseItem.length) in minimumLength..maximumLength)

    companion object {
        const val MINIMUM_LENGTH_RANGE = 0F
        const val MAXIMUM_LENGTH_RANGE = 21F
    }
}
