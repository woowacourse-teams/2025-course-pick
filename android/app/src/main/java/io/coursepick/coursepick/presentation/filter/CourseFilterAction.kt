package io.coursepick.coursepick.presentation.filter

sealed interface CourseFilterAction {
    data object Cancel : CourseFilterAction

    data object Reset : CourseFilterAction

    data object Apply : CourseFilterAction

    data class UpdateLengthRange(
        val start: Double,
        val end: Double,
    ) : CourseFilterAction
}
