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
    val inclineSummaryStringResourceId: Int = course.inclineSummary.toUiModel().id
}
