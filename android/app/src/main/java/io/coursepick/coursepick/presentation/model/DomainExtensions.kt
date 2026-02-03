package io.coursepick.coursepick.presentation.model

import androidx.annotation.StringRes
import io.coursepick.coursepick.R
import io.coursepick.coursepick.domain.course.InclineSummary
import io.coursepick.coursepick.domain.course.Meter

val Meter.formatted: String
    get() =
        if (this < 1000) {
            "${this}m"
        } else {
            String.format(
                locale = null,
                format = "%.2f",
                this.toKilometer(),
            ) + "km"
        }

val InclineSummary.stringResourceId: Int
    @StringRes get() =
        when (this) {
            InclineSummary.MOSTLY_FLAT -> R.string.incline_summary_flat
            InclineSummary.REPEATING_HILLS -> R.string.incline_summary_repeating_hills
            InclineSummary.SOMETIMES_UPHILL -> R.string.incline_summary_sometimes_uphill
            InclineSummary.SOMETIMES_DOWNHILL -> R.string.incline_summary_sometimes_downhill
            InclineSummary.CONTINUOUS_UPHILL -> R.string.incline_summary_continuous_uphill
            InclineSummary.CONTINUOUS_DOWNHILL -> R.string.incline_summary_continuous_downhill
            InclineSummary.UNKNOWN -> R.string.incline_summary_unknown
        }
