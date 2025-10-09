package io.coursepick.coursepick.presentation.filter

import android.os.Parcelable
import io.coursepick.coursepick.presentation.model.Difficulty
import kotlinx.parcelize.Parcelize

@Parcelize
data class CourseFilter(
    val lengthRange: FloatRange = FloatRange(MINIMUM_LENGTH_RANGE, MAXIMUM_LENGTH_RANGE),
    val difficulties: Set<Difficulty> = setOf(Difficulty.EASY, Difficulty.NORMAL, Difficulty.HARD),
) : Parcelable {
    val minimumLengthMeter: Int get() = (lengthRange.first * KM_TO_METER).toInt()
    val maximumLengthMeter: Int
        get() =
            if (lengthRange.last == MAXIMUM_LENGTH_RANGE) Float.MAX_VALUE.toInt() else (lengthRange.last * KM_TO_METER).toInt()

    companion object {
        const val MINIMUM_LENGTH_RANGE = 0F
        const val MAXIMUM_LENGTH_RANGE = 21F
        private const val KM_TO_METER = 1000
    }
}
