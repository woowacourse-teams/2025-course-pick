package io.coursepick.coursepick.presentation.filter

import android.os.Parcelable
import io.coursepick.coursepick.presentation.model.Difficulty
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue

@Parcelize
data class CourseFilter(
    val lengthRange: @RawValue IntRange = IntRange(0, Int.MAX_VALUE),
    val difficulties: Set<Difficulty> = setOf(Difficulty.EASY, Difficulty.NORMAL, Difficulty.HARD),
) : Parcelable
