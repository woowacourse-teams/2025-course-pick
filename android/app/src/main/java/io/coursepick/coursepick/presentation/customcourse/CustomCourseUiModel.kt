package io.coursepick.coursepick.presentation.customcourse

import androidx.annotation.StringRes
import io.coursepick.coursepick.R
import io.coursepick.coursepick.domain.course.Course
import io.coursepick.coursepick.domain.course.Distance
import io.coursepick.coursepick.domain.course.InclineSummary

data class CustomCourseUiModel(
    val course: Course,
    val selected: Boolean,
) {
    val distance: String? =
        course.distance?.let { distance: Distance ->
            if (distance.meter < 1000) {
                "${distance.meter.value.toInt()}m"
            } else {
                String.format(
                    locale = null,
                    format = "%.2f",
                    distance.meter.toKilometer().value,
                ) + "km"
            }
        }

    @StringRes
    val inclineSummaryStringResourceId: Int = course.inclineSummary.stringResourceId

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
