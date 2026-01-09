package io.coursepick.coursepick.presentation.model

import androidx.annotation.StringRes
import io.coursepick.coursepick.R
import io.coursepick.coursepick.domain.course.InclineSummary

enum class InclineSummaryUiModel(
    @StringRes val id: Int,
) {
    MOSTLY_FLAT(R.string.incline_summary_flat),
    REPEATING_HILLS(R.string.incline_summary_repeating_hills),
    SOMETIMES_UPHILL(R.string.incline_summary_sometimes_uphill),
    SOMETIMES_DOWNHILL(R.string.incline_summary_sometimes_downhill),
    CONTINUOUS_UPHILL(R.string.incline_summary_continuous_uphill),
    CONTINUOUS_DOWNHILL(R.string.incline_summary_continuous_downhill),
    UNKNOWN(R.string.incline_summary_unknown),
}

fun InclineSummary.toUiModel(): InclineSummaryUiModel =
    when (this) {
        InclineSummary.MOSTLY_FLAT -> InclineSummaryUiModel.MOSTLY_FLAT
        InclineSummary.REPEATING_HILLS -> InclineSummaryUiModel.REPEATING_HILLS
        InclineSummary.SOMETIMES_UPHILL -> InclineSummaryUiModel.SOMETIMES_UPHILL
        InclineSummary.SOMETIMES_DOWNHILL -> InclineSummaryUiModel.SOMETIMES_DOWNHILL
        InclineSummary.CONTINUOUS_UPHILL -> InclineSummaryUiModel.CONTINUOUS_UPHILL
        InclineSummary.CONTINUOUS_DOWNHILL -> InclineSummaryUiModel.CONTINUOUS_DOWNHILL
        InclineSummary.UNKNOWN -> InclineSummaryUiModel.UNKNOWN
    }
