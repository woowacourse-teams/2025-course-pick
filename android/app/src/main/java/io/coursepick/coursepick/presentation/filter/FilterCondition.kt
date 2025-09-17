package io.coursepick.coursepick.presentation.filter

import android.os.Parcelable
import io.coursepick.coursepick.domain.course.Difficulty
import kotlinx.parcelize.Parcelize

@Parcelize
data class FilterCondition(
    val lengthRange: LengthRange = LengthRange(0, Int.MAX_VALUE),
    val difficulties: Set<Difficulty> = setOf(Difficulty.EASY, Difficulty.NORMAL, Difficulty.HARD),
) : Parcelable
