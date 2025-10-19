package io.coursepick.coursepick.presentation.course

import androidx.annotation.StringRes
import io.coursepick.coursepick.R
import io.coursepick.coursepick.domain.course.Course
import io.coursepick.coursepick.domain.course.InclineSummary
import io.coursepick.coursepick.domain.course.Segment
import io.coursepick.coursepick.presentation.model.Difficulty

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

    private val InclineSummary.stringResourceId: Int
        get() =
            when (this) {
                InclineSummary.MOSTLY_FLAT -> R.string.incline_summary_flat
                InclineSummary.REPEATING_HILLS -> R.string.incline_summary_repeating_hills
                InclineSummary.SOMETIMES_UPHILL -> R.string.incline_summary_sometimes_uphill
                InclineSummary.SOMETIMES_DOWNHILL -> R.string.incline_summary_sometimes_downhill
                InclineSummary.CONTINUOUS_UPHILL -> R.string.incline_summary_continuous_uphill
                InclineSummary.CONTINUOUS_DOWNHILL -> R.string.incline_summary_continuous_downhill
                InclineSummary.UNKNOWN -> R.string.incline_summary_continuous_unknown
            }
}
