package io.coursepick.coursepick.presentation.filter

import android.os.Parcelable
import io.coursepick.coursepick.domain.course.Meter
import io.coursepick.coursepick.presentation.course.CourseItem
import io.coursepick.coursepick.presentation.model.Difficulty
import kotlinx.parcelize.Parcelize

@Parcelize
data class CourseFilter(
    val lengthRange: FloatRange = FloatRange(MINIMUM_LENGTH_RANGE, MAXIMUM_LENGTH_RANGE),
    val difficulties: Set<Difficulty> = setOf(Difficulty.EASY, Difficulty.NORMAL, Difficulty.HARD),
) : Parcelable {
    private val minimumLength: Meter get() = Meter.kmToMeter(lengthRange.first)
    private val maximumLength: Meter
        get() =
            if (lengthRange.last == MAXIMUM_LENGTH_RANGE) {
                Meter(Int.MAX_VALUE)
            } else {
                Meter.kmToMeter(lengthRange.last)
            }

    fun matches(courseItem: CourseItem): Boolean =
        (difficulties.isEmpty() || courseItem.difficulty in difficulties) &&
            (Meter(courseItem.length) in minimumLength..maximumLength)

    companion object {
        const val MINIMUM_LENGTH_RANGE = 0F
        const val MAXIMUM_LENGTH_RANGE = 21F
    }
}
