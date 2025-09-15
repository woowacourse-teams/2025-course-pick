package io.coursepick.coursepick.presentation.course

import io.coursepick.coursepick.domain.course.Difficulty
import io.coursepick.coursepick.domain.course.LengthRange

data class FilterCondition(
    val lengthRange: LengthRange = LengthRange(0, Int.MAX_VALUE),
    val difficulties: Set<Difficulty> = setOf(Difficulty.EASY, Difficulty.NORMAL, Difficulty.HARD),
)
