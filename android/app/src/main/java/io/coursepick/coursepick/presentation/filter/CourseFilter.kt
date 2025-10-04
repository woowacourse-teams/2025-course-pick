package io.coursepick.coursepick.presentation.filter

import android.os.Parcelable
import io.coursepick.coursepick.presentation.model.Difficulty
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue

@Parcelize
data class CourseFilter(
    val lengthRange: @RawValue IntRange = IntRange(0, Int.MAX_VALUE),
    val difficulties: Set<Difficulty> = setOf(Difficulty.EASY, Difficulty.NORMAL, Difficulty.HARD),
) : Parcelable {
    val minimumLengthKm: Int get() = lengthRange.first / 1000
    val maximumLengthKm: Int
        get() =
            if (lengthRange.last == Int.MAX_VALUE) MAXIMUM_LENGTH_RANGE else lengthRange.last / 1000

    companion object {
        const val MINIMUM_LENGTH_RANGE = 0
        const val MAXIMUM_LENGTH_RANGE = 21
    }
}
