package io.coursepick.coursepick.presentation.filter

import io.coursepick.coursepick.domain.course.Kilometer

sealed interface CourseFilterAction {
    data object Cancel : CourseFilterAction

    data object Reset : CourseFilterAction

    data object Apply : CourseFilterAction

    data class UpdateLengthRange(
        val start: Kilometer,
        val end: Kilometer,
    ) : CourseFilterAction
}
