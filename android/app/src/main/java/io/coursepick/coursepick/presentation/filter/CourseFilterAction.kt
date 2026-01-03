package io.coursepick.coursepick.presentation.filter

import io.coursepick.coursepick.presentation.model.Difficulty

sealed interface CourseFilterAction {
    data object Cancel : CourseFilterAction

    data object Reset : CourseFilterAction

    data object Apply : CourseFilterAction

    data class UpdateLengthRange(
        val start: Double,
        val end: Double,
    ) : CourseFilterAction

    data class ToggleDifficulty(
        val difficulty: Difficulty,
    ) : CourseFilterAction
}
