package io.coursepick.coursepick.presentation.course

import androidx.annotation.StringRes
import io.coursepick.coursepick.domain.course.Course
import io.coursepick.coursepick.domain.course.InclineSummary
import io.coursepick.coursepick.domain.course.Segment
import io.coursepick.coursepick.presentation.model.Difficulty
import io.coursepick.coursepick.presentation.model.stringResourceId

data class CourseItem(
    val course: Course,
    val selected: Boolean,
    val favorite: Boolean,
) {
    val id: String = course.id
    val name: String = course.name.value
    val distance: Int? =
        course.distance
            ?.meter
            ?.value
            ?.toInt()
    val length: Int =
        course.length.meter.value
            .toInt()
    val segments: List<Segment> = course.segments
    val roadType: String = course.roadType
    val difficulty: Difficulty = Difficulty(course.difficulty)

    @StringRes
    val inclineSummaryStringResourceId: Int = course.inclineSummary.stringResourceId

    val isInclineSummaryUnknown: Boolean = course.inclineSummary == InclineSummary.UNKNOWN
}
