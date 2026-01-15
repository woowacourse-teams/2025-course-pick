package io.coursepick.coursepick.presentation.customcourse

import androidx.annotation.StringRes
import io.coursepick.coursepick.domain.course.Course
import io.coursepick.coursepick.domain.course.Distance
import io.coursepick.coursepick.presentation.model.toUiModel

data class CustomCourseUiModel(
    val course: Course,
    val selected: Boolean,
) {
    val distance: String? =
        course.distance?.let { distance: Distance -> distance.meter.toUiModel().value }

    val length: String =
        course.length.meter
            .toUiModel()
            .value

    @StringRes
    val inclineSummaryStringResourceId: Int = course.inclineSummary.toUiModel().id
}
